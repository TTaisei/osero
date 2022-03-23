import java.util.Random;
import java.util.function.BiConsumer;
import java.io.*;
import java.util.ArrayList;
import java.util.Set;

public class Osero extends OseroBase {
    protected static Random rand = new Random(0);

    private static InputStreamReader isr = new InputStreamReader(System.in);
    protected static BufferedReader br = new BufferedReader(isr);

    protected ArrayList<BiConsumer<long[], Boolean>> playMethod = new ArrayList<BiConsumer<long[], Boolean>>();
    protected static int[] readGoal = new int[2];
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

    public static final boolean PRINT = true;
    public static final boolean NOPRINT = false;

    public Osero(){
        this.setup();
    }

    public Osero(ArrayList<BiConsumer<long[], Boolean>> playMethod){
        this.setup();
        this.playMethod = playMethod;
    }

    public Osero(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white){
        this.setup();
        this.playMethod.add(black);
        this.playMethod.add(white);
    }

    public void play(boolean printMode){
        boolean can = true, oldCan = true;

        if (printMode) this.printBoard();

        while ((can = this.checkAll()) || oldCan){
            if (can){
                this.playMethod.get(this.turn ? 1:0).accept(this.bw, this.turn);
                if (printMode) this.printBoard();
            }

            this.turn = !this.turn;
            oldCan = can;
        }

        this.countLast();
    }

    public void play(){
        boolean can = true, oldCan = true;

        while ((can = this.checkAll()) || oldCan){
            if (can){
                this.playMethod.get(this.turn ? 1:0).accept(this.bw, this.turn);
            }

            this.turn = !this.turn;
            oldCan = can;
        }
    }

    public static void human(long board[], boolean turn){
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
            }catch (Exception e){
                System.out.println("error. once choose.");
                continue;
            }
        }while (!OseroBase.check(row, col, board, turn));

        OseroBase.put(row, col, board, turn);
    }

    public static void random(long board[], boolean turn){
        int row, col;

        do {
            row = Osero.rand.nextInt(OseroBase.SIZE);
            col = Osero.rand.nextInt(OseroBase.SIZE);
        }while (!OseroBase.check(row, col, board, turn));

        OseroBase.put(row, col, board, turn);
    }

    public static void nHand(long board[], boolean turn){
        Osero.exploreAssist(board, turn, Osero::exploreNHand);
    }

    public static void nHandCustom(long board[], boolean turn){
        Osero.exploreAssist(board, turn, Osero::exploreNHandCustom);
    }

    protected static void exploreAssist(long[] board, boolean turn, FourFunction func){
        double maxScore = -100.0;
        double score;
        int[] rowAns = new int[OseroBase.SIZE << 1];
        int[] colAns = new int[OseroBase.SIZE << 1];
        int placeNum = 0;
        long[] boardLeaf = new long[2];

        int row = -1, col = 0;
        for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
            row++;
            if (row >= OseroBase.SIZE){
                row = 0;
                col++;
            }
            if (!OseroBase.check(row, col, board, turn)) continue;
            boardLeaf[0] = board[0]; boardLeaf[1] = board[1];
            OseroBase.put(row, col, boardLeaf, turn);
            score = func.getScore(
                boardLeaf,
                turn,
                !turn,
                1
            );
            if (score > maxScore){
                maxScore = score;
                placeNum = 0;
                rowAns[0] = row;
                colAns[0] = col;
            }else if (score == maxScore){
                placeNum++;
                rowAns[placeNum] = row;
                colAns[placeNum] = col;
            }
        }

        if (placeNum > 1){
            int place = rand.nextInt(placeNum+1);
            rowAns[0] = rowAns[place];
            colAns[0] = colAns[place];
        }

        OseroBase.put(rowAns[0], colAns[0], board, turn);
    }

    protected static double exploreNHand(long[] board, boolean nowTurn, boolean turn, int num){
        if (num >= Osero.readGoal[(nowTurn ? 1:0)]) return Osero.count(board, nowTurn);

        int score = 0, placeNum = 0;
        int row = -1, col = 0;
        long[] boardLeaf = new long[2];
        for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
            row++;
            if (row >= OseroBase.SIZE){
                row = 0;
                col++;
            }
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

        if (placeNum > 0) return (double)score / placeNum;
        else              return (double)Osero.count(board, turn);
    }

    protected static double exploreNHandCustom(long[] board, boolean nowTurn, boolean turn, int num){
        if (num >= Osero.readGoal[(nowTurn ? 1:0)]) return Osero.countCustom(board, nowTurn);

        double score = 0, placeNum = 0;
        int row = -1, col = 0;
        long[] boardLeaf = new long[2];
        for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
            row++;
            if (row >= OseroBase.SIZE){
                row = 0;
                col++;
            }
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

        if (placeNum > 0) return score / placeNum;
        else              return Osero.countCustom(board, turn);
    }

    protected static int count(long board[], boolean turn){
        int my, opp;
        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        long place = 1;
        int score = 0;
        while (place != 0){
            if      ((board[my] & place) != 0)  score++;
            else if ((board[opp] & place) != 0) score--;
            place = place << 1;
        }

        return score;
    }

    protected static double countCustom(long board[], boolean turn){
        int my, opp;
        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        long place = 1;
        double score = 0;
        int i = 0;
        while (place != 0){
            if      ((board[my] & place) != 0)  score += Osero.customScore[i];
            else if ((board[opp] & place) != 0) score -= Osero.customScore[i];
            place = place << 1;
            i++;
        }

        return score;
    }

    public static void nLeast(long board[], boolean turn){
        Osero.exploreAssist(board, turn, Osero::exploreNLeast);
    }

    public static void nMost(long board[], boolean turn){
        Osero.exploreAssist(board, turn, Osero::exploreNMost);
    }

    public static double exploreNLeast(long board[], boolean nowTurn, boolean turn, int num){
        int row = -1, col = 0;
        int placeNum = 0;
        double score = 0.;
        long[] boardLeaf = new long[2];

        if (num >= Osero.readGoal[(nowTurn ? 1:0)]){
            for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
                row++;
                if (row >= OseroBase.SIZE){
                    row = 0;
                    col++;
                }
                if (Osero.check(row, col, board, turn)) placeNum++;
            }

            return -(double)placeNum;
        }

        int nextNum;

        if (nowTurn == turn){
            nextNum = num + 1;
        }else{
            nextNum = num;
        }
        for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
            row++;
            if (row >= OseroBase.SIZE){
                row = 0;
                col++;
            }
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

        return placeNum > 0 ? -score / placeNum : 0;
    }

    public static double exploreNMost(long board[], boolean nowTurn, boolean turn, int num){
        int row = -1, col = 0;
        int placeNum = 0;
        double score = 0.;
        long[] boardLeaf = new long[2];

        if (nowTurn == turn && num >= Osero.readGoal[(nowTurn ? 1:0)]){
            for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
                row++;
                if (row >= OseroBase.SIZE){
                    row = 0;
                    col++;
                }
                if (Osero.check(row, col, board, turn)) placeNum++;
            }

            return (double)placeNum;
        }

        int nextNum;
        if (nowTurn != turn){
            nextNum = num + 1;
        }else{
            nextNum = num;
        }
        for (int place = 0; place < OseroBase.SIZE << OseroBase.SHIFTNUM; place++){
            row++;
            if (row >= OseroBase.SIZE){
                row = 0;
                col++;
            }
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

        return placeNum > 0 ? score / placeNum : 0;
    }

    public void setPlayMethod(ArrayList<BiConsumer<long[], Boolean>> p){
        if (p.size() != 2){
            System.out.println("playMethod size is wrong.");
            System.exit(-1);
        }
        this.playMethod = p;
    }

    public void setPlayMethod(BiConsumer<long[], Boolean> black, BiConsumer<long[], Boolean> white){
        this.playMethod.clear();
        this.playMethod.add(black);
        this.playMethod.add(white);
    }

    public void setReadGoal(int[] r){
        if (r.length != 2){
            System.out.println("readGoal size is wrong.");
            System.exit(-1);
        }
        this.readGoal = r;
    }

    public void setReadGoal(int black, int white){
        this.readGoal[0] = black;
        this.readGoal[1] = white;
    }

    public void setCustomScore(double[] c){
        if (c.length != 64){
            System.out.println("customScore's length is wrong.");
            System.exit(-1);
        }
        this.customScore = c;
    }

    public void setRandom(long seed){
        this.rand = new Random(seed);
    }
}