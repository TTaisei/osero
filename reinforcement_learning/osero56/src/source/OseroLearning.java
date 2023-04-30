package source;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import myNet.matrix.Matrix;
import myNet.optimizer.Optimizer;

/**
 * Class for learning.
 */
public class OseroLearning extends Osero {
	/** Network to calculate q value. */
	protected static Optimizer opt;

	/** Value of eta (learning rate). */
	protected static final double ETA = 0.001;
	/** Vlaue of epsilon */
    protected static final double EPSILON = 0.05;
    /** Value of gamma. */
    protected static final double GAMMA = 0.99;

	/**
     * Constructor for child class.
     */
    public OseroLearning() {
    	this.setup();
    	this.playMethods = new ArrayList<BiConsumer<long[], Boolean>>();
    }

    /**
     * Constructor for play.
     * @param playMethods Playing methods of black and white.
     */
    public OseroLearning(ArrayList<BiConsumer<long[], Boolean>> playMethods) {
        this.setup();
        this.playMethods = playMethods;
    }

    /**
     * Constructor for play.
     * @param black Playing method of black.
     * @param white Playing method of white.
     */
    public OseroLearning(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white) {
        this.setup();
        this.playMethods.clear();
        this.playMethods.add(black);
        this.playMethods.add(white);
    }

    /**
     * Set network to this network.
     * @param net Netowork to set.
     */
    public void setOpt(Optimizer opt) {
    	OseroLearning.opt = opt;
    }

    /**
     * Get result of game.
     * @return black score - white score
     */
    public int getResult(){
        return this.popCount(this.bw[0]) - this.popCount(this.bw[1]);
    }

    /**
     * Make Matrix object from long [].
     * @param l Matrix to cast.
     * @param turn Trun of game.
     * @return Casted Matrix object.
     */
    public static Matrix longArray2Matrix(long l[], boolean turn) {
    	Matrix rtn = new Matrix(new double[Osero.SIZE][Osero.SIZE]);
    	rtn.fillNum(0.);

    	int my, opp;
    	if (turn) {
    		my = 1; opp = 0;
    	} else {
    		my = 0; opp = 1;
    	}

    	long place;
    	for (int i = 0; i < Osero.SIZE; i++) {
    		for (int j = 0; j < Osero.SIZE; j++) {
    			place = 1L << (i << OseroBase.SHIFTNUM) + j;
    			if ((l[my] & place) != 0)
    				rtn.matrix[i][j] = 1.;
    			else if ((l[opp] & place) != 0)
    				rtn.matrix[i][j] = -1.;
    			else
    				;
    		}
		}

    	return rtn;
    }

    /**
     * Get quantity value.
     * @param board Board of osero.
     * @param turn Turn of game.
     * @return Quantity value.
     */
    public static Matrix getQValue(long board[], boolean turn) {
    	Matrix in = OseroLearning.longArray2Matrix(board,  turn);
    	return OseroLearning.opt.net.forward(OseroLearning.flatten(in));
    }

    /**
     * Explore place having max quantity value.
     * @param board Now board.
     * @param turn Now turn.
     * @return Numbers of row and column having max quantity value.
     */
    protected static int[] exploreMaxQValue(long board[], boolean turn){
        var rowAns = new ArrayList<Integer>();
        var colAns = new ArrayList<Integer>();
        int placeNum = 0;
        Matrix qValue = OseroLearning.getQValue(board, turn);
        double maxQValue = -100.;

        for (int i = 0; i < Osero.SIZE; i++) {
        	for (int j = 0; j < Osero.SIZE; j++) {
        		if (!Osero.check(i, j, board, turn)) continue;
        		if (qValue.matrix[0][(i << OseroBase.SHIFTNUM) + j] > maxQValue) {
        			maxQValue = qValue.matrix[0][(i << OseroBase.SHIFTNUM) + j];
                    placeNum = 0;
        			rowAns.clear();
        			rowAns.add(i);
                    colAns.clear();
                    colAns.add(j);
        		} else if (qValue.matrix[0][(i << OseroBase.SHIFTNUM) + j] == maxQValue) {
        			placeNum++;
        			rowAns.add(i);
                    colAns.add(j);
        		}
        	}
        }

        if (placeNum > 1) {
            int place = rand.nextInt(placeNum+1);
            rowAns.set(0, rowAns.get(place));
            colAns.set(0, colAns.get(place));
        }

        if (rowAns.size() == 0) {
            return null;
        }

        return new int[]{rowAns.get(0), colAns.get(0)};
    }

    /**
     * Update quantity value.
     * @param board Now board.
     * @param turn Now turn.
     * @param row The number of row.
     * @param col The number of column.
     */
    protected static void updateQValue(long[] board, boolean turn, int row, int col) {
    	Matrix x = OseroLearning.longArray2Matrix(board, turn);
        Matrix y = OseroLearning.opt.forward(OseroLearning.flatten(x));
        Matrix t = y.clone();
    	double reward = 0.;

    	if (!Osero.checkAll(board, turn) && !Osero.checkAll(board, !turn)) {
        	// if game is end.
	    	int score = Osero.count(board, turn);
	    	if      (score > 0) reward = 1.;
	    	else if (score < 0) reward = -1.;

	    	double oldQ = t.matrix[0][(row << OseroBase.SHIFTNUM) + col];
	    	t.matrix[0][(row << OseroBase.SHIFTNUM) + col]
    			+= OseroLearning.ETA * (reward - oldQ);
    	} else {
    		// if not game is end.
    		double qValueNext = 0.;
    		int qValueNum = 0;
    		for (int i = 0; i < OseroBase.SIZE; i++) {
    			for (int j = 0; j < OseroBase.SIZE; j++) {
    				if (!Osero.check(i,  j, board, !turn)) continue;

    				long boardLeaf[] = board.clone();

    				Osero.put(i,  j,  boardLeaf,  !turn);
    				var rowCol = OseroLearning.exploreMaxQValue(boardLeaf, turn);
                    if (rowCol == null) continue;

                    Osero.put(i,  j,  boardLeaf, turn);
                    qValueNext += OseroLearning.getQValue(
                    	boardLeaf, turn
                    ).matrix[0][(i << Osero.SHIFTNUM) + j];
                    qValueNum++;
    			}
    		}

    		double maxQValueNext = qValueNext / qValueNum;
    		double oldQ = t.matrix[0][(row << OseroBase.SHIFTNUM) + col];
    		t.matrix[0][(row << OseroBase.SHIFTNUM) + col]
    			+= OseroLearning.ETA * (OseroLearning.GAMMA * maxQValueNext - oldQ);
    	}

    	opt.back(OseroLearning.flatten(x), y, t);
    }

    /**
     * Playing method (q learing).
     * @param board The board.
     * @param turn The turn.
     */
    public static void learning(long board[], boolean turn) {
    	// epsilon
        if (Osero.rand.nextDouble() < OseroLearning.EPSILON) {
            Osero.random(board, turn);
            return;
        }

        // think part
        int[] rowCol = OseroLearning.exploreMaxQValue(board, turn);

        Osero.put(rowCol[0], rowCol[1], board, turn);

        // update quantity value
        OseroLearning.updateQValue(board, turn, rowCol[0], rowCol[1]);
    }

    protected static Matrix flatten(Matrix in) {
    	Matrix rtn = new Matrix(new double[1][in.row * in.col]);

    	for (int i = 0; i < Osero.SIZE; i++) {
        	for (int j = 0; j < Osero.SIZE; j++) {
        		rtn.matrix[0][(i << OseroBase.SHIFTNUM) + j] = in.matrix[i][j];
        	}
    	}

    	return rtn;
    }
}
