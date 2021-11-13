package BitBoard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class oseroHumanHuman extends BitBoard{
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);

    public void play(){
        boolean can = true;
        boolean old_can = true;

        printb();

        while ((can = check_all()) || old_can){
            if (can){
                if (turn){
                    System.out.println("whtie turn.");
                }else{
                    System.out.println("black turn.");
                }
                human();
                printb();
            }else{
                System.out.print("not place. once ");
            }
            turn = !turn;
            old_can = can;
        }

        count_last();
    }

    public void human(){
        int line = -1, col = -1;
        String line_s, col_s;

        while (true){
            try{
                System.out.print("line: ");
                line_s = br.readLine();
                System.out.print("col: ");
                col_s = br.readLine();

                line = Integer.parseInt(line_s);
                col = Integer.parseInt(col_s);
                line--; col--;
            }catch (Exception e){
                System.out.println("error. once choose.");
                continue;
            }

            if (check(line, col, bw, turn)){
                break;
            }else{
                System.out.println("can't put that place. once choose.");
            }
        }

        put(line, col, bw, turn);
    }
}