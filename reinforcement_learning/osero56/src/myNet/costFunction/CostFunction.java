package myNet.costFunction;

import myNet.matrix.Matrix;

/**
 * Cost function's base class.
 * All cost functions must extend this class.
 */
public class CostFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public CostFunction(){
        ;
    }

    /**
     * Calcurate this cost function.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return Difference between y and t.
     */
    public Matrix calcurate(Matrix y, Matrix t){
        return Matrix.sub(y, t);
    }

    /**
     * Calcurate this cost function's differential.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return The result of differentiating the difference between y and t.
     */
    public Matrix differential(Matrix y, Matrix t){
        return Matrix.sub(y, t);
    }

    @Override
    public String toString(){
        return "CostFunction";
    }
}
