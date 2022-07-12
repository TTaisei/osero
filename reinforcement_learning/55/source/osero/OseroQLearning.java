package source.osero;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Class for Q Learning.
 */
public class OseroQLearning extends Osero {
    /** Value of eta. */
    protected static final double ETA = 0.01;
    /** Vlaue of epsilon */
    protected static final double EPSILON = 0.05;
    /** Value of gamma. */
    protected static final double GAMMA = 0.99;
    /** Value of default quantity. */
    protected static final double DEFAULT_Q = 0.5;
    /** Table for quantity value. */
    protected static HashMap<Long[], Double> qTable;

    /**
     * Constructor for child class.
     */
    public OseroQLearning() {
        this.setup();
        this.playMethods = new ArrayList<BiConsumer<long[], Boolean>>();
        OseroQLearning.qTable = new HashMap<Long[], Double>();
    }

    /**
     * Constructor for play.
     * @param playMethods Playing methods of black and white.
     */
    public OseroQLearning(ArrayList<BiConsumer<long[], Boolean>> playMethods) {
        this.setup();
        this.playMethods = playMethods;
        OseroQLearning.qTable = new HashMap<Long[], Double>();
    }

    /**
     * Constructor for play.
     * @param black Playing method of black.
     * @param white Playing method of white.
     */
    public OseroQLearning(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white) {
        this.setup();
        this.playMethods.clear();
        this.playMethods.add(black);
        this.playMethods.add(white);
        OseroQLearning.qTable = new HashMap<Long[], Double>();
    }

    /**
     * Explore place having max quantity value.
     * @param board Now board.
     * @param turn Now turn.
     * @return Numbers of row and column having max quantity value.
     */
    protected static int[] exploreMaxQValue(long[] board, boolean turn){
        var validPositions = OseroQLearning.getValidPositions(board, turn);
        var rowAns = new ArrayList<Integer>();
        var colAns = new ArrayList<Integer>();
        int placeNum = 0;
        double qValue, maxQValue = -100.;

        for (var vp : validPositions) {
            qValue = OseroQLearning.getQValue(vp);
            if (qValue > maxQValue) {
                maxQValue = qValue;
                placeNum = 0;
                rowAns = new ArrayList<Integer>(){{
                    add((int)(long)vp.get(2));
                }};
                colAns = new ArrayList<Integer>(){{
                    add((int)(long)vp.get(3));
                }};
            } else if (qValue == maxQValue) {
                placeNum++;
                rowAns.add((int)(long)vp.get(2));
                colAns.add((int)(long)vp.get(3));
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
     * Playing method (q learing).
     * @param board The board.
     * @param turn The turn.
     */
    public static void qLearning(long board[], boolean turn) {
        // epsilon
        if (Osero.rand.nextDouble() < OseroQLearning.EPSILON) {
            Osero.random(board, turn);
            return;
        }

        // think part
        int[] rowCol = OseroQLearning.exploreMaxQValue(board, turn);
        
        Osero.put(rowCol[0], rowCol[1], board, turn);

        // update quantity value
        OseroQLearning.updateQValue(board, turn, rowCol[0], rowCol[1]);
    }

    /**
     * Update quantity value.
     * @param board Now board.
     * @param turn Now turn.
     * @param row The number of row.
     * @param col The number of column.
     */
    protected static void updateQValue(long[] board, boolean turn, int row, int col) {
        int my, opp;
        double newQ = 0;
        Long[] key;
        double oldQ;

        if (turn) {
            my = 1; opp = 0;
        } else {
            my = 0; opp = 1;
        }
        key = new Long[]
        {
            board[my],
            board[opp],
            (long)row,
            (long)col
        };
        oldQ = OseroQLearning.getQValue(key);

        // if game is end
        if (!Osero.checkAll(board, turn) && !Osero.checkAll(board, !turn)) {
            int score = Osero.count(board, turn);
            double reward;
            if (score > 0) {
                reward = 1.;
            } else if (score < 0) {
                reward = -1.;
            } else {
                reward = 0.;
            }

            newQ = oldQ + OseroQLearning.ETA * (reward - oldQ);
        } else {
            double qValueNext = 0;
            var validPositions = OseroQLearning.getValidPositions(board, !turn);
            if (validPositions.size() == 0) {
                return;
            }

            for (var vp : validPositions) {
                long[] boardLeaf = board.clone();

                Osero.put(
                    (int)(long)vp.get(2),
                    (int)(long)vp.get(3),
                    boardLeaf,
                    !turn
                );
                var rowCol = OseroQLearning.exploreMaxQValue(boardLeaf, turn);
                if (rowCol == null) {
                    continue;
                }
                Osero.put(rowCol[0], rowCol[1], boardLeaf, turn);
                qValueNext += getQValue(
                    new Long[]{
                        boardLeaf[my],
                        boardLeaf[opp],
                        (long)rowCol[0],
                        (long)rowCol[1]
                    }
                );
            }

            double maxQValueNext = qValueNext / validPositions.size();
            newQ = oldQ + OseroQLearning.ETA * (
                OseroQLearning.GAMMA * maxQValueNext - oldQ
            );
        }

        OseroQLearning.qTable.put(key, newQ);
    }

    /**
     * Get all valid positions and the board.
     * @return All valid positions and the board.
     */
    protected static ArrayList<ArrayList<Long>> getValidPositions(long board[], boolean turn) {
        var rtn = new ArrayList<ArrayList<Long>>();
        int my, opp;
        if (turn) {
            my = 1; opp = 0;
        } else {
            my = 0; opp = 1;
        }

        for (int i = 0; i < Osero.SIZE; i++) {
            for (int j = 0; j < Osero.SIZE; j++) {
                if (Osero.check(i, j, board, turn)) {
                    var rtnElement = new ArrayList<Long>();
                    rtnElement.add(board[my]);
                    rtnElement.add(board[opp]);
                    rtnElement.add((long)i);
                    rtnElement.add((long)j);

                    rtn.add(rtnElement);
                }
            }
        }

        return rtn;
    }

    /**
     * Get quantity value.
     * @param pos Valid position.
     * @return Quantity value.
     */
    protected static double getQValue(ArrayList<Long> pos) {
        Double rtn = OseroQLearning.qTable.get(
            new Long[]{pos.get(0), pos.get(1), pos.get(2), pos.get(3)}
        );
        return rtn == null    ? OseroQLearning.DEFAULT_Q
            /* rtn != null */ : rtn;
    }

    /**
     * Get quantity value.
     * @param pos Valid position.
     * @return Quantity value.
     */
    protected static double getQValue(Long[] pos) {
        Double rtn = OseroQLearning.qTable.get(pos);
        return rtn == null    ? OseroQLearning.DEFAULT_Q
            /* rtn != null */ : rtn;
    }

    /**
     * Get result of game.
     * @return black score - white score
     */
    public int getResult(){
        return this.popCount(this.bw[0]) - this.popCount(this.bw[1]);
    }

    /**
     * Output this quantity table to csv file.
     * @param fileName File name to output.
     * @return Success?
     */
    public boolean outputQTable(String fileName) {
        try (
            PrintWriter fp = new PrintWriter(fileName);
        ) {
            fp.write("black,white,row,col,qValue\n");
            for (var key: OseroQLearning.qTable.keySet()) {
                fp.write(String.format(
                    "%d,%d,%d,%d,%f\n",
                    key[0],
                    key[1],
                    key[2],
                    key[3],
                    OseroQLearning.getQValue(key)
                ));
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}