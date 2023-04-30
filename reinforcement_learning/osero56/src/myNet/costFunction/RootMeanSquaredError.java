package myNet.costFunction;

import myNet.matrix.Matrix;

/**
 * Cost function's base class.
 * All cost functions must extend this class.
 */
public class RootMeanSquaredError extends CostFunction {
    /**
     * Constructor for this class.
     * Nothing to do.
     */
    public RootMeanSquaredError(){
        ;
    }

    /**
     * Calcurate this cost function.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return RMSE between y and t.
     */
    public Matrix calcurate(Matrix y, Matrix t){
        Matrix rtn = new Matrix(new double[1][y.col]);
        double num;

        for (int j = 0; j < y.col; j++){
            num = 0;
            for (int i = 0; i < y.row; i++){
                num += Math.pow(y.matrix[i][j] - t.matrix[i][j], 2);
            }
            rtn.matrix[0][j] = Math.sqrt(num);
        }

        return Matrix.div(rtn, y.row);
    }

    /**
     * Calcurate this cost function's differential.
     * @param y Matrix of network's output.
     * @param t Matrix of actual data.
     * @return The result of differentiating the MAE between y and t.
     */
    public Matrix differential(Matrix y, Matrix t){
        return new Matrix(new double[1][1]);
    }

    @Override
    public String toString(){
        return "RootMeanSquaredError";
    }
}
