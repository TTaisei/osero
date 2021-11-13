package BitBoard;

public class BitBoard {
    public long bw[] = new long[2];
    public boolean turn = false;

    public BitBoard(){
        setup();
    }

    public boolean check_all(){
        int i, j;

        for (i = 0; i < 8; i++){
            for (j = 0; j < 8; j++){
                if (check(i, j, bw, turn)) return true;
            }
        }

        return false;
    }

    public boolean check(int line, int col, long[] now, boolean turn){
        long place = 1;
        place = place << (line << 3) + col;

        if ((now[0] & place) != 0) return false;
        if ((now[1] & place) != 0) return false;

        int line_x, col_y;
        int x, y;
        int my, opp;

        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        for (x = -1; x <= 1; x++){
            for (y = -1; y <= 1; y++){
                if (x == 0 && y == 0){
                    continue;
                }
                line_x = line + x;
                col_y = col + y;
                place = (long)1 << (line_x << 3) + col_y;
                while ((now[opp] & place) != 0
                       && 0 <= x + line_x && x + line_x < 8
                       && 0 <= y + col_y && y + col_y < 8){
                    line_x += x;
                    col_y += y;
                    place = (long)1 << (line_x << 3) + col_y;
                    if ((now[my] & place) != 0){
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void put(int line, int col, long[] now, boolean turn){
        int x, y;
        int line_x, col_y;
        int my, opp;
        long inver, place;

        if (turn){
            my = 1; opp = 0;
        }else{
            my = 0; opp = 1;
        }

        now[my] += (long)1 << (line << 3) + col;

        for (x = -1; x <= 1; x++){
            for (y = -1; y <= 1; y++){
                if (x == 0 && y == 0){
                    continue;
                }
                inver = 0;
                line_x = line + x;
                col_y = col + y;
                place = (long)1 << (line_x << 3) + col_y;
                while ((now[opp] & place) != 0
                       && 0 <= x + line_x && x + line_x < 8
                       && 0 <= y + col_y && y + col_y < 8){
                    inver += place;
                    line_x += x;
                    col_y += y;
                    place = (long)1 << (line_x << 3) + col_y;
                }
                if ((now[my] & place) != 0){
                    now[my] += inver;
                    now[opp] -= inver;
                }
            }
        }
    }

    public void count_last(){
        int black = popcount(bw[0]);
        int white = popcount(bw[1]);

        System.out.printf("black: %d, white: %d\n", black, white);

        if (black > white){
            System.out.println("black win!");
        }else if (white > black){
            System.out.println("white win!");
        }else{
            System.out.println("draw!");
        }
    }

    public int popcount(long now){
        now = now - ((now >> 1) & doubling(0x55555555));
        now = (now & doubling(0x33333333)) + ((now >> 2) & doubling(0x33333333));
        now = (now + (now >> 4)) & doubling(0x0f0f0f0f);
        now = now + (now >> 8);
        now = now + (now >> 16);
        now = now + (now >> 32);
    
        return (int)now & 0x7f;
    }

    public void printb(){
        int num = 0;
        long place = 1;

        System.out.print("\n  ");
        for (int i = 0; i < 8; i++) System.out.printf(" %d ", i + 1);

        System.out.println("\n -------------------------");
        while (place != 0){
            if (num % 8 == 0) System.out.printf("%d", (num >> 3) + 1);
            if ((bw[0] & place) != 0) System.out.print("|@ ");
            else if ((bw[1] & place) != 0) System.out.print("|O ");
            else System.out.print("|  ");
            if (num % 8 == 7) System.out.println("|\n -------------------------");
            num++;
            place = place << 1;
        }
    }

    public void setup(){
        turn = false;
        bw[0] = shift32(0x8) + 0x10000000;
        bw[1] = shift32(0x10) + 0x08000000;
    }

    public long doubling(int num){
        return ((long)num << 32) + num;
    }

    public long shift32(int num){
        return (long)num << 32;
    }
}