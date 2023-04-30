https://qiita.com/tt_and_tk/items/068f2afde6db637e189f

[前回](https://qiita.com/tt_and_tk/items/065c7ef34bdbd7fc04b6)

# 今回の目標
[前回](https://qiita.com/tt_and_tk/items/065c7ef34bdbd7fc04b6)からの続きで，深層強化学習をやる！
# ここから本編
さて，[前回](https://qiita.com/tt_and_tk/items/065c7ef34bdbd7fc04b6)はQ学習を行いましたが，ほとんどの値が初期値から更新されず失敗に終わりました．
原因としては深層強化学習の存在意義としても言われている通り，「状態」が無限大に存在するボードゲームに対して，表を用いたQ学習では対処しきれないためですね．
そこで畳み込み層などを用いた深層強化学習が提案されているわけですが，ここではひとまず全結合層のみの深層強化学習に挑んでみたいと思います．
なお，ここで紹介しているプログラム内容は，深層学習をしていること以外は[前回](https://qiita.com/tt_and_tk/items/065c7ef34bdbd7fc04b6)とほぼ同じです．．．
## 使用ツール等
ちなみにmyNetは深層学習のための自作ライブラリです．

- Eclipse 2023-03 (4.27.0)
- Java17
- myNet 2.0.0
## フォルダ構成
Eclipse初使用でよくわからなかったのもあって，myNetを同じプロジェクト内にコピペしています．

```
├─.settings
├─bin
│  ├─myNet
│  │  ├─costFunction
│  │  ├─layer
│  │  ├─matrix
│  │  ├─network
│  │  ├─nodes
│  │  │  └─activationFunction
│  │  ├─optimizer
│  │  └─version
│  ├─osero56
│  └─source
└─src
    ├─myNet
    │  ├─costFunction
    │  ├─layer
    │  ├─matrix
    │  ├─network
    │  ├─nodes
    │  │  └─activationFunction
    │  ├─optimizer
    │  └─version
    ├─osero56
    └─source
```

## FourFunction
探索に用いる関数を格納するクラス．

```java
package source;

@FunctionalInterface
public interface FourFunction {
    public abstract double getScore(long board[], boolean nowTurn, boolean turn, int num);
}
```

## OseroBase
オセロをするための基底クラス．
長いのでdetailsにします．

<details>

```java
package source;

/**
 * Basic osero class.
 */
public class OseroBase {
    /** Size of board. */
    public static final int SIZE = 8;
    /** Constant number for calculation. */
    protected static final int SHIFTNUM = 3;
    /** BitBoard. */
    protected long bw[] = new long[2];
    /** turn. */
    protected boolean turn = false;

    /**
     * Constructor for child class.
     */
    protected OseroBase(){
        ;
    }

    /**
     * Find out if there is a place for it on this board.
     * @return Can put?
     */
    protected boolean checkAll(){
        for (int i = 0; i < OseroBase.SIZE; i++)
            for (int j = 0; j < OseroBase.SIZE; j++)
                if (OseroBase.check(i, j, this.bw, this.turn))
                    return true;


        return false;
    }

    /**
     * Find out if there is a place for it on this board.
     * @param board Now board.
     * @param turn Now turn.
     * @return Can put?
     */
    protected static boolean checkAll(long board[], boolean turn){
        for (int i = 0; i < OseroBase.SIZE; i++)
            for (int j = 0; j < OseroBase.SIZE; j++)
                if (OseroBase.check(i, j, board, turn))
                    return true;

        return false;
    }

    /**
     * Count standing bits from number.
     * @return Number of standing bits.
     */
    protected int popCount(long now){
        now = now - ((now >> 1) & 0x5555555555555555L);
        now = (now & 0x3333333333333333L) + ((now >> 2) & 0x3333333333333333L);
        now = (now + (now >> 4)) & 0x0f0f0f0f0f0f0f0fL;
        now = now + (now >> 8);
        now = now + (now >> 16);
        now = now + (now >> 32);

        return (int)now & 0x7f;
    }

    /**
     * Print this board.
     */
    public void printBoard(){
        int num = 0;
        long place = 1;

        System.out.print("\n  ");
        for (int i = 0; i < 8; i++) System.out.printf(" %d ", i + 1);

        System.out.println("\n -------------------------");
        while (place != 0){
            if (num % 8 == 0) System.out.printf("%d", (num >> 3) + 1);
            if ((this.bw[0] & place) != 0) System.out.print("|@ ");
            else if ((this.bw[1] & place) != 0) System.out.print("|O ");
            else System.out.print("|  ");
            if (num % 8 == 7) System.out.println("|\n -------------------------");
            num++;
            place = place << 1;
        }
    }

    /**
     * Initialize this board.
     */
    public void setup(){
        this.turn = false;
        this.bw[0] = 0x810000000L;
        this.bw[1] = 0x1008000000L;
    }

    /**
     * Count result.
     */
    public void countLast(){
        int black = this.popCount(this.bw[0]);
        int white = this.popCount(this.bw[1]);

        System.out.printf("black: %d, white: %d\n", black, white);

        if (black > white){
            System.out.println("black win!");
        }else if (white > black){
            System.out.println("white win!");
        }else{
            System.out.println("draw!");
        }
    }

    /**
     * Find out if it can be placed in that location.
     * @param row Number of row.
     * @param col Number of column.
     * @param board The board.
     * @param turn The turn.
     * @return Can put?
     */
    protected static boolean check(int row, int col, long[] board, boolean turn){
        long place = 1L << (row << OseroBase.SHIFTNUM) + col;
        if ((board[0] & place) != 0) return false;
        if ((board[1] & place) != 0) return false;

        int my, opp;
        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        int focusRow, focusCol;
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                if (x == 0 && y == 0) continue;
                focusRow = row + x; focusCol = col + y;
                place = 1L << (focusRow << OseroBase.SHIFTNUM) + focusCol;

                while ((board[opp] & place) != 0
                    && 0 <= x + focusRow && x + focusRow < OseroBase.SIZE
                    && 0 <= y + focusCol && y + focusCol < OseroBase.SIZE){
                    focusRow += x;
                    focusCol += y;
                    place = 1L << (focusRow << OseroBase.SHIFTNUM) + focusCol;
                    if ((board[my] & place) != 0){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Put the board.
     * @param row Number of row.
     * @param col Number of column.
     * @param board The board.
     * @param turn The turn.
     */
    protected static void put(int row, int col, long[] board, boolean turn){
        int my, opp;
        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        int focusRow, focusCol;
        long inverse, place;
        board[my] += 1L << (row << OseroBase.SHIFTNUM) + col;
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                if (x == 0 && y == 0) continue;
                inverse = 0;
                focusRow = row + x; focusCol = col + y;
                place = 1L << (focusRow << OseroBase.SHIFTNUM) + focusCol;

                while ((board[opp] & place) != 0
                    && 0 <= x + focusRow && x + focusRow < OseroBase.SIZE
                    && 0 <= y + focusCol && y + focusCol < OseroBase.SIZE){
                    inverse += place;
                    focusRow += x;
                    focusCol += y;
                    place = 1L << (focusRow << OseroBase.SHIFTNUM) + focusCol;
                    if ((board[my] & place) != 0){
                        board[my] += inverse;
                        board[opp] -= inverse;
                        break;
                    }
                }
            }
        }
    }
}
```

</details>

## Osero
オセロ用クラス．
こちらも長いので詳細に．

<details>

```java
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
```

</details>

## OseroLearning
学習を行うためのクラス．本記事のメインディッシュです．
### コンストラクタ等
深層強化学習では，表形式のQ学習と異なり，入力は状態のみです．
ネットワークの出力が各行動のQ値となり，それが最大になる所を選べばいいようです．

```java
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
```

### setOpt
myNetでは最適化関数内にネットワークが保存されるので，重み更新などを考慮するとネットワーク変数を保持するよりもそれを持つOptimizerオブジェクトを保持する方がいろいろと便利です．
そんなOptimizerオブジェクトのsetを行うメソッドです．

```java
    /**
     * Set network to this network.
     * @param net Netowork to set.
     */
    public void setOpt(Optimizer opt) {
    	OseroLearning.opt = opt;
    }
```

### getResult
その時点でのゲーム結果を受け取るメソッド．

```java
    /**
     * Get result of game.
     * @return black score - white score
     */
    public int getResult(){
        return this.popCount(this.bw[0]) - this.popCount(this.bw[1]);
    }
```

### longArray2Matrix
オセロの盤面はBitBoardで保存してありますが，これをネットワークに入れるにはmyNetで扱っているデータ形式であるMatrix型に変換しなければなりません．それを行うのがこのメソッドです．

```java
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
```

### flatten
今回のネットワークは畳み込みを行わず，一次元配列を入力とする普通の全結合そうなので8x8のオセロ盤面は都合が悪いです．
そこで，オセロ盤面を平滑化するメソッドを作りました．

```java
    protected static Matrix flatten(Matrix in) {
    	Matrix rtn = new Matrix(new double[1][in.row * in.col]);

    	for (int i = 0; i < Osero.SIZE; i++) {
        	for (int j = 0; j < Osero.SIZE; j++) {
        		rtn.matrix[0][(i << OseroBase.SHIFTNUM) + j] = in.matrix[i][j];
        	}
    	}

    	return rtn;
    }
```

### getQValue
Q値を得るメソッドです．

```java
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
```

### exploreMaxQValue
Q値が最大になる位置を探索し，その位置を返すメソッド．
万一，そのような個所が複数あれば，ランダムで一か所を選びます．

```java
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
```

### updateQValue
Q値を算出するネットワークの重みを更新するメソッド．
考えていることは[前回作成した似たようなメソッド](https://qiita.com/tt_and_tk/items/065c7ef34bdbd7fc04b6#updateqvalue)と大差ないので説明はそちらに譲ります．

```java
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
```

### learning
ネットワークを更新しつつ，ネットワークの算出結果に従って石を置いていくメソッド．
一定の確率でランダムに置く．

```java
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
```

## Run
上述したlearningメソッドを，先手後手を交代しながらrandom，nHand，nHandCustom，nLeast，nMostと1000回戦わせ，その勝率の推移を出力させます．

```java
package osero56;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import myNet.costFunction.MeanSquaredError;
import myNet.layer.Input;
import myNet.layer.Output;
import myNet.network.Network;
import myNet.nodes.activationFunction.AF;
import myNet.optimizer.SGD;
import myNet.optimizer.Optimizer;
import source.Osero;
import source.OseroLearning;

public class Run {
    public static void main(String[] str) {
        final int PLAYNUM = 1000;
        final String FILENAME = "result.csv";
        BiConsumer<long[], Boolean> learning = OseroLearning::learning;
        ArrayList<BiConsumer<long[], Boolean>> opps = new ArrayList<>(){
            {
                add(Osero::random);
                add(Osero::nHand);
                add(Osero::nHandCustom);
                add(Osero::nLeast);
                add(Osero::nMost);
            }
        };
        int winNum, stoneNum, result;
        var run = new OseroLearning();
        Network net = new Network(
        	Osero.SIZE * Osero.SIZE,
        	new Input(Osero.SIZE * Osero.SIZE, AF.RELU),
       	    new Dense(Osero.SIZE * Osero.SIZE, AF.RELU),
        	new Output(Osero.SIZE * Osero.SIZE, AF.SIGMOID)
		);
        Optimizer opt = new SGD(net, new MeanSquaredError());

        run.setReadGoal(1, 1);
        run.setOpt(opt);

        try (
            PrintWriter fp = new PrintWriter(FILENAME);
        ) {
            fp.write("number,win,stone\n");

            for (int i = 0; i < PLAYNUM; i++) {
            	if (i % 100 == 0) System.out.printf("%d\n", i);
                winNum = 0; stoneNum = 0;

                for (var opp : opps) {
                    run.setPlayMethods(learning, opp);
                    run.play();
                    result = run.getResult();
                    winNum += result > 0 ? 1 : -1;
                    stoneNum += result;

                    run.setPlayMethods(opp, learning);
                    run.play();
                    result = run.getResult();
                    winNum -= result > 0 ? 1 : -1;
                    stoneNum -= result;
                }

                fp.write(String.format(
                    "%d,%d,%d\n", i, winNum, stoneNum
                ));
            }
        } catch (IOException e) {
            System.out.println("\nIO Exception");
        }

        System.out.println("end!");
    }
}
```

## 実行結果
ここまで長々と呼んでくださった皆さん，申し訳ありません．
一瞬で勾配爆発しました．

## 考察
最初に試したネットワークが以下の通りです．

```
input: 65
--------------------------------
Input	nodes num: 64
--------------------------------
Dense	nodes num: 64
--------------------------------
Output	nodes num: 64
--------------------------------
```

その後，以下のネットワークも試しました．

```
input: 65
--------------------------------
Input	nodes num: 64
--------------------------------
Output	nodes num: 64
--------------------------------
```

```
input: 65
--------------------------------
Input	nodes num: 8
--------------------------------
Output	nodes num: 64
--------------------------------

```

しかし悉く爆発しました．
最初は`SGD`にしていた最適化関数を`Adam`に変更しても同様の結果でした．


# フルバージョン
# 次回は

https://qiita.com/tt_and_tk/items/068f2afde6db637e189f
