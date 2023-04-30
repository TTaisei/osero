package myNet.nodes.activationFunction;

import myNet.matrix.Matrix;

/**
 * Tanh function.
 */
public class Tanh extends ActivationFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public Tanh(){
        ;
    }

    /**
     * Actiation function execution.
     * @param matrix linear transformationed matrix.
     * @return output matrix.
     */
    @Override
    public Matrix calcurate(Matrix matrix){
        Matrix rtn = matrix.clone();

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = Math.tanh(matrix.matrix[i][j]);
            }
        }

        return rtn;
    }

    /**
     * Calcurate this activation function's differential.
     * @param matrix Matrix of input.
     * @return The result of differentiating this activation function.
     */
    public Matrix differential(Matrix matrix){
        Matrix rtn = this.calcurate(matrix);
        rtn.pow();
        rtn.mult(-1);
        return Matrix.add(rtn, 1.0);
    }

    @Override
    public String toString(){
        return "Tanh";
    }
}
