package myNet.nodes.activationFunction;

import myNet.matrix.Matrix;

/**
 * ReLu function.
 */
public class ReLu extends ActivationFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public ReLu(){
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
                rtn.matrix[i][j] = Math.max(0.0, matrix.matrix[i][j]);
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
        Matrix rtn = matrix.clone();

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                if (rtn.matrix[i][j] < 0){
                    rtn.matrix[i][j] = 0;
                }else{
                    rtn.matrix[i][j] = 1.0;
                }
            }
        }

        return rtn;
    }

    @Override
    public String toString(){
        return "ReLu";
    }
}
