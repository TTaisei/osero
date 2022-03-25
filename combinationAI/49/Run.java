import java.util.function.BiConsumer;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintWriter;

public class Run {
    // public static void main(String[] str){
    //     Osero run = new OseroAI(OseroAI::oseroAI, OseroAI::oseroAI, "oseroAI.net");
    //     run.play(Osero.PRINT);
    // }

    public static void main(String[] str){
        ArrayList<BiConsumer<long[], Boolean>> playMethod;
        playMethod = new ArrayList<BiConsumer<long[], Boolean>>();
        ArrayList<String> playMethodName;
        playMethodName = new ArrayList<String>();
        ArrayList<Integer> readGoals;
        readGoals = new ArrayList<Integer>();
        
        playMethod.add(Osero::random);	playMethodName.add("random");	readGoals.add(1);
        playMethod.add(Osero::random);	playMethodName.add("random");	readGoals.add(2);
        playMethod.add(Osero::nHand);	playMethodName.add("nHand");	readGoals.add(1);
        playMethod.add(Osero::nHand);	playMethodName.add("nHand");	readGoals.add(2);
        playMethod.add(Osero::nHandCustom);	playMethodName.add("nHandCustom");	readGoals.add(1);
        playMethod.add(Osero::nHandCustom);	playMethodName.add("nHandCustom");	readGoals.add(2);
        playMethod.add(Osero::nLeast);	playMethodName.add("nLeast");	readGoals.add(1);
        playMethod.add(Osero::nLeast);	playMethodName.add("nLeast");	readGoals.add(2);
        playMethod.add(Osero::nMost);	playMethodName.add("nMost");	readGoals.add(1);
        playMethod.add(Osero::nMost);	playMethodName.add("nMost");	readGoals.add(2);
        playMethod.add(OseroAI::oseroAI);	playMethodName.add("oseroAI");	readGoals.add(1);
        playMethod.add(OseroAI::oseroAI);	playMethodName.add("oseroAI");	readGoals.add(2);
        
        OseroAI run = new OseroAI(OseroAI::oseroAI, OseroAI::oseroAI, "oseroAI.net");

        try(
            PrintWriter fp = new PrintWriter("data.csv");
        ){
            int[] result;
            fp.write("black_method,white_method,black_score,white_score,black_read_goal,white_read_goal,seed\n");

            for (int i = 0; i < playMethod.size(); i++){
                System.out.printf("\r%d/%d", i+1, playMethod.size());
                for (int j = 0; j < playMethod.size(); j++){
                    run.setPlayMethod(playMethod.get(i), playMethod.get(j));
                    run.setReadGoal(readGoals.get(i), readGoals.get(j));
                    for (int k = 0; k < 100; k++){
                        run.setRandom(k);
                        run.setup();
                        result = run.playAndGetResult();

                        fp.printf(
                            "%s,%s,%d,%d,%d,%d,%d\n",
                            playMethodName.get(i),
                            playMethodName.get(j),
                            result[0],
                            result[1],
                            readGoals.get(i),
                            readGoals.get(j),
                            k
                        );
                    }
                }
            }

            System.out.println();
        }catch (IOException e){
            System.out.println("IO Exception");
            System.exit(-1);
        }
    }
}