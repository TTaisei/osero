package myNet.nodes;

import java.io.Serializable;

import myNet.matrix.Matrix;
import myNet.nodes.activationFunction.AF;
import myNet.nodes.activationFunction.ActivationFunction;
import myNet.nodes.activationFunction.Liner;
import myNet.nodes.activationFunction.ReLu;
import myNet.nodes.activationFunction.Sigmoid;
import myNet.nodes.activationFunction.Tanh;

/**
 * One node class.
 */
public class Node implements Serializable {
    /** The number of inputs for this node. */
    public int in = 1;
    /** The list of weight for this node. */
    public Matrix w;
    /** Activation function of this node. */
    public ActivationFunction aFunc;
    /** Valiable for back propagation. */
    public double delta;
    /** Matrix linear transformed. */
    public Matrix x;
    /** Matrix of output from this node. */
    public Matrix a;

    /**
     * Constructor for this class.
     * Number of inputs includes bias.
     * @param input_num Number of inputs.
     * @param type Type of activation function.
     * @exception System.exit The specified activation function
     *                        does not exist or misspecified.
     */
    public Node(int input_num, AF type){
        in += input_num;
        w = new Matrix(new double[in][1]);
        // w.fillNum(0.5);
        w.fillRandom(-1., 1.);

        switch (type) {
            case RELU:
                aFunc = new ReLu();
                break;
            case SIGMOID:
                aFunc = new Sigmoid();
                break;
            case TANH:
                aFunc = new Tanh();
                break;
            case LINER:
                aFunc = new Liner();
                break;
            default:
                System.out.println("ERROR: The specified activation function is wrong");
                System.exit(-1);
        }
    }

    /**
     * Constructor for this class.
     * Number of inputs includes bias.
     * @param input_num Number of inputs.
     * @param type Type of activation function.
     * @param num Number of seed.
     * @exception System.exit The specified activation function
     *                        does not exist or misspecified.
     */
    public Node(int input_num, AF type, int num){
        in += input_num;
        w = new Matrix(new double[in][1]);
        // w.fillNum(0.5);
        w.fillRandom(-1., 1., num);

        switch (type) {
            case RELU:
                aFunc = new ReLu();
                break;
            case SIGMOID:
                aFunc = new Sigmoid();
                break;
            case TANH:
                aFunc = new Tanh();
                break;
            case LINER:
                aFunc = new Liner();
                break;
            default:
                System.out.println("ERROR: The specified activation function is wrong");
                System.exit(-1);
        }
    }

    /**
     * Doing forward propagation.
     * @param input Array of inputs.
     * @return output array's element.
     */
    public Matrix forward(Matrix input){
        this.x = Matrix.dot(input, w);
        this.a = aFunc.calcurate(x);

        return a.clone();
    }

    @Override
    public String toString(){
        String rtn = String.format("input: %d\n", this.in - 1);
        rtn += String.format("activation function: %s", this.aFunc.toString());

        return rtn;
    }
}
