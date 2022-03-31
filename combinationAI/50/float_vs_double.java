public class float_vs_double {
    public static void main(String[] str){
        int[] maxNums = {1000, 1000000};
        long start, end;

        for (int maxNum: maxNums){
            System.out.printf("max num: %d\n", maxNum);

            // float start
            start = System.nanoTime();
            float numFloat = 0.0f;
            for (int i = 0; i < maxNum; i++){
                numFloat += 0.1f;
                numFloat = numFloat * 0.1f;
                numFloat = numFloat / 0.1f;
                numFloat -= 0.1f;
            }
            end = System.nanoTime();
            // float end

            System.out.printf("float time: %d [ns]\n", end - start);
            long floatTime = end - start;

            // double start
            start = System.nanoTime();
            double numDouble = 0.0;
            for (int i = 0; i < maxNum; i++){
                numDouble += 0.1;
                numDouble = numDouble * 0.1;
                numDouble = numDouble / 0.1;
                numDouble -= 0.1;
            }
            end = System.nanoTime();
            // double end

            System.out.printf("double time: %d [ns]\n", end - start);
            System.out.printf("The difference: %d [ns]\n\n", end - start - floatTime);
        }
    }
}