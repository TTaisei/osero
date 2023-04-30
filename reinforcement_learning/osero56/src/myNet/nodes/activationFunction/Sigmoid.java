package myNet.nodes.activationFunction;

import myNet.matrix.Matrix;

/**
 * Sigmoid function.
 */
public class Sigmoid extends ActivationFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public Sigmoid(){
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
                rtn.matrix[i][j] = 1 / (1 + Math.exp(-matrix.matrix[i][j]));
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
                rtn.matrix[i][j] = (1 - 1/(1+Math.exp(-matrix.matrix[i][j])))
                                   / (1 + Math.exp(-matrix.matrix[i][j]));
            }
        }

        return rtn;
    }

    @Override
    public String toString(){
        return "Sigmoid";
    }
}
