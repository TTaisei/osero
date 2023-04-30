package myNet.nodes.activationFunction;

import java.io.Serializable;

import myNet.matrix.Matrix;

/**
 * Activation function's base class.
 * All activation functions must extend this class.
 */
public class ActivationFunction implements Serializable {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public ActivationFunction(){
        ;
    }

    /**
     * Actiation function execution.
     * @param matrix linear transformationed matrix.
     * @return output matrix.
     */
    public Matrix calcurate(Matrix matrix){
        Matrix rtn = matrix.clone();
        return rtn;
    }

    /**
     * Calcurate this activation function's differential.
     * @param matrix Matrix of input.
     * @return The result of differentiating this activation function.
     */
    public Matrix differential(Matrix matrix){
        Matrix rtn = matrix.clone();
        rtn.fillNum(1.0);
        return rtn;
    }

    @Override
    public String toString(){
        return "";
    }
}
