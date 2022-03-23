import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.MyNet.matrix.*;
import org.MyNet.network.*;
import org.MyNet.layer.*;
import org.MyNet.nodes.activationFunction.*;
import org.MyNet.optimizer.*;
import org.MyNet.costFunction.*;

public class Run03 {
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

        Network net = new Network(
            192,
            new Input(100, AF.RELU),
            new Dense(50, AF.SIGMOID),
            new Output(1, AF.RELU)
        );
        Adam opt = new Adam(net, new MeanSquaredError());
        opt.fit(X, T, 1000, X.row / 30, valX, valT, "data/03/history1000.csv");
    }
}