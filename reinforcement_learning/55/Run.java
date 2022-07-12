import java.util.function.BiConsumer;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import source.osero.Osero;
import source.osero.OseroQLearning;

public class Run {
    public static void main(String[] str) {
        final int PLAYNUM = 1000;
        final String FILENAME = "result.csv";
        final String QTABLE_FILENAME = "qTable.csv";
        BiConsumer<long[], Boolean> qLearning = OseroQLearning::qLearning;
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
        var run = new OseroQLearning();
        run.setReadGoal(1, 1);

        try (
            PrintWriter fp = new PrintWriter(FILENAME);
        ) {
            fp.write("number,win,stone\n");

            for (int i = 0; i < PLAYNUM; i++) {
                System.out.printf("\r%d/%d", i+1, PLAYNUM);
                winNum = 0; stoneNum = 0;

                for (var opp : opps) {
                    run.setPlayMethods(qLearning, opp);
                    run.play();
                    result = run.getResult();
                    winNum += result > 0 ? 1 : -1;
                    stoneNum += result;

                    run.setPlayMethods(opp, qLearning);
                    run.play();
                    result = run.getResult();
                    winNum -= result > 0 ? 1 : -1;
                    stoneNum -= result;
                }

                fp.write(String.format(
                    "%d,%d,%d\n", i, winNum, stoneNum
                ));
            }

            System.out.println();
            run.outputQTable(QTABLE_FILENAME);
        } catch (IOException e) {
            System.out.println("\nIO Exception");
        }
    }
}