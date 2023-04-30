package myNet.layer;

import java.io.Serializable;

import myNet.matrix.Matrix;
import myNet.nodes.Node;
import myNet.nodes.activationFunction.AF;

/**
 * Class for layer.
 */
public class Layer implements Serializable{
    /** Nodes array for this layer. */
    public Node[] nodes;
    /** number of nodes this layer. */
    public int nodes_num;
    /** Type of activation function of this layer. */
    public AF AF;
    /** Type of this layer. */
    public String type = "Layer";

    /**
     * Constructor for this class.
     */
    public Layer(){
        ;
    }

    /**
     * Constructor for this class.
     * @param nodes_num number of nodes for this node.
     * @param input_num number of input matrix's length.
     * @param type type of activation function for this node.
     */
    public Layer(int nodes_num, int input_num, AF type){
        this.nodes_num = nodes_num;
        this.nodes = new Node[this.nodes_num];
        this.AF = type;

        for (int i = 0; i < this.nodes_num; i++){
            nodes[i] = new Node(input_num, type);
        }
    }

    /**
     * Constructor to call from the Network class.
     * @param nodes_num number of nodes for this node.
     * @param type type of activation function for this node.
     */
    public Layer(int nodes_num, AF type){
        this.nodes_num = nodes_num;
        this.nodes = new Node[this.nodes_num];
        this.AF = type;
    }

    /**
     * Doing forward propagation.
     * In this class, nothing to do.
     * @param in input matrix.
     * @return Matrix instance of output.
     */
    public Matrix forward(Matrix in){
        return in;
    }

    /**
     * Doing back propagation.
     */
    public void back(){
        ;
    }

    /**
     * Calucrate delta each nodes.
     */
    public void calDelta(){
        ;
    }

    @Override
    public String toString(){
        String str = String.format("nodes num: %d", this.nodes_num);

        return str;
    }

    @Override
    public Layer clone(){
        Layer rtn = new Layer(this.nodes_num, this.AF);
        return rtn;
    }
}
