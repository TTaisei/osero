package osero56;

import myNet.layer.Input;
import myNet.layer.Output;
import myNet.network.Network;
import myNet.nodes.activationFunction.AF;

public class Test {
	public static void main(String str[]) {
		Network net = new Network(
			64,
			new Input(8, AF.RELU),
//			new Dense(64, AF.RELU),
			new Output(64, AF.SIGMOID)
		);
//		Optimizer opt = new SGD(net, new MeanSquaredError());
		System.out.println(net);
	}
}
