package myNet.layer;

import myNet.matrix.Matrix;
import myNet.nodes.Node;
import myNet.nodes.activationFunction.AF;

/**
 * Class for input layer.
 */
public class Input extends Layer{
    /** Type of this layer. */
    public final String type = "Input";

    /**
     * Constructor for this class.
     * @param nodes_num
     */
    public Input(int nodes_num, int input_num, AF type){
        super(nodes_num, input_num, type);
    }

    /**
     * Constructor to call from the Network class.
     * @param nodes_num number of nodes for this node.
     * @param type type of activation function for this node.
     */
    public Input(int nodes_num, AF type){
        this.nodes_num = nodes_num;
        this.nodes = new Node[this.nodes_num];
        this.AF = type;
    }

    /**
     * Doing forward propagation.
     * @param in input matrix.
     * @return Matrix instance of output.
     */
    public Matrix forward(Matrix in){
        if (in.col + 1 != this.nodes[0].in){
            System.out.println("input layer error");
            System.exit(-1);
        }

        Matrix in_ = Matrix.appendCol(in, 1.0);
        Matrix out[] = new Matrix[this.nodes.length];

        for (int i = 0; i < nodes.length; i++){
            out[i] = nodes[i].forward(in_);
        }

        return Matrix.hstack(out);
    }

    @Override
    public String toString(){
        String str = "Input\t";
        str += String.format("nodes num: %d", this.nodes_num);

        return str;
    }

    @Override
    public Input clone(){
        Input rtn = new Input(this.nodes_num, this.AF);
        return rtn;
    }
}
