package myNet.optimizer;

import java.util.Random;

import myNet.costFunction.CostFunction;
import myNet.layer.Layer;
import myNet.matrix.Matrix;
import myNet.network.Network;
import myNet.nodes.Node;

/**
 * Class for Stochastic Gradient Descent.
 */
public class SGD extends Optimizer{
    /**
     * Constructor for this class.
     */
    public SGD(){
        ;
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     */
    public SGD(Network net, CostFunction f){
        this.net = net;
        this.cFunc = f;
        rand = new Random(0);
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param eta Learning rate.
     */
    public SGD(Network net, CostFunction f, double eta){
        this.net = net;
        this.cFunc = f;
        this.eta = eta;
        rand = new Random(0);
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param seed Seed of random.
     */
    public SGD(Network net, CostFunction f, int seed){
        this.net = net;
        this.cFunc = f;
        rand = new Random(seed);
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param eta Learning rate.
     * @param seed Seed of random.
     */
    public SGD(Network net, CostFunction f, double eta, int seed){
        this.net = net;
        this.cFunc = f;
        this.eta = eta;
        rand = new Random(seed);
    }

    /**
     * Doing back propagation.
     * @param x Input layer.
     * @param y Result of forward propagation.
     * @param t Answer.
     */
    public void back(Matrix x, Matrix y, Matrix t){
        // last layer
        Layer nowLayer = this.net.layers[this.net.layers_num-1];
        Layer preLayer = this.net.layers[this.net.layers_num-2];
        for (int i = 0; i < nowLayer.nodes.length; i++){
            Node nowNode = nowLayer.nodes[i];
            Matrix cal;

            cal = this.cFunc.differential(nowNode.a, t.getCol(i));
            cal = Matrix.dot(cal.T(), nowNode.aFunc.differential(nowNode.x));
            nowNode.delta = cal.matrix[0][0];
            cal = Matrix.mult(this.calA(preLayer.nodes), nowNode.delta);
            cal.mult(-this.eta);
            nowNode.w.add(cal.meanCol().T());
        }

        // middle layer and input layer
        for (int i = this.net.layers_num-2; i >= 0; i--){
            Node[] nextNodes = this.net.layers[i+1].nodes;
            Node[] nowNodes = this.net.layers[i].nodes;
            Node[] preNodes;
            Matrix deltas = new Matrix(new double[1][nextNodes.length]);
            Matrix preA;

            if (i != 0){
                // middle layer
                preNodes = this.net.layers[i-1].nodes;
                preA = this.calA(preNodes);
            }else{
                // input layer
                preA = Matrix.appendCol(x, 1.0);
            }

            for (int j = 0; j < nextNodes.length; j++){
                deltas.matrix[0][j] = nextNodes[j].delta;
            }

            for (int j = 0; j < nowNodes.length; j++){
                Node nowNode = nowNodes[j];
                Matrix cal;

                nowNode.delta = Matrix.dot(deltas, this.calW(nextNodes, j)).matrix[0][0]
                                * nowNode.aFunc.differential(nowNode.x.meanCol()).matrix[0][0];
                cal = Matrix.mult(preA.meanCol(), -this.eta*nowNode.delta);
                nowNode.w.add(cal.T());
            }
        }
    }
}
