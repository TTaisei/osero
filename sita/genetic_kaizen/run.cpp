#include "osero_genetic.h"

int osero_genetic::mode = 1;
int osero_genetic::computer = 0;
int osero_genetic::player = 1;
int osero_genetic::srand_num = 99;
bool osero_genetic::print = false;

const int child = 32;
const int parent = 16;
const int entire = 100;

const int proba_arr[parent] = {
    99, 89, 81, 74, 67, 61, 55, 50, 46, 41,
    38, 34, 31, 28, 26, 23//, 21, 19, 17, 16
};

enum class sele_method{
    all,
    ranking,
    tournament,
    roulette,
    sentinel
};

enum class evol_method{
    random,
    one_crossing,
    two_crossing,
    mask,
    sentinel
};

inline double first_eva(void){
    return DOUBLE(rand()) / (RAND_MAX >> 1) - 1;
}

// sele_func member
void all(double ** eva, double ** new_eva, int * score);
void ranking(double ** eva, double ** new_eva, int * score);
void tournament(double ** eva, double ** new_eva, int * score);
void roulette(double ** eva, double ** new_eva, int * score);

// evol_func member
void random(double ** par_eva, double ** chi_eva);
void one_crossing(double ** par_eva, double ** chi_eva);
void two_crossing(double ** par_eva, double ** chi_eva);
void mask(double ** par_eva, double ** chi_eva);

// run mutation in specified probability
void mutation(double ** eva, int proba);

int main(void){
    int win_sum;
    int score[child];
    double ** eva = new double *[child], ** par_eva = new double *[parent];
    double eva_p[64], eva_my[64], eva_f[child][64];
    void (* sele_func[INT(sele_method::sentinel)])(double **, double **, int *) = {
        all,
        ranking,
        tournament,
        roulette
    };
    void (* evol_func[INT(evol_method::sentinel)])(double **, double **) = {
        random,
        one_crossing,
        two_crossing,
        mask
    };

    osero_genetic * run;
    FILE * dataf = fopen("data.csv", "w");
    FILE * evaf = fopen("eva.csv", "w");
    fprintf(
        dataf,
       "evolution_method,select_method,per_mutation,computer,player,generation,win_per\n"
    );
    fprintf(evaf, "evolution_method,select_method,per_mutation,computer,player,indi");
    for (int i = 0; i < 64; i++) fprintf(evaf, ",%d", i);
    fprintf(evaf, "\n");

    srand(99);

    // setup eva and par_eva
    for (int i = 0; i < child; i++){
        eva[i] = new double[64];
    }
    for (int i = 0; i < parent; i++){
        par_eva[i] = new double[64];
    }

    // setup eva_p and eva_my
    for (int i = 0; i < 64; i++){
        eva_p[i] = 1.0;

        if (i == 0 || i == 7 || i == 56 || i == 63){
            eva_my[i] = 1.0;
        }else if (i == 1 || i == 6 || i == 8 || i == 9 || i == 14 ||i == 15 ||
                  i == 48 || i == 49|| i == 54 || i == 55 || i == 57 || i == 62){
            eva_my[i] = -1.0;
        }else if (i == 2 || i == 5 || i == 58 || i == 61 ||
                  i == 16 || i == 23 || i == 40 || i == 47 ||
                  i == 18 || i == 21 || i == 42 || i == 45){
            eva_my[i] = 0.5;
        }else{
            eva_my[i] = 0.1;
        }
    }

    // setup eva
    for (int i = 0; i < child; i++){
        for (int j = 0; j < 64; j++){
            eva_f[i][j] = first_eva();
        }
    }

    // evolution_method
    for (int evol = 0; evol < INT(evol_method::sentinel); evol++){
        // print progress
        printf("[");
        for (int i = 0; i < evol + 1; i++) printf("#");
        for (int i = 0; i < INT(evol_method::sentinel) - evol - 1; i++) printf(" ");
        printf("]\n");

        // select_method
        for (int sele = 0; sele < INT(sele_method::sentinel); sele++){
            // print progress
            printf("    [");
            for (int i = 0; i < sele + 1; i++) printf("#");
            for (int i = 0; i < INT(sele_method::sentinel) - sele - 1; i++) printf(" ");
            printf("]\n");

            // per_mutation
            for (int muta = 0; muta <= 10; muta += 5){
                // print progress
                printf("        [");
                for (int i = 0; i < muta / 5 + 1; i++) printf("#");
                for (int i = 0; i < 3 - muta / 5 - 1; i++) printf(" ");
                printf("]\n");

                // computer
                for (int computer = 1; computer <= 2; computer++){
                    // player
                    for (int player = 0; player <= 4; player++){
                        // setup eva
                        for (int i = 0; i < child; i++){
                            for (int j = 0; j < 64; j++){
                                eva[i][j] = eva_f[i][j];
                            }
                        }

                        // start learning
                        for (int gene = 0; gene < entire; gene++){
                            win_sum = 0;
                            for (int indi = 0; indi < child; indi++){
                                if (player == 0){
                                    run = new osero_genetic(0, eva[indi], 1);
                                }else if (player == 1 || player == 2){
                                    run = new osero_genetic(0, eva[indi], 0, eva_p);
                                    run -> read_goal[1] = player;
                                }else{
                                    run = new osero_genetic(0, eva[indi], 0, eva_my);
                                    run -> read_goal[1] = player - 2;
                                }

                                run -> read_goal[0] = computer;

                                score[indi] = run -> play();
                                if (score[indi] > 0) win_sum++;

                                delete run;
                            }

                            sele_func[sele](eva, par_eva, score);

                            evol_func[evol](par_eva, eva);

                            if (muta) mutation(eva, muta);

                            // data output
                            fprintf(
                                dataf,
                                "%d,%d,%d,%d,%d,%d,%d\n",
                                evol,
                                sele,
                                muta,
                                computer,
                                player,
                                gene,
                                win_sum
                            );
                        }

                        // final eva output
                        for (int i = 0; i < child; i++){
                            fprintf(
                                evaf,
                                "%d,%d,%d,%d,%d,%d",
                                evol,
                                sele,
                                muta,
                                computer,
                                player,
                                i
                            );
                            for (int j = 0; j < 64; j++){
                                fprintf(evaf, ",%2.4f", eva[i][j]);
                            }
                            fprintf(evaf, "\n");
                        }
                    }
                }
            }
        }
    }

    // tidying up
    fclose(evaf);
    fclose(dataf);
    for (int i = 0; i < child; i++){
        delete[] eva[i];
    }
    for (int i = 0; i < parent; i++){
        delete[] par_eva[i];
    }
    delete[] eva;
    delete[] par_eva;

    return 0;
}

// selection method
void all(double ** eva, double ** new_eva, int * score){
    int num = 0;

    for (int i = 0; i < child; i++){
        if (score[i] > 0){
            for (int j = 0; j < 64; j++){
                new_eva[num][j] = eva[i][j];
            }
            num++;
        }
        if (num == parent) break;
    }

    if (num < parent){
        int times = parent - num;
        for (int i = 0; i < times; i++){
            for (int j = 0; j < 64; j++){
                new_eva[num][j] = eva[i][j];
            }
            num++;
        }
    }
}

void ranking(double ** eva, double ** new_eva, int * score){
    int i, j;
    int rank[child];
    int cnt;

    for (i = 0; i < child; i++){
        cnt = 0;
        for (j = 0; j < child; j++){
            if (score[j] > score[i]) cnt++;
        }
        rank[i] = cnt + 1;
    }

    cnt = 0;
    i = 0;
    do{
        if (rank[i] && rand() % 100 < proba_arr[rank[i] - 1]){
            for (j = 0; j < 64; j++){
                new_eva[cnt][j] = eva[i][j];
            }
            rank[i] = 0;
            cnt++;
        }
        i++;
        if (i == child) i = 0;
    }while (cnt != parent);
}

void tournament(double ** eva, double ** new_eva, int * score){
    int i, j;
    int place;

    for (i = 0; i < parent; i++){
        place = i << 1;
        if (score[place] > score[place + 1]){
            ;
        }else{
            place++;
        }
        for (j = 0; j < 64; j++){
            new_eva[i][j] = eva[place][j];
        }
    }
}

void roulette(double ** eva, double ** new_eva, int * score){
    int i, j;
    int min_score = 100;

    for (i = 0; i < child; i++){
        if (score[i] < min_score){
            min_score = score[i];
        }
    }

    for (i = 0; i < child; i++){
        score[i] = score[i] - min_score + 5;
    }

    int cnt;
    int place = 100, now_place;

    i = 0;
    for (cnt = 0; cnt < parent; cnt++){
        now_place = 0;
        do{
            i++;
            if (i == child) i = 0;
            now_place += score[i];
        }while (now_place < place);

        for (j = 0; j < 64; j++){
            new_eva[cnt][j] = eva[i][j];
        }
        score[i] = 0;
    }
}

// evolution method
void random(double ** par_eva, double ** chi_eva){
    for (int i = 0; i < child; i++){
        for (int j = 0; j < 64; j++){
            chi_eva[i][j] = par_eva[rand() % parent][j];
        }
    }
}

void one_crossing(double ** par_eva, double ** chi_eva){
    int i, j;
    int mama, papa;
    int cut_place;

    for (i = 0; i < child; i++){
        cut_place = rand() % 64;
        mama = rand() % parent;
        do{
            papa = rand() % parent;
        }while (mama == papa);

        for (j = 0; j < cut_place; j++){
            chi_eva[i][j] = par_eva[mama][j];
        }
        for (; j < 64; j++){
            chi_eva[i][j] = par_eva[papa][j];
        }
    }
}

void two_crossing(double ** par_eva, double ** chi_eva){
    int i, j;
    int mama, papa;
    int cut_place1, cut_place2;

    for (i = 0; i < child; i++){
        do{
            cut_place1 = rand() % 64;
            cut_place2 = rand() % 64;
        }while (cut_place1 >= cut_place2);
        mama = rand() % parent;
        do{
            papa = rand() % parent;
        }while (mama == papa);

        for (j = 0; j < cut_place1; j++){
            chi_eva[i][j] = par_eva[mama][j];
        }
        for (; j < cut_place2; j++){
            chi_eva[i][j] = par_eva[papa][j];
        }
        for (; j < 64; j++){
            chi_eva[i][j] = par_eva[mama][j];
        }
    }
}

void mask(double ** par_eva, double ** chi_eva){
    int i, j;
    int mama, papa;
    BOARD mask_b, place;

    for (i = 0; i < child; i++){
        mask_b = (static_cast<BOARD>(rand()) << 32) + rand();
        mama = rand() % parent;
        do{
            papa = rand() % parent;
        }while (mama == papa);

        j = 0;
        place = 1;
        while (place){
            if (place & mask_b){
                chi_eva[i][j] = par_eva[mama][j];
            }else{
                chi_eva[i][j] = par_eva[papa][j];
            }
            j++;
            place = place << 1;
        }
    }
}

void mutation(double ** eva, int proba){
    for (int i = 0; i < child; i++){
        for (int j = 0; j < 64; j++){
            if (rand() % 100 < proba){
                eva[i][j] = first_eva();
            }
        }
    }
}