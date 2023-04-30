package source;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.BiConsumer;

/**
 * Class for play osero.
 */
public class Osero extends OseroBase {
    protected static Random rand = new Random(0);

    private static InputStreamReader isr = new InputStreamReader(System.in);
    protected static BufferedReader br = new BufferedReader(isr);

    protected ArrayList<BiConsumer<long[], Boolean>> playMethods
        = new ArrayList<BiConsumer<long[], Boolean>>();
    protected static int[] readGoals = new int[2];
    protected static double[] customScore = {
         1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0,
        -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
         0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
         0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
         0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
         0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
        -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
         1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0
    };

    /**
     * Constructor for child class.
     */
    protected Osero() {
        this.setup();
    }

    /**
     * Constructor for play.
     * @param playMethods Playing methods of black and white.
     */
    public Osero(ArrayList<BiConsumer<long[], Boolean>> playMethods) {
        this.setup();
        this.playMethods = playMethods;
    }

    /**
     * Constructor for play.
     * @param black Playing method of black.
     * @param white Playing method of white.
     */
    public Osero(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white) {
        this.setup();
        this.playMethods.clear();
        this.playMethods.add(black);
        this.playMethods.add(white);
    }

    /**
     * Play osero.
     * @param printMode is print?
     */
    public void play(boolean printMode) {
        boolean can = true, oldCan = true;
        this.setup();

        if (printMode) this.printBoard();

        while ((can = this.checkAll()) || oldCan) {
            if (can) {
                this.playMethods.get(this.turn ? 1:0).accept(this.bw, this.turn);
                if (printMode) this.printBoard();
            }

            this.turn = !this.turn;
            oldCan = can;
        }

        this.countLast();
    }

    /**
     * Play no printing.
     */
    public void play() {
        boolean can = true, oldCan = true;
        this.setup();

        while ((can = this.checkAll()) || oldCan) {
            if (can) {
                this.playMethods.get(this.turn ? 1:0).accept(this.bw, this.turn);
            }

            this.turn = !this.turn;
            oldCan = can;
        }
    }

    /**
     * Playing method (human).
     * @param board The board.
     * @param turn The turn.
     */
    public static void human(long board[], boolean turn) {
        int row = 0, col = 0;
        String rowS, colS;

        do {
            try{
                System.out.print("row: ");
                rowS = br.readLine();
                System.out.print("col: ");
                colS = br.readLine();

                row = Integer.parseInt(rowS);
                col = Integer.parseInt(colS);
                row--; col--;
            }catch (Exception e) {
                System.out.println("error. once choose.");
                continue;
            }
        }while (!OseroBase.check(row, col, board, turn));

        OseroBase.put(row, col, board, turn);
    }

    /**
     * Playing method (random).
     * @param board The board.
     * @param turn The turn.
     */
    public static void random(long board[], boolean turn) {
        int row, col;

        do {
            row = Osero.rand.nextInt(OseroBase.SIZE);
            col = Osero.rand.nextInt(OseroBase.SIZE);
        }while (!OseroBase.check(row, col, board, turn));

        OseroBase.put(row, col, board, turn);
    }

    /**
     * Playing method (n hand).
     * @param board The board.
     * @param turn The turn.
     */
    public static void nHand(long board[], boolean turn) {
        Osero.exploreAssist(board, turn, Osero::exploreNHand);
    }

    /**
     * Playing method (n hand custom).
     * @param board The board.
     * @param turn The turn.
     */
    public static void nHandCustom(long board[], boolean turn) {
        Osero.exploreAssist(board, turn, Osero::exploreNHandCustom);
    }

    /**
     * Assist to explore the board.
     */
    protected static void exploreAssist(long[] board, boolean turn, FourFunction func) {
        double maxScore = -100.0;
        double score;
        int[] rowAns = new int[OseroBase.SIZE << 1];
        int[] colAns = new int[OseroBase.SIZE << 1];
        int placeNum = 0;
        long[] boardLeaf = new long[2];

        for (int row = 0; row < OseroBase.SIZE; row++) {
            for (int col = 0; col < OseroBase.SIZE; col++) {
                if (!OseroBase.check(row, col, board, turn)) continue;
                boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
                OseroBase.put(row, col, boardLeaf, turn);
                score = func.getScore(
                    boardLeaf,
                    turn,
                    !turn,
                    1
                );
                if (score > maxScore) {
                    maxScore = score;
                    placeNum = 0;
                    rowAns[0] = row;
                    colAns[0] = col;
                }else if (score == maxScore) {
                    placeNum++;
                    rowAns[placeNum] = row;
                    colAns[placeNum] = col;
                }
            }
        }

        if (placeNum > 1) {
            int place = rand.nextInt(placeNum+1);
            rowAns[0] = rowAns[place];
            colAns[0] = colAns[place];
        }

        OseroBase.put(rowAns[0], colAns[0], board, turn);
    }

    /**
     * Explore (n hand).
     * @param board The borad.
     * @param nowTurn The turn.
     * @param turn The now exploring turn.
     * @param num Number of now exploring.
     * @return score of the place.
     */
    protected static double exploreNHand(long[] board, boolean nowTurn, boolean turn, int num) {
        if (num >= Osero.readGoals[(nowTurn ? 1:0)]) return Osero.count(board, nowTurn);

        int score = 0, placeNum = 0;
        long[] boardLeaf = new long[2];
        for (int row = 0; row < OseroBase.SIZE; row++) {
            for (int col = 0; col < OseroBase.SIZE; col++) {
                if (!OseroBase.check(row, col, board, turn)) continue;
                placeNum += 1;
                boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
                OseroBase.put(row, col, boardLeaf, turn);
                score += Osero.exploreNHand(
                    boardLeaf,
                    nowTurn,
                    !turn,
                    num + 1
                );
            }
        }

        if (placeNum > 0) return (double)score / placeNum;
        else              return (double)Osero.count(board, turn);
    }

    /**
     * Explore (n hand custom).
     * @param board The borad.
     * @param nowTurn The turn.
     * @param turn The now exploring turn.
     * @param num Number of now exploring.
     * @return score of the place.
     */
    protected static double exploreNHandCustom(long[] board, boolean nowTurn, boolean turn, int num) {
        if (num >= Osero.readGoals[(nowTurn ? 1:0)]) return Osero.countCustom(board, nowTurn);

        double score = 0, placeNum = 0;
        long[] boardLeaf = new long[2];
        for (int row = 0; row < OseroBase.SIZE; row++) {
            for (int col = 0; col < OseroBase.SIZE; col++) {
                if (!OseroBase.check(row, col, board, turn)) continue;
                placeNum += 1;
                boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
                OseroBase.put(row, col, boardLeaf, turn);
                score += Osero.exploreNHandCustom(
                    boardLeaf,
                    nowTurn,
                    !turn,
                    num + 1
                );
            }
        }

        if (placeNum > 0) return score / placeNum;
        else              return Osero.countCustom(board, turn);
    }

    /**
     * Count score.
     * @param board The board.
     * @param turn The turn.
     * @return score.
     */
    protected static int count(long board[], boolean turn) {
        int my, opp;
        if (turn) {
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        long place = 1;
        int score = 0;
        while (place != 0) {
            if      ((board[my] & place) != 0)  score++;
            else if ((board[opp] & place) != 0) score--;
            place = place << 1;
        }

        return score;
    }

    /**
     * Count score be based on this customScore.
     * @param board The board.
     * @param turn The turn.
     * @return score.
     */
    protected static double countCustom(long board[], boolean turn) {
        int my, opp;
        if (turn) {
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        long place = 1;
        double score = 0;
        int i = 0;
        while (place != 0) {
            if      ((board[my] & place) != 0)  score += Osero.customScore[i];
            else if ((board[opp] & place) != 0) score -= Osero.customScore[i];
            place = place << 1;
            i++;
        }

        return score;
    }

    /**
     * Playing method (n least).
     * @param board The board.
     * @param turn The turn.
     */
    public static void nLeast(long board[], boolean turn) {
        Osero.exploreAssist(board, turn, Osero::exploreNLeast);
    }

    /**
     * Playing method (n most).
     * @param board The board.
     * @param turn The turn.
     */
    public static void nMost(long board[], boolean turn) {
        Osero.exploreAssist(board, turn, Osero::exploreNMost);
    }

    /**
     * Explore (n least).
     * @param board The borad.
     * @param nowTurn The turn.
     * @param turn The now exploring turn.
     * @param num Number of now exploring.
     * @return score of the place.
     */
    public static double exploreNLeast(long board[], boolean nowTurn, boolean turn, int num) {
        int placeNum = 0;
        double score = 0.;
        long[] boardLeaf = new long[2];

        if (num >= Osero.readGoals[(nowTurn ? 1:0)]) {
            for (int row = 0; row < OseroBase.SIZE; row++) {
                for (int col = 0; col < OseroBase.SIZE; col++) {
                    if (Osero.check(row, col, board, turn)) {
                        placeNum++;
                    }
                }
            }

            return -(double)placeNum;
        }

        int nextNum;

        if (nowTurn == turn) {
            nextNum = num + 1;
        }else{
            nextNum = num;
        }
        for (int row = 0; row < OseroBase.SIZE; row++) {
            for (int col = 0; col < OseroBase.SIZE; col++) {
                if (!Osero.check(row, col, board, turn)) continue;
                boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
                placeNum++;
                score += Osero.exploreNLeast(
                    boardLeaf,
                    nowTurn,
                    !turn,
                    nextNum
                );
            }
        }

        return placeNum > 0 ? -score / placeNum : 0;
    }

    /**
     * Explore (n most).
     * @param board The borad.
     * @param nowTurn The turn.
     * @param turn The now exploring turn.
     * @param num Number of now exploring.
     * @return score of the place.
     */
    public static double exploreNMost(long board[], boolean nowTurn, boolean turn, int num) {
        int placeNum = 0;
        double score = 0.;
        long[] boardLeaf = new long[2];

        if (nowTurn == turn && num >= Osero.readGoals[(nowTurn ? 1:0)]) {
            for (int row = 0; row < OseroBase.SIZE; row++) {
                for (int col = 0; col < OseroBase.SIZE; col++) {
                    if (Osero.check(row, col, board, turn)) {
                        placeNum++;
                    }
                }
            }

            return (double)placeNum;
        }

        int nextNum;
        if (nowTurn != turn) {
            nextNum = num + 1;
        }else{
            nextNum = num;
        }
        for (int row = 0; row < OseroBase.SIZE; row++) {
            for (int col = 0; col < OseroBase.SIZE; col++) {
                if (!Osero.check(row, col, board, turn)) continue;
                boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
                placeNum++;
                score += Osero.exploreNLeast(
                    boardLeaf,
                    nowTurn,
                    !turn,
                    nextNum
                );
            }
        }

        return placeNum > 0 ? score / placeNum : 0;
    }

    /**
     * Set playing methods.
     * @param p Playing methods of black and white.
     */
    public void setPlayMethods(ArrayList<BiConsumer<long[], Boolean>> p) {
        if (p.size() != 2) {
            System.out.println("playMethods size is wrong.");
            System.exit(-1);
        }
        this.playMethods = p;
    }

    /**
     * Set playing methods.
     * @param black Playing method of black.
     * @param white Playing method of white.
     */
    public void setPlayMethods(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white) {
        this.playMethods.clear();
        this.playMethods.add(black);
        this.playMethods.add(white);
    }

    /**
     * Set read goals.
     * @param r Read goals of black and white.
     */
    public void setReadGoal(int[] r) {
        if (r.length != 2) {
            System.out.println("readGoals size is wrong.");
            System.exit(-1);
        }
        Osero.readGoals = r;
    }

    /**
     * Set read goals.
     * @param black Read goal of black.
     * @param white Read goal of white.
     */
    public void setReadGoal(int black, int white) {
        Osero.readGoals[0] = black;
        Osero.readGoals[1] = white;
    }

    /**
     * Set custom score.
     * @param c Custom score.
     */
    public void setCustomScore(double[] c) {
        if (c.length != 64) {
            System.out.println("customScore's length is wrong.");
            System.exit(-1);
        }
        Osero.customScore = c;
    }

    /**
     * Set Number of random seed.
     * @param seed Number of seed.
     */
    public void setRandom(long seed) {
        Osero.rand = new Random(seed);
    }
}
