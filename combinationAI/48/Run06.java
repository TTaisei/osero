import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.MyNet.matrix.*;
import org.MyNet.network.*;
import org.MyNet.layer.*;
import org.MyNet.nodes.activationFunction.*;
import org.MyNet.optimizer.*;
import org.MyNet.costFunction.*;

public class Run06 {
    public static void main(String str[]){
        ArrayList<BiConsumer<long[], Boolean>> playMethod = new ArrayList<BiConsumer<long[], Boolean>>();
        playMethod.add(Osero::random);
        playMethod.add(Osero::nHand);
        playMethod.add(Osero::nHandCustom);
        playMethod.add(Osero::nLeast);
        playMethod.add(Osero::nMost);

        OseroData run = new OseroData();
        run.setReadGoal(2, 2);

        // train data
        run.dataClear();
        for (BiConsumer<long[], Boolean> black: playMethod){
            for (BiConsumer<long[], Boolean> white: playMethod){
                run.setPlayMethod(black, white);
                run.addData(1);
            }
        }
        Matrix X = new Matrix(run.history);
        Matrix T = new Matrix(run.result);

        // val data
        run.dataClear();
        run.setRandom(100);
        for (BiConsumer<long[], Boolean> black: playMethod){
            for (BiConsumer<long[], Boolean> white: playMethod){
                run.setPlayMethod(black, white);
                run.addData(1);
            }
        }
        Matrix valX = new Matrix(run.history);
        Matrix valT = new Matrix(run.result);

        int i = 1;
        for (int nodesNum = 5; nodesNum <= 100; nodesNum += 5){
            System.out.printf("\r%d", i);
            Network net = new Network(
                192,
                new Input(100, AF.RELU),
                new Dense(nodesNum, AF.SIGMOID),
                new Output(1, AF.RELU)
            );
            Adam opt = new Adam(net, new MeanAbsoluteError());
            opt.fit(X, T, 100, X.row / 30, valX, valT,
            "data/06/history"+Integer.toString(nodesNum)+".csv");
            // opt.fit(X, T, 10, X.row / 30, valX, valT);
            // net.save("df_14_dense64.net");
            i++;
        }
        System.out.println();
    }
}