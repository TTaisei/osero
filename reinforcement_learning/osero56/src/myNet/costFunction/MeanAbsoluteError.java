package myNet.costFunction;

import myNet.matrix.Matrix;

/**
 * Cost function's base class.
 * All cost functions must extend this class.
 */
public class MeanAbsoluteError extends CostFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public MeanAbsoluteError(){
        ;
    }

    /**
     * Calcurate this cost function.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return MSE between y and t.
     */
    public Matrix calcurate(Matrix y, Matrix t){
        Matrix rtn = Matrix.abs(Matrix.sub(y, t));
        return rtn.meanCol();
    }

    /**
     * Calcurate this cost function's differential.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return The result of differentiating the MAE between y and t.
     */
    public Matrix differential(Matrix y, Matrix t){
        Matrix rtn = new Matrix(t);

        for (int i = 0; i < t.row; i++){
            for (int j = 0; j < t.col; j++){
                if (y.matrix[i][j] - t.matrix[i][j] > 0){
                    rtn.matrix[i][j] = 1.0;
                }else{
                    rtn.matrix[i][j] = -1.0;
                }
            }
        }

        return rtn;
    }

    @Override
    public String toString(){
        return "MeanAbsoluteError";
    }
}
