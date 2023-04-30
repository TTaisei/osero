package myNet.costFunction;

import myNet.matrix.Matrix;

/**
 * Cost function's base class.
 * All cost functions must extend this class.
 */
public class MeanSquaredError extends CostFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public MeanSquaredError(){
        ;
    }

    /**
     * Calcurate this cost function.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return MAE between y and t.
     */
    public Matrix calcurate(Matrix y, Matrix t){
        Matrix rtn = Matrix.sub(y, t);

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] *= rtn.matrix[i][j];
            }
        }

        return rtn.meanCol();
    }

    /**
     * Calcurate this cost function's differential.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return The result of differentiating the MSE between y and t.
     */
    public Matrix differential(Matrix y, Matrix t){
        Matrix rtn = Matrix.sub(y, t);
        rtn.meanCol();
        rtn.mult(2);
        return rtn;
    }

    @Override
    public String toString(){
        return "MeanSquaredError";
    }
}
