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
public class Adam extends Optimizer{
    /** beta */
    double beta1 = 0.9, beta2 = 0.999;
    /** m */
    ArrayList<ArrayList<Matrix>> m;
    /** v */
    double v = 10e-8;

    /**
     * Constructor for this class.
     */
    public Adam(){
        ;
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     */
    public Adam(Network net, CostFunction f){
        this.net = net;
        this.cFunc = f;
        this.eta = 0.001;
        rand = new Random(0);
        this.setM();
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param eta Learning rate.
     */
    public Adam(Network net, CostFunction f, double eta,
                double beta1, double beta2, double v){
        this.net = net;
        this.cFunc = f;
        this.eta = eta;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.v = v;
        rand = new Random(0);
        this.setM();
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param seed Seed of random.
     */
    public Adam(Network net, CostFunction f, int seed){
        this.net = net;
        this.cFunc = f;
        this.eta = 0.001;
        rand = new Random(seed);
        this.setM();
    }

    /**
     * Constructor for this class.
     * @param net Network to which optimization is applied.
     * @param f Cost function in this net.
     * @param eta Learning rate.
     * @param seed Seed of random.
     */
    public Adam(Network net, CostFunction f, double eta,
                double beta1, double beta2, double v, int seed){
        this.net = net;
        this.cFunc = f;
        this.eta = eta;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.v = v;
        rand = new Random(seed);
        this.setM();
    }

    private void setM(){
        this.m = new ArrayList<ArrayList<Matrix>>();

        for (int i = 0; i < this.net.layers_num; i++){
            this.m.add(new ArrayList<Matrix>());
            for (int j = 0; j < this.net.layers[i].nodes_num; j++){
                this.m.get(i).add(this.net.layers[i].nodes[j].w.clone());
                this.m.get(i).get(j).fillNum(0.);
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
        double sum = 0.;
        double v = 1 / Math.sqrt(this.v / (1-this.beta2));

        // last layer
        Layer nowLayer = this.net.layers[this.net.layers_num-1];
        Layer preLayer = this.net.layers[this.net.layers_num-2];
        ArrayList<Matrix> m = this.m.get(this.net.layers_num-1);
        for (int i = 0; i < nowLayer.nodes.length; i++){
            Node nowNode = nowLayer.nodes[i];
            Matrix cal;

            cal = this.cFunc.differential(nowNode.a, t.getCol(i));
            cal = Matrix.dot(cal.T(), nowNode.aFunc.differential(nowNode.x));
            nowNode.delta = cal.matrix[0][0];
            cal = Matrix.mult(this.calA(preLayer.nodes), nowNode.delta);
            cal = cal.meanCol();
            sum += Matrix.sum(Matrix.pow(cal));

            m.get(i).mult(this.beta1);
            m.get(i).add(Matrix.mult(cal.T(), (1-this.beta1)));

            nowNode.w.add(Matrix.mult(m.get(i), -this.eta*v/(1-this.beta1)));
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

            m = this.m.get(i);
            for (int j = 0; j < nowNodes.length; j++){
                Node nowNode = nowNodes[j];
                Matrix cal;

                nowNode.delta = Matrix.dot(deltas, this.calW(nextNodes, j)).matrix[0][0]
                                * nowNode.aFunc.differential(nowNode.x.meanCol()).matrix[0][0];
                cal = Matrix.mult(preA.meanCol(), nowNode.delta);
                sum += Matrix.sum(Matrix.pow(cal));

                m.get(j).mult(this.beta1);
                m.get(j).add(Matrix.mult(cal.T(), (1-this.beta1)));

                nowNode.w.add(Matrix.mult(m.get(j), -this.eta*v/(1-this.beta1)));
            }
        }

        this.v = this.beta2 * this.v + (1 - this.beta2) * sum;
    }
}
