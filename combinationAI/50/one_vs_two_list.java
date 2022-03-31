import org.MyNet.matrix.*;

public class one_vs_two_list {
    public static void main(String[] str){
        int[] sizes = {1000};//, 1000000};
        long start, end;
        
        for (int size: sizes){
            System.out.printf("size: %d\n", size);
            double num;

            // one start
            start = System.nanoTime();
            double listOne[] = new double[size * size];
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    num = listOne[i * size + j];
                }
            }
            end = System.nanoTime();
            // one end

            System.out.printf("one time: %d\n", end - start);

            // two start
            start = System.nanoTime();
            double listTwo[][] = new double[size][size];
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    num = listTwo[i][j];
                }
            }
            end = System.nanoTime();
            // two end

            System.out.printf("two time: %d\n", end - start);
        }
    }
}