package myNet.matrix;

import java.io.*;
import java.lang.Math;
import java.util.Random;
import java.util.ArrayList;

/**
 * Class for calculation and save of matrix.
 */
public class Matrix implements Serializable {
    /** Matrix's value. */
    public double matrix[][];
    /** Matrix's row, col. */
    public int row, col;
    /** Matrix's shape */
    public int shape[];

    /**
     * Constructor for this class.
     * Set the matrix and its size.
     * @param in Two dimensional matrix of type Matrix.
     */
    public Matrix(Matrix in){
        this.matrix = new double[in.row][in.col];
        this.shape = new int[2];
        this.row = in.row;
        this.col = in.col;
        this.shape[0] = this.row; this.shape[1] = this.col;

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = in.matrix[i][j];
            }
        }
    }

    /**
     * Constructor for this class.
     * Set the matrix and its size.
     * @param in Two dimensional matrix of type double[][].
     */
    public Matrix(double in[][]){
        this.matrix = new double[in.length][in[0].length];
        this.shape = new int[2];
        this.row = in.length;
        this.col = in[0].length;
        this.shape[0] = this.row; this.shape[1] = this.col;

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = in[i][j];
            }
        }
    }

    /**
     * Constructor for this class.
     * Set the matrix and its size.
     * @param in Two dimensional matrix of type Double[][].
     */
    public Matrix(Double in[][]){
        this.matrix = new double[in.length][in[0].length];
        this.shape = new int[2];
        this.row = in.length;
        this.col = in[0].length;
        this.shape[0] = this.row; this.shape[1] = this.col;

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = in[i][j];
            }
        }
    }

    /**
     * Constructor for this class.
     * Set the matrix and its size.
     * @param in Two dimensional matrix of type ArrayList<ArrayList<Double>>.
     */
    public Matrix(ArrayList<ArrayList<Double>> in){
        this.matrix = new double[in.size()][in.get(0).size()];
        this.shape = new int[2];
        this.row = in.size();
        this.col = in.get(0).size();
        this.shape[0] = this.row; this.shape[1] = this.col;

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = in.get(i).get(j);
            }
        }
    }

    /**
     * Add a matrix to a matrix.
     * @param a Augend matrix.
     * @param b Addend matrix.
     * @return New added Matrix instance.
     */
    public static Matrix add(Matrix a, Matrix b){
        if (a.row != b.row || a.col != b.col){
            System.out.println("adding error");
            System.exit(-1);
        }

        Matrix rtn = new Matrix(new double[a.row][a.col]);
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < a.col; j++){
                rtn.matrix[i][j] = a.matrix[i][j] + b.matrix[i][j];
            }
        }

        return rtn;
    }

    /**
     * Add a number to a matirx.
     * @param a Matrix.
     * @param num Number.
     * @return New added Matrix instance.
     */
    public static Matrix add(Matrix a, double num){
        Matrix rtn = new Matrix(new double[a.row][a.col]);
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < a.col; j++){
                rtn.matrix[i][j] = a.matrix[i][j] + num;
            }
        }

        return rtn;
    }

    /**
     * Add a matrix to this matrix.
     * @param a Addend matrix.
     */
    public void add(Matrix a){
        if (this.row != a.row || this.col != a.col){
            System.out.println("adding error");
            System.exit(-1);
        }

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] += a.matrix[i][j];
            }
        }
    }

    /**
     * Add a number to this matirx.
     * @param num Number.
     */
    public void add(double num){
        Matrix rtn = new Matrix(new double[this.row][this.col]);
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                rtn.matrix[i][j] = this.matrix[i][j] + num;
            }
        }
    }

    /**
     * Subtract a matrix from a matrix.
     * @param a Matrix to be subtracted.
     * @param b Matrix to subtract.
     * @return New subtracted Matrix instance.
     */
    public static Matrix sub(Matrix a, Matrix b){
        if (a.row != b.row || a.col != b.col){
            System.out.println("subtracting error");
            System.exit(-1);
        }

        Matrix rtn = new Matrix(new double[a.row][a.col]);
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < a.col; j++){
                rtn.matrix[i][j] = a.matrix[i][j] - b.matrix[i][j];
            }
        }

        return rtn;
    }

    /**
     * Subtract a number from a matrix.
     * @param a Matrix.
     * @param num Number.
     * @return New subtracted Matrix instance.
     */
    public static Matrix sub(Matrix a, double num){
        Matrix rtn = new Matrix(new double[a.row][a.col]);
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < a.col; j++){
                rtn.matrix[i][j] = a.matrix[i][j] - num;
            }
        }

        return rtn;
    }

    /**
     * Multiply a matrix by a number.
     * @param a Matrix.
     * @param num Number.
     * @return New multiplied Matrix instance.
     */
    public static Matrix mult(Matrix a, double num){
        Matrix rtn = new Matrix(new double[a.row][a.col]);
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < a.col; j++){
                rtn.matrix[i][j] = a.matrix[i][j] * num;
            }
        }

        return rtn;
    }

    /**
     * Multiply this matrix by a number.
     * @param num Number.
     */
    public void mult(double num){
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = this.matrix[i][j] * num;
            }
        }
    }

    /**
     * Divid a matrix by a number.
     * @param a Matrix.
     * @param num Number.
     * @return New divided Matrix instance.
     */
    public static Matrix div(Matrix a, double num){
        Matrix rtn = new Matrix(new double[a.row][a.col]);
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < a.col; j++){
                rtn.matrix[i][j] = a.matrix[i][j] / num;
            }
        }

        return rtn;
    }

    /**
     * Divid this matrix by a number.
     * @param num Number.
     */
    public void div(double num){
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = this.matrix[i][j] / num;
            }
        }
    }

    /**
     * Dot product for two matrices.
     * @param a Matrix to be dot producted.
     * @param b Matrix to dot product.
     * @return New dot producted Matrix instance.
     */
    public static Matrix dot(Matrix a, Matrix b){
        if (a.col != b.row){
            System.out.println("dot producting error");
            System.exit(-1);
        }

        Matrix rtn = new Matrix(new double[a.row][b.col]);
        double num = 0;
        for (int i = 0; i < a.row; i++){
            for (int j = 0; j < b.col; j++){
                num = 0;
                for (int k = 0; k < a.col; k++){
                    num += a.matrix[i][k] * b.matrix[k][j];
                }
                rtn.matrix[i][j] = num;
            }
        }

        return rtn;
    }

    /**
     * Create transpose of this matrix.
     * @return New Matrix instance transposed of this matrix .
     */
    public Matrix T(){
        Matrix rtn = new Matrix(new double[col][row]);
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                rtn.matrix[j][i] = this.matrix[i][j];
            }
        }

        return rtn;
    }

    /**
     * Fill this matrix with a number.
     * @param num Number to fill.
     */
    public void fillNum(double num){
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = num;
            }
        }
    }

    /**
     * Fill this matrix with random numbers has range 0~1.
     */
    public void fillNextRandom(){
        Random rand = new Random(0);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = rand.nextDouble();
            }
        }
    }

    /**
     * Fill this matrix with random number has range min~max.
     * @param min Number of min for range.
     * @param max Number of max for range.
     */
    public void fillRandom(double min, double max){
        Random rand = new Random(0);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = rand.nextDouble()*(max-min) + min;
            }
        }
    }

    /**
     * Fill this matrix with random numbers has range 0~1.
     * @param num Number of seed.
     */
    public void fillNextRandom(int num){
        Random rand = new Random(num);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = rand.nextDouble();
            }
        }
    }

    /**
     * Fill this matrix with random number has range min~max.
     * @param min Number of min for range.
     * @param max Number of max for range.
     * @param num Number of seed.
     */
    public void fillRandom(double min, double max, int num){
        Random rand = new Random(num);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = rand.nextDouble()*(max-min) + min;
            }
        }
    }

    /**
     * Append a number to the side of this matrix.
     * @param matrix Matrix to be appended.
     * @param num number to append.
     * @return A Matrix instance appended number.
     */
    public static Matrix appendCol(Matrix matrix, double num){
        Matrix rtn = new Matrix(new double[matrix.row][matrix.col+1]);

        for (int i = 0; i < matrix.row; i++){
            for (int j = 0; j < matrix.col+1; j++){
                if (j != matrix.col){
                    rtn.matrix[i][j] = matrix.matrix[i][j];
                }else{
                    rtn.matrix[i][j] = num;
                }
            }
        }

        return rtn;
    }

    /**
     * Append a Matrix to the row of this matrix.
     * @param matrix Matrix to append.
     * @return A Matrix instance appended Matrix.
     */
    public Matrix append(Matrix matrix){
        if (matrix.row != 1){
            System.out.println("The number of row in the input matrix is wrong.");
            System.exit(-1);
        }else if (matrix.col != this.col){
            System.out.println("The number of col in the input matrix is wrong.");
            System.exit(-1);
        }

        Matrix rtn = new Matrix(new double[this.row+1][this.col]);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                rtn.matrix[i][j] = this.matrix[i][j];
            }
        }

        for (int i = 0; i < this.col; i++){
            rtn.matrix[this.row][i] = matrix.matrix[0][i];
        }

        return rtn;
    }

    /**
     * Append a Matrix to the row of this matrix.
     * @param matrix Matrix to append.
     * @return A Matrix instance appended Matrix.
     */
    public Matrix append(double[][] matrix){
        if (matrix.length != 1){
            System.out.println("The number of row in the input matrix is wrong.");
            System.exit(-1);
        }else if (matrix[0].length != this.col){
            System.out.println("The number of col in the input matrix is wrong.");
            System.exit(-1);
        }

        Matrix rtn = new Matrix(new double[this.row+1][this.col]);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                rtn.matrix[i][j] = this.matrix[i][j];
            }
        }

        for (int i = 0; i < this.col; i++){
            rtn.matrix[this.row][i] = matrix[0][i];
        }

        return rtn;
    }

    /**
     * Stack matrices horizontally.
     * @param matrices Matrices to stack.
     *                 These should not have more than two columns.
     * @return New Matrix instance stacked.
     */
    public static Matrix hstack(Matrix ... matrices){
        Matrix rtn = new Matrix(new double[matrices[0].row][matrices.length]);

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = matrices[j].matrix[i][0];
            }
        }

        return rtn;
    }

    /**
     * Stack matrices vertical.
     * @param matrices Matrices to stack.
     *                 These should not have more than two rows.
     * @return New Matrix instance stacked.
     */
    public static Matrix vstack(Matrix ... matrices){
        Matrix rtn = new Matrix(new double[matrices.length][matrices[0].col]);

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = matrices[i].matrix[0][j];
            }
        }

        return rtn;
    }

    /**
     * Split a matrix vertically.
     * @param in Matrix to be split.
     * @param num Number of split.
     * @return Array of Matrix instance.
     */
    public static Matrix[] vsplit(Matrix in, int num){
        Matrix[] rtn = new Matrix[num];
        int size = in.row / num;
        if (size * num != in.row){
            System.out.println("vsplit error");
            System.exit(-1);
        }

        for (int i = 0; i < num; i++){
            rtn[i] = new Matrix(new double[size][in.col]);
            for (int j = 0; j < size; j++){
                for (int k = 0; k < in.col; k++){
                    rtn[i].matrix[j][k] = in.matrix[i*size+j][k];
                }
            }
        }

        return rtn;
    }

    /**
     * Sort a matrix vertically.
     * @param in Matrix to be sort.
     * @param order Order of sort.
     * @return Matrix instance.
     */
    public static Matrix vsort(Matrix in, int[] order){
        Matrix rtn = new Matrix(new double[order.length][in.col]);

        for (int i = 0; i < order.length; i++){
            for (int j = 0; j < in.col; j++){
                rtn.matrix[i][j] = in.matrix[order[i]][j];
            }
        }

        return rtn;
    }

    /**
     * Sort a matrix vertically.
     * @param in Matrix to be sort.
     * @param order Order of sort.
     * @return Matrix instance.
     */
    public static Matrix vsort(Matrix in, ArrayList<Integer> order){
        Matrix rtn = new Matrix(new double[order.size()][in.col]);

        for (int i = 0; i < order.size(); i++){
            for (int j = 0; j < in.col; j++){
                rtn.matrix[i][j] = in.matrix[order.get(i)][j];
            }
        }

        return rtn;
    }

    /**
     * Return absolute value of this matrix.
     */
    public void abs(){
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = Math.abs(this.matrix[i][j]);
            }
        }
    }

    /**
     * Return absolute value of a matrix.
     * @param in Matrix.
     * @return Absolute value of a matrx.
     */
    public static Matrix abs(Matrix in){
        Matrix rtn = in.clone();

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = Math.abs(in.matrix[i][j]);
            }
        }

        return rtn;
    }

    /**
     * Calcurate average of each columns.
     * @return Matrix instance that had everage of each columns in this matrix.
     */
    public Matrix meanCol(){
        Matrix rtn = new Matrix(new double[1][this.col]);

        double num = 0;
        for (int j = 0; j < this.col; j++){
            num = 0;
            for (int i = 0; i < this.row; i++){
                num += this.matrix[i][j];
            }
            rtn.matrix[0][j] = num / this.row;
        }

        return rtn;
    }

    /**
     * Calcurate average of each columns.
     * @param in A Matrix instance.
     * @return Matrix instance that had everage of each columns in a matrix.
     */
    public static Matrix meanCol(Matrix in){
        Matrix rtn = new Matrix(new double[1][in.col]);

        double num = 0;
        for (int j = 0; j < in.col; j++){
            num = 0;
            for (int i = 0; i < in.row; i++){
                num += in.matrix[i][j];
            }
            rtn.matrix[0][j] = num / in.row;
        }

        return rtn;
    }

    /**
     * Calcurate average of each rows.
     * @return Matrix instance that had everage of each rows in this matrix.
     */
    public Matrix meanRow(){
        Matrix rtn = new Matrix(new double[this.row][1]);

        double num = 0;
        for (int i = 0; i < this.row; i++){
            num = 0;
            for (int j = 0; j < this.row; j++){
                num += this.matrix[i][j];
            }
            rtn.matrix[i][0] = num / this.col;
        }

        return rtn;
    }

    /**
     * Calcurate average of each rows.
     * @param in A Matrix instance.
     * @return Matrix instance that had everage of each rows in a matrix.
     */
    public static Matrix meanRow(Matrix in){
        Matrix rtn = new Matrix(new double[in.row][1]);

        double num = 0;
        for (int i = 0; i < in.row; i++){
            num = 0;
            for (int j = 0; j < in.row; j++){
                num += in.matrix[i][j];
            }
            rtn.matrix[i][0] = num / in.col;
        }

        return rtn;
    }

    /**
     * Calucurate square root each number of this matrix.
     * @return New Matrix instance.
     */
    public Matrix sqrt(){
        Matrix rtn = new Matrix(new double[this.row][this.col]);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                rtn.matrix[i][j] = Math.sqrt(this.matrix[i][j]);
            }
        }

        return rtn;
    }

    /**
     * Calucurate square root each number of a matrx.
     * @param in Matrix instance.
     * @return New Matrix instance.
     */
    public static Matrix sqrt(Matrix in){
        Matrix rtn = new Matrix(new double[in.row][in.col]);

        for (int i = 0; i < in.row; i++){
            for (int j = 0; j < in.col; j++){
                rtn.matrix[i][j] = Math.sqrt(in.matrix[i][j]);
            }
        }

        return rtn;
    }

    /**
     * Power of a matrix element.
     * @param matrix A Matrix instance.
     * @return Multiplying a matrix by itself.
     */
    public static Matrix pow(Matrix matrix){
        Matrix rtn = matrix.clone();

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = Math.pow(rtn.matrix[i][j], 2);
            }
        }

        return rtn;
    }

    /**
     * Power of this matrix element.
     */
    public void pow(){
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = Math.pow(this.matrix[i][j], 2);
            }
        }
    }

    /**
     * Power of a matrix element.
     * @param matrix A Matrix instance.
     * @param num Number to a power.
     * @return Powered a matrix by itself.
     */
    public static Matrix pow(Matrix matrix, int num){
        Matrix rtn = matrix.clone();

        for (int i = 0; i < rtn.row; i++){
            for (int j = 0; j < rtn.col; j++){
                rtn.matrix[i][j] = Math.pow(rtn.matrix[i][j], num);
            }
        }

        return rtn;
    }

    /**
     * Power of this matrix element.
     * @param num Number to a power.
     */
    public void pow(int num){
        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                this.matrix[i][j] = Math.pow(this.matrix[i][j], num);
            }
        }
    }

    /**
     * Get a single column matrix from a matrix.
     * @param in Matrix instance.
     * @param num Number of column that extract.
     * @return Extracted matrix.
     */
    public static Matrix getCol(Matrix in, int num){
        Matrix rtn = new Matrix(new double[in.row][1]);

        for (int i = 0; i < in.row; i++){
            rtn.matrix[i][0] = in.matrix[i][num];
        }

        return rtn;
    }

    /**
     * Get a single column matrix from this matrix.
     * @param num Number of column that extract.
     * @return Extracted matrix.
     */
    public Matrix getCol(int num){
        Matrix rtn = new Matrix(new double[this.row][1]);

        for (int i = 0; i < this.row; i++){
            rtn.matrix[i][0] = this.matrix[i][num];
        }

        return rtn;
    }

    /**
     * Get a single row matrix from a matrix.
     * @param in Matrix instance.
     * @param num Number of row that extract.
     * @return Extracted matrix.
     */
    public static Matrix getRow(Matrix in, int num){
        Matrix rtn = new Matrix(new double[1][in.col]);

        for (int i = 0; i < in.col; i++){
            rtn.matrix[0][i] = in.matrix[num][i];
        }

        return rtn;
    }

    /**
     * Get a single row matrix from this matrix.
     * @param num Number of row that extract.
     * @return Extracted matrix.
     */
    public Matrix getRow(int num){
        Matrix rtn = new Matrix(new double[1][this.col]);

        for (int i = 0; i < this.col; i++){
            rtn.matrix[0][i] = this.matrix[num][i];
        }

        return rtn;
    }

    /**
     * Calcrate sum.
     * @return Result of sum.
     */
    public double sum(){
        double sum = 0.;

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                sum += this.matrix[i][j];
            }
        }

        return sum;
    }

    /**
     * Calcrate sum.
     * @param in matrix to investigate.
     * @return Result of sum.
     */
    public static double sum(Matrix in){
        double sum = 0.;

        for (int i = 0; i < in.row; i++){
            for (int j = 0; j < in.col; j++){
                sum += in.matrix[i][j];
            }
        }

        return sum;
    }

    @Override
    public String toString(){
        String str = "[";

        int i = 0;
        for (double[] ele: matrix){
            if (i == 0){
                str += "[";
            }else{
                str += "\n [";
            }
            i++;
            for (double num: ele){
                str += String.format("%.4f ", num);
            }
            str += "]";
        }
        str += "]\n";

        return str;
    }

    /**
     * Method to compare this Matrix instance and a Matrix instance.
     * Without override.
     * @param o A Matrix instance.
     * @return Is equal?
     */
    public boolean equals(Matrix o){
        if (o == this){
            return true;
        }
        if (this.row != o.row || this.col != o.col){
            return false;
        }

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                if (this.matrix[i][j] != o.matrix[i][j]){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Method to compare this Matrix instance and a double[][] instance.
     * Without override.
     * @param o A double[][] instance.
     * @return Is equal?
     */
    public boolean equals(double o[][]){
        if (this.row != o.length || this.col != o[0].length){
            return false;
        }
        for (int i = 0; i < o.length; i++){
            for (int j = 0; j < o[0].length; j++){
                if (this.matrix[i][j] != o[i][j]){
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Method to compare this Matrix instance and a double[][] instance.
     * Without override.
     * @param o A Double[][] instance.
     * @return Is equal?
     */
    public boolean equals(Double o[][]){
        if (this.row != o.length || this.col != o[0].length){
            return false;
        }
        for (int i = 0; i < o.length; i++){
            for (int j = 0; j < o[0].length; j++){
                if (this.matrix[i][j] != o[i][j]){
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public Matrix clone(){
        Matrix rtn = new Matrix(new double[this.row][this.col]);

        for (int i = 0; i < this.row; i++){
            for (int j = 0; j < this.col; j++){
                rtn.matrix[i][j] = this.matrix[i][j];
            }
        }

        return rtn;
    }

    @Override
    public int hashCode(){
        double sum = 0;

        for (double[] ele: this.matrix){
            for (double num: ele){
                sum += num;
            }
        }

        return (int)sum;
    }
}
