#include <string>

#include "osero_genetic.h"

#define SIZEOF(arr) (sizeof(arr) / sizeof(int))

const int eva_num = 10;
const int board_size = 64;

void set_eva(double ** eva, char ** name);

int main(void){
    int i, j;
    double ** eva = new double *[eva_num];
    char ** name = new char *[eva_num];
    FILE * datap;
    osero_genetic * run;
    int srand_init_arr[] = {
        81, 82, 83, 84, 85, 86, 87, 88, 89, 90,
        91, 92, 93, 94, 95, 96, 97, 98, 99, 100,
        616, 617, 618, 619, 620, 621, 622, 623, 624, 625,
        626, 627, 628, 629, 630, 631, 632, 633, 634, 635
    };

    for (i = 0; i < eva_num; i++){
        eva[i] = new double[board_size];
        name[i] = new char[20];
    }
    set_eva(eva, name);

    datap = fopen("data.csv", "w");
    fprintf(datap, "black,white,srand_num,black_score,white_score\n");

    for (i = 0; i < eva_num; i++){
        printf("[");
        for (j = 0; j < i + 1; j++){
            printf("#");
        }
        for (; j < eva_num; j++){
            printf(" ");
        }
        printf("]\n");

        for (j = 0; j < eva_num; j++){
            run = new osero_genetic(1, eva[i], 1, eva[j]);
            for (int srand_init = 0; srand_init < SIZEOF(srand_init_arr); srand_init++){
                run -> reset();
                run -> srand_num = srand_init_arr[srand_init];
                run -> play();
                fprintf(
                    datap,
                    "%s,%s,%d,%d,%d\n",
                    name[i],
                    name[j],
                    srand_init_arr[srand_init],
                    run -> bw_score[0],
                    run -> bw_score[1]
                );
            }
            delete run;
        }
    }

    fclose(datap);
    for (i = 0; i < eva_num; i++){
        delete[] eva[i];
        delete[] name[i];
    }
    delete[] eva;
    delete[] name;

    return 0;
}

void set_eva(double ** eva, char ** name){
    int i, j;
    FILE * eva_read = fopen("eva_file.txt", "r");
    FILE * name_read = fopen("name_file.txt", "r");

    i = 0, j = 0;
    while (fscanf(eva_read, "%lf", &eva[i][j]) != EOF){
        j++;
        if (j == board_size){
            j = 0;
            i++;
        }
    }

    i = 0;
    while (fscanf(name_read, "%s", name[i]) != EOF){
        i++;
    }

    fclose(eva_read);
    fclose(name_read);
}