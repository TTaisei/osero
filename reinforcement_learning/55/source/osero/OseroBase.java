package source.osero;

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
        for (int i = 0; i < OseroBase.SIZE; i++){
            for (int j = 0; j < OseroBase.SIZE; j++){
                if (OseroBase.check(i, j, this.bw, this.turn)){
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Find out if there is a place for it on this board.
     * @param board Now board.
     * @param turn Now turn.
     * @return Can put?
     */
    protected static boolean checkAll(long board[], boolean turn){
        for (int i = 0; i < OseroBase.SIZE; i++){
            for (int j = 0; j < OseroBase.SIZE; j++){
                if (OseroBase.check(i, j, board, turn)){
                    return true;
                }
            }
        }

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
        long inver, place;
        board[my] += 1L << (row << OseroBase.SHIFTNUM) + col;
        for (int x = -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                if (x == 0 && y == 0) continue;
                inver = 0;
                focusRow = row + x; focusCol = col + y;
                place = 1L << (focusRow << OseroBase.SHIFTNUM) + focusCol;

                while ((board[opp] & place) != 0
                    && 0 <= x + focusRow && x + focusRow < OseroBase.SIZE
                    && 0 <= y + focusCol && y + focusCol < OseroBase.SIZE){
                    inver += place;
                    focusRow += x;
                    focusCol += y;
                    place = 1L << (focusRow << OseroBase.SHIFTNUM) + focusCol;
                    if ((board[my] & place) != 0){
                        board[my] += inver;
                        board[opp] -= inver;
                    }
                }
            }
        }
    }
}