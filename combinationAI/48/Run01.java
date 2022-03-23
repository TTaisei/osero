import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.MyNet.matrix.*;
import org.MyNet.network.*;
import org.MyNet.layer.*;
import org.MyNet.nodes.activationFunction.*;
import org.MyNet.optimizer.*;
import org.MyNet.costFunction.*;

public class Run01 {
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
        // T.div(64.0);

        Input[] inputLayers = {
            new Input(150, AF.TANH), new Input(150, AF.SIGMOID), new Input(150, AF.RELU),
            new Input(100, AF.TANH), new Input(100, AF.SIGMOID), new Input(100, AF.RELU)
        };
        Dense[] denseLayers = {
            new Dense(100, AF.TANH), new Dense(100, AF.SIGMOID), new Dense(100, AF.RELU),
            new Dense(50, AF.TANH),  new Dense(50, AF.SIGMOID),  new Dense(50, AF.RELU)
        };

        int i = 1;
        for (Input input: inputLayers){
            System.out.printf("\r%d/%d", i, inputLayers.length);
            for (Dense dense: denseLayers){
                Network net = new Network(
                    192,
                    input,
                    dense,
                    new Output(1, AF.RELU)
                );
                Adam opt = new Adam(net, new MeanAbsoluteError());

                for (int batch = 10; batch <= 30; batch += 10){
                    String fileName = "data/01/";
                    fileName += "node" + Integer.toString(net.layers[0].nodes_num) + " ";
                    fileName += "act" + net.layers[0].nodes[0].aFunc.toString() + " ";
                    fileName += "node" + Integer.toString(net.layers[1].nodes_num) + " ";
                    fileName += "act" + net.layers[1].nodes[0].aFunc.toString() + " ";
                    fileName += "batch" + Integer.toString(batch);

                    opt.fit(X, T, 30, X.row / batch, fileName+".csv");
                }
            }

            i++;
        }

        System.out.println();
    }
}