import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.MyNet.matrix.*;
import org.MyNet.network.*;
import org.MyNet.layer.*;
import org.MyNet.nodes.activationFunction.*;
import org.MyNet.optimizer.*;
import org.MyNet.costFunction.*;

public class Run {
    public static void main(String str[]){
        ArrayList<BiConsumer<long[], Boolean>> playMethod = new ArrayList<BiConsumer<long[], Boolean>>();
        playMethod.add(Osero::random);
        playMethod.add(Osero::nHand);
        playMethod.add(Osero::nHandCustom);
        playMethod.add(Osero::nLeast);
        playMethod.add(Osero::nMost);

        OseroData run = new OseroData();
        run.setReadGoal(2, 2);
        run.dataClear();

        for (BiConsumer<long[], Boolean> black: playMethod){
            for (BiConsumer<long[], Boolean> white: playMethod){
                run.setPlayMethod(black, white);
                run.addData(1);
            }
        }

        // Deep Learning
        Matrix X = new Matrix(run.history);
        Matrix T = new Matrix(run.result);
        T.div(64.0);

        Network net = new Network(
            192,
            // new Input(192, AF.RELU),
            // new Output(1, AF.LINER)
            new Input(100, AF.TANH),
            new Dense(50, AF.TANH),
            new Output(1, AF.SIGMOID)
        );
        Adam opt = new Adam(net, new MeanSquaredError());
        opt.fit(X, T, 10, X.row / 20);
        net.save("osero.net");
    }
}