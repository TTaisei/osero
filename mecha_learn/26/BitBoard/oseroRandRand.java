package BitBoard;

import java.util.Random;

public class oseroRandRand extends BitBoard{
    Random rand = new Random();

    public void play(){
        boolean can = true;
        boolean old_can = true;

        printb();

        while ((can = check_all()) || old_can){
            if (can){
                random();
                printb();
            }
            turn = !turn;
            old_can = can;
        }

        count_last();
    }
   
    public void random(){
        int line, col;

        do{
            line = rand.nextInt(8);
            col = rand.nextInt(8);
        }while (!check(line, col, bw, turn));

        put(line, col, bw, turn);
    }
}