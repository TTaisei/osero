public class one_vs_two_for {
    public static void main(String[] str){
        int[] sizes = {1000};//, 1000000};
        long start, end;
        
        for (int size: sizes){
            System.out.printf("size: %d\n", size);
            Matrix x = new Matrix(new double[size][size]);
            x.fillNum(0.1);
            double num;

            // one start
            start = System.nanoTime();
            for (int i = 0; i < size * size; i++){
                num = x.matrix[i / size][i % size];
            }
            end = System.nanoTime();
            // one end

            System.out.printf("one time div: %d\n", end - start);

            // one start
            start = System.nanoTime();
            int row = 0, col = 0;
            for (int i = 0; i < size * size; i++){
                num = x.matrix[row][col];
                col++;
                if (col >= size){
                    col = 0;
                    row++;
                }
            }
            end = System.nanoTime();
            // one end

            System.out.printf("one time inc: %d\n", end - start);

            // two start
            start = System.nanoTime();
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    num = x.matrix[i][j];
                }
            }
            end = System.nanoTime();
            // two end

            System.out.printf("two time: %d\n", end - start);
        }
    }
}