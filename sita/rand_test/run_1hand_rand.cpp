#include "osero_genetic.h"

const int child = 100;
const int entire = 1000;

double eva[64];

int main(void){
    int win, win_sum;
    int i, j;
    osero_genetic * run;
    FILE * fp = fopen("data_1hand_rand.csv", "w");

    fprintf(fp, "srand_num,win_per\n");

    for (i = 0; i < 64; i++) eva[i] = 1.0;

    for (i = 0; i < entire; i++){
        win_sum = 0;

        for (j = 0; j < child; j++){
            run = new osero_genetic(
                INT(PLAY_WAY::nhand),
                eva,
                INT(PLAY_WAY::random)
            );
            run -> read_goal[INT(TURN::black)] = 1;
            run -> mode = 1;
            run -> player = 1, run -> computer = 0;
            run -> srand_num = i;
            win = INT(run -> play());

            if (win) win_sum++;
            delete run;
        }

        fprintf(fp, "%d,%d\n", i, win_sum);
    }

    fclose(fp);

    return 0;
}