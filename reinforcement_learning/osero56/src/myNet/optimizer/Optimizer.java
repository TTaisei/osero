package myNet.optimizer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import myNet.costFunction.CostFunction;
import myNet.layer.Layer;
import myNet.matrix.Matrix;
import myNet.network.Network;
import myNet.nodes.Node;

/**
 * Parent class of optimizer.
 */
public class Optimizer {
    Random rand;
    /** Network to which optimization is applied. */
    public Network net;
    /** Learning rate. */
    public double eta = 0.01;
    /** Cost function of this network. */
    public CostFunction cFunc;

    /**
     * Constructor for this class.
     */
    protected Optimizer(){
        ;
    }

    /**
     * Doing forward propagation.
     * @param in input matrix.
     * @return Matrix instance of output.
     */
    public Matrix forward(Matrix in){
        return this.net.forward(in);
    }

    public boolean networkIsWrong(){
        if (this.net.layers[this.net.layers_num-1].type != "Output"){
            return true;
        }
        if (this.net.layers[0].type != "Input"){
            return true;
        }

        for (int i = 1; i < this.net.layers_num; i++){
            System.out.println(i);
            if (this.net.layers[i].type != "Dense"){
                return true;
            }
        }

        return false;
    }

    /**
     * Calcurate output of a layer.
     * @param nodes Nodes in the layer.
     * @return Output of the layer.
     */
    public Matrix calA(Node[] nodes){
        Matrix rtn = new Matrix(new double[nodes[0].a.row][nodes.length]);

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = nodes[j].a.matrix[i][0];
            }
        }

        return Matrix.appendCol(rtn, 1.0);
    }

    /**
     * Get a matrix of weights related to the output of a node.
     * @param nodes Nodes of next layer.
     * @param num Number of the node.
     * @return Matrix instance.
     */
    public Matrix calW(Node[] nodes, int num){
        Matrix rtn = new Matrix(new double[nodes.length][1]);

        for (int i = 0; i < nodes.length; i++){
            rtn.matrix[i][0] = nodes[i].w.matrix[num][0];
        }

        return rtn;
    }

    /**
     * Make data for mini batch learning.
     * @param x Input data.
     * @param t Answer.
     * @param batchSize Number of batch size.
     * @param rand Random instance.
     * @return Splited input data and answer.
     */
    public Matrix[][] makeMiniBatch(Matrix x, Matrix t, int batchSize, Random rand){
        int rtnSize = (int)(x.row / batchSize) + 1;
        int num;
        int i;
        ArrayList<Integer> order = new ArrayList<Integer>(rtnSize);
        ArrayList<Integer> check = new ArrayList<Integer>(rtnSize);

        for (i = 0; i < x.row; i++){
            check.add(i);
        }
        for (i = 0; i < x.row; i++){
            num = rand.nextInt(x.row - order.size());
            order.add(check.get(num));
            check.remove(num);
        }
        for (; i < rtnSize*batchSize; i++){
            order.add(rand.nextInt(x.row));
        }

        Matrix x_ = Matrix.vsort(x, order);
        Matrix t_ = Matrix.vsort(t, order);
        Matrix[][] rtn = {Matrix.vsplit(x_, rtnSize), Matrix.vsplit(t_, rtnSize)};
        return rtn;
    }

    /**
     * Run learning.
     * @param x Input layer.
     * @param t Answer.
     * @param nEpoch Number of epoch.
     * @param batchSize Size of batch.
     * @return Output of this network.
     */
    public Matrix fit(Matrix x, Matrix t, int nEpoch, int batchSize){
        Matrix[][] xt = this.makeMiniBatch(x, t, batchSize, rand);
        Matrix[] xs = xt[0];
        Matrix[] ts = xt[1];
        Matrix y = ts[0].clone();
        int backNum = (int)(x.row / batchSize) + 1;

        for (int i = 0; i < nEpoch; i++){
            System.out.printf("Epoch %d/%d\n", i+1, nEpoch);
            for (int j = 0; j < backNum; j++){
                y = this.forward(xs[j]);
                this.back(xs[j], y, ts[j]);
                System.out.printf("\rloss: %.4f", this.cFunc.calcurate(y, ts[j]).matrix[0][0]);
            }
            System.out.println();
        }

        return y;
    }

    /**
     * Run learning.
     * @param x Input layer.
     * @param t Answer.
     * @param nEpoch Number of epoch.
     * @param batchSize Size of batch.
     * @param valX Input layer for validation.
     * @param valT Answer for validation.
     * @return Output of this network.
     */
    public Matrix fit(Matrix x, Matrix t, int nEpoch, int batchSize,
                      Matrix valX, Matrix valT){
        Matrix[][] xt = this.makeMiniBatch(x, t, batchSize, rand);
        Matrix[] xs = xt[0];
        Matrix[] ts = xt[1];
        Matrix[][] valxt = this.makeMiniBatch(valX, valT, batchSize, rand);
        Matrix[] valxs = valxt[0];
        Matrix[] valts = valxt[1];
        Matrix y = ts[0].clone();
        Matrix valY;
        int backNum = (int)(x.row / batchSize) + 1;

        for (int i = 0; i < nEpoch; i++){
            System.out.printf("Epoch %d/%d\n", i+1, nEpoch);
            for (int j = 0; j < backNum; j++){
                valY = this.forward(valxs[j]);
                y = this.forward(xs[j]);
                this.back(xs[j], y, ts[j]);
                System.out.printf(
                    "\rloss: %.4f - valLoss: %.4f",
                    this.cFunc.calcurate(y, ts[j]).matrix[0][0],
                    this.cFunc.calcurate(valY, valts[j]).matrix[0][0]
                );
            }
            System.out.println();
        }

        return y;
    }

    /**
     * Run learning and save log.
     * @param x Input layer.
     * @param t Answer.
     * @param nEpoch Number of epoch.
     * @param batchSize Size of batch.
     * @param fileName Name of logging file.
     * @return Output of this network.
     */
    public Matrix fit(Matrix x, Matrix t, int nEpoch, int batchSize, String fileName){
        Matrix[][] xt = this.makeMiniBatch(x, t, batchSize, rand);
        Matrix[] xs = xt[0];
        Matrix[] ts = xt[1];
        Matrix y = ts[0].clone();
        int backNum = (int)(x.row / batchSize) + 1;
        double loss = 0.;

        try(
            PrintWriter fp = new PrintWriter(fileName);
        ){
            fp.write("Epoch,loss\n");
            for (int i = 0; i < nEpoch; i++){
                for (int j = 0; j < backNum; j++){
                    y = this.forward(xs[j]);
                    this.back(xs[j], y, ts[j]);
                }
                loss = this.cFunc.calcurate(y, ts[ts.length-1]).matrix[0][0];
                fp.printf("%d,%f\n", i+1, loss);
            }
        }catch (IOException e){
            System.out.println("IO Exception");
            System.exit(-1);
        }

        return y;
    }

    /**
     * Run learning and save log.
     * @param x Input layer.
     * @param t Answer.
     * @param nEpoch Number of epoch.
     * @param batchSize Size of batch.
     * @param valX Input layer for validation.
     * @param valT Answer for validation.
     * @param fileName Name of logging file.
     * @return Output of this network.
     */
    public Matrix fit(Matrix x, Matrix t, int nEpoch, int batchSize,
                      Matrix valX, Matrix valT, String fileName){
        Matrix[][] xt = this.makeMiniBatch(x, t, batchSize, rand);
        Matrix[] xs = xt[0];
        Matrix[] ts = xt[1];
        Matrix y = ts[0].clone();
        Matrix valY;
        int backNum = (int)(x.row / batchSize) + 1;
        double loss = 0., valLoss = 0.;

        try(
            PrintWriter fp = new PrintWriter(fileName);
        ){
            fp.write("Epoch,loss,valLoss\n");
            for (int i = 0; i < nEpoch; i++){
                for (int j = 0; j < backNum; j++){
                    y = this.forward(xs[j]);
                    this.back(xs[j], y, ts[j]);
                }
                valY = this.forward(valX);
                loss = this.cFunc.calcurate(y, ts[ts.length-1]).matrix[0][0];
                valLoss = this.cFunc.calcurate(valY, valT).matrix[0][0];
                fp.printf("%d,%f,%f\n", i+1, loss, valLoss);
            }
        }catch (IOException e){
            System.out.println("IO Exception");
            System.exit(-1);
        }

        return y;
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
