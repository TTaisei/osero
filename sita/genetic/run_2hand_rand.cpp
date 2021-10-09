#include <iostream>
// #include <fstream>
#include "osero_genetic.h"

using namespace std;

const int epoc = 30;
const int entire = 100;

int main(void){
    int win, win_sum;
    int i, j, k;
    double eva[epoc][64], new_eva[epoc][64];
    osero_genetic * run;
    FILE * fp = fopen("data_2hand_rand.csv", "w");
    printf("progress ");
    for (i = 0; i < epoc; i++) printf(".");
    printf("\n");
    fprintf(fp, "num,win_per\n");

    // srand(static_cast<unsigned int>(time(NULL)));
    srand(0);
    for (i = 0; i < epoc; i++)
        for (j = 0; j < 64; j++)
            eva[i][j] = static_cast<double>(rand()) / (RAND_MAX >> 1) - 1;

    for (i = 0; i < entire; i++){
        printf("%3d/%3d: ", i + 1, entire);
        win_sum = 0;

        for (j = 0; j < epoc; j++){
            printf(".");
            run = new osero_genetic(
                static_cast<int>(PLAY_WAY::nhand),
                eva[j],
                static_cast<int>(PLAY_WAY::random)
            );
            run -> read_goal = 2;
            // srand(static_cast<unsigned int>(time(NULL)) + i + j);
            // srand(i + j);
            win = static_cast<int>(run -> play());

            if (win) {
                for (k = 0; k < 64; k++)
                    new_eva[win_sum][k] = eva[j][k];
                win_sum++;
            }
            delete run;
        }

        if (win_sum){
            for (j = 0; j < epoc; j++){
                for (k = 0; k < 64; k++){
                    if (rand() % 100 > 5)
                        eva[j][k] = new_eva[rand() % win_sum][k];
                    else
                        eva[j][k] = static_cast<double>(rand()) / (RAND_MAX >> 1) - 1;
                }
            }
        }

        printf("\n");
        fprintf(fp, "%d,%d\n", i, win_sum);
    }

    fclose(fp);

    fp = fopen("eva_2hand_rand.csv", "w");

    for (i = 0; i < epoc; i++){
        for (j = 0; j < 64; j++){
            fprintf(fp, "%2.4f", eva[i][63 - j]);
            if ((j + 1) % 8 == 0) fprintf(fp, "\n");
            else fprintf(fp, ",");
        }
        fprintf(fp, "\n");
    }

    fclose(fp);

    // for (i = 0; i < epoc; i++){
    //     for (j = 0; j < 64; j++){
    //         if (eva[i][63 - i] < 0)
    //             printf("%2.4f ", eva[i][63 - j]);
    //         else
    //             printf(" %2.4f ", eva[i][63 - j]);
    //         if ((j + 1) % 8 == 0) printf("\n");
    //     }
    //     printf("\n");
    // }

    return 0;
}