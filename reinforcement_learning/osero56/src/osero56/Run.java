package osero56;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import myNet.costFunction.MeanSquaredError;
import myNet.layer.Dense;
import myNet.layer.Input;
import myNet.layer.Output;
import myNet.network.Network;
import myNet.nodes.activationFunction.AF;
import myNet.optimizer.Adam;
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
        	new Input(Osero.SIZE * Osero.SIZE, AF.SIGMOID),
        	new Dense(Osero.SIZE, AF.SIGMOID),
        	new Output(Osero.SIZE * Osero.SIZE, AF.SIGMOID)
		);
        Optimizer opt = new Adam(net, new MeanSquaredError());
        System.out.println(net);
        
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