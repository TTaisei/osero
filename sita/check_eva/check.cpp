#include "osero_genetic.h"

int main(void){
    FILE * eva_file, * name_file, * fp;
    osero_genetic * run;
    double eva[64], eva_p[64];
    char filename[50];
    char eva_ele[10], * not_eva;
    bool win_lose;

    for (int i = 0; i < 64; i++) eva_p[i] = 1.0;

    eva_file = fopen("all_eva.txt", "r");
    name_file = fopen("filename.txt", "r");
    fp = fopen("all_data.csv", "w");
    fprintf(fp, "filename,eva_num,computer,player,srand_num,win_lose\n");

    for (int file = 0; file < 6; file++){
        printf("[");
        for (int i = 0; i < file + 1; i++) printf("#");
        for (int i = 0; i < 6 - file - 1; i++) printf(" ");
        printf("]\n");

        fgets(filename, sizeof(filename), name_file);
        for (int i = 0; i < sizeof(filename); i++){
            if (filename[i] == '.'){
                filename[i] = '\0';
                break;
            }
        }

        for (int eva_num = 0; eva_num < 30; eva_num++){
            for (int i = 0; i < 64; i++){
                fgets(eva_ele, 10, eva_file);
                eva[i] = strtod(eva_ele, &not_eva);
            }

            for (int computer = 1; computer <= 2; computer++){
                for (int player = 0; player < 3; player++){
                    for (int srand_num = 81; srand_num <= 100; srand_num++){
                        if (player == 0){
                            run = new osero_genetic(0, eva, 1);
                        }else{
                            run = new osero_genetic(0, eva, 0, eva_p);
                        }
                        run -> srand_num = srand_num;
                        run -> read_goal[0] = computer;
                        run -> read_goal[1] = player;
                        run -> computer = 0;
                        run -> player = 1;

                        win_lose = run -> play();

                        fprintf(
                            fp,
                            "%s,%d,%d,%d,%d,%d\n",
                            filename,
                            eva_num,
                            computer,
                            player,
                            srand_num,
                            INT(win_lose)
                        );
                        delete run;
                    }
                }
            }
        }
    }

    fclose(eva_file);
    fclose(name_file);
    fclose(fp);

    return 0;
}