package myNet.optimizer;

import java.util.ArrayList;
import java.util.Random;

import myNet.costFunction.CostFunction;
import myNet.layer.Layer;
import myNet.matrix.Matrix;
import myNet.network.Network;
import myNet.nodes.Node;

/**
 * Class for Stochastic Gradient Descent.
 */
public class MomentumSGD extends Optimizer{
    /** Value of momentum */
    double alpha = 0.9;
    /** Amount of change in weight */
    ArrayList<ArrayList<Matrix>> dw;

    /**
     * Constructor for this class.
     */
    public MomentumSGD(){
        ;
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     */
    public MomentumSGD(Network net, CostFunction f){
        this.net = net;
        this.cFunc = f;
        rand = new Random(0);
        this.setDw();
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param eta Learning rate.
     * @param alpha Value of momentum.
     */
    public MomentumSGD(Network net, CostFunction f, double eta, double alpha){
        this.net = net;
        this.cFunc = f;
        this.eta = eta;
        this.alpha = alpha;
        rand = new Random(0);
        this.setDw();
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param seed Seed of random.
     */
    public MomentumSGD(Network net, CostFunction f, int seed){
        this.net = net;
        this.cFunc = f;
        rand = new Random(seed);
        this.setDw();
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param eta Learning rate.
     * @param alpha Value of momentum.
     * @param seed Seed of random.
     */
    public MomentumSGD(Network net, CostFunction f,
                       double eta, double alpha, int seed){
        this.net = net;
        this.cFunc = f;
        this.eta = eta;
        this.alpha = alpha;
        rand = new Random(seed);
        this.setDw();
    }

    /**
     * Set dw field.
     */
    private void setDw(){
        this.dw = new ArrayList<ArrayList<Matrix>>();
        for (int i = 0; i < this.net.layers_num; i++){
            this.dw.add(new ArrayList<Matrix>());
            for (int j = 0; j < this.net.layers[i].nodes_num; j++){
                this.dw.get(i).add(this.net.layers[i].nodes[j].w.clone());
                this.dw.get(i).get(j).fillNum(0.);
            }
        }
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
        ArrayList<Matrix> dw = this.dw.get(this.net.layers_num-1);
        for (int i = 0; i < nowLayer.nodes.length; i++){
            Node nowNode = nowLayer.nodes[i];
            Matrix cal;

            cal = this.cFunc.differential(nowNode.a, t.getCol(i));
            cal = Matrix.dot(cal.T(), nowNode.aFunc.differential(nowNode.x));
            nowNode.delta = cal.matrix[0][0];
            cal = Matrix.mult(this.calA(preLayer.nodes), nowNode.delta);
            cal.mult(-this.eta);
            dw.get(i).mult(this.alpha);
            dw.get(i).add(cal.meanCol().T());
            nowNode.w.add(dw.get(i));
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

            dw = this.dw.get(i);
            for (int j = 0; j < nowNodes.length; j++){
                Node nowNode = nowNodes[j];
                Matrix cal;

                nowNode.delta = Matrix.dot(deltas, this.calW(nextNodes, j)).matrix[0][0]
                                * nowNode.aFunc.differential(nowNode.x.meanCol()).matrix[0][0];
                cal = Matrix.mult(preA.meanCol(), -this.eta*nowNode.delta);
                dw.get(j).mult(this.alpha);
                dw.get(j).add(cal.T());
                nowNode.w.add(dw.get(j));
            }
        }
    }
}
