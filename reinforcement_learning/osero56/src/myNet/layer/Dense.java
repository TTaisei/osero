package myNet.layer;

import myNet.matrix.Matrix;
import myNet.nodes.Node;
import myNet.nodes.activationFunction.AF;

/**
 * Class for affine layer.
 */
public class Dense extends Layer{
    /** Type of this layer. */
    public final String type = "Dense";

    /**
     * Constructor for this class.
     * @param nodes_num
     */
    public Dense(int nodes_num, int input_num, AF type){
        super(nodes_num, input_num, type);
    }

    /**
     * Constructor to call from the Network class.
     * @param nodes_num number of nodes for this node.
     * @param type type of activation function for this node.
     */
    public Dense(int nodes_num, AF type){
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
            System.out.println("dense layer error");
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
        String str = "Dense\t";
        str += String.format("nodes num: %d", this.nodes_num);

        return str;
    }

    @Override
    public Dense clone(){
        Dense rtn = new Dense(this.nodes_num, this.AF);
        return rtn;
    }
}
