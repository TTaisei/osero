#include "osero_genetic.h"

// select method: tournament
// evolution method: one crossing
// computer: one hand
// player: random, one hand, two hand, least opp number of moves, most my number of moves
// mutation: 3%

const int child = 32;
const int parent = 16;
const int entire = 1000;
const int board_size = 64;
const int mutation = 3;

enum class player{
    random,
    one_hand,
    two_hand,
    one_least,
    one_most,
    sentinel
};

inline double first_eva(void){
    return DOUBLE(rand()) / (RAND_MAX >> 1) - 1;
}

void tournament(double ** eva, double ** new_eva, int * score);
void one_crossing(double ** par_eva, double ** chi_eva);

int main(void){
    int i, j;
    int computer = INT(PLAY_WAY::nhand_custom);
    int play_method;
    int score[child];
    int win_sum;
    double ** eva = new double *[child], ** par_eva = new double *[parent];
    osero_genetic * run;

    FILE * dataf = fopen("data.csv", "w");
    FILE * evaf = fopen("eva.csv", "w");

    // setup eva
    for (i = 0; i < child; i++){
        eva[i] = new double[board_size];
        for (j = 0; j < board_size; j++){
            eva[i][j] = first_eva();
        }
    }
    for (i = 0; i < parent; i++){
        par_eva[i] = new double[board_size];
    }

    // file print
    fprintf(dataf, "generation,player,win_per\n");
    for (i = 0; i < board_size - 1; i++){
        fprintf(evaf, "%d,", i);    
    }
    fprintf(evaf, "%d\n", i);

    srand(99);
    for (int gene = 0; gene < entire; gene++){
        printf("[%4d/%4d]", gene + 1, entire);
        win_sum = 0;
        for (int indi = 0; indi < child; indi++){
            printf(".");
            play_method = rand() % INT(player::sentinel);
            switch (play_method){
                case INT(player::random):
                    run = new osero_genetic(
                        computer,
                        eva[indi],
                        INT(PLAY_WAY::random)
                    );
                    break;
                case INT(player::one_hand):
                    run = new osero_genetic(
                        computer,
                        eva[indi],
                        INT(PLAY_WAY::nhand)
                    );
                    break;
                case INT(player::two_hand):
                    run = new osero_genetic(
                        computer,
                        eva[indi],
                        INT(PLAY_WAY::nhand)
                    );
                    run -> read_goal[1] = 2;
                    break;
                case INT(player::one_least):
                    run = new osero_genetic(
                        computer,
                        eva[indi],
                        INT(PLAY_WAY::nleast)
                    );
                    break;
                case INT(player::one_most):
                    run = new osero_genetic(
                        computer,
                        eva[indi],
                        INT(PLAY_WAY::nmost)
                    );
                    break;
                default:
                    printf("program miss\n");
            }

            run -> computer = 0;
            run -> player = 1;

            score[indi] = run -> play();
            if (score[indi] > 0) win_sum++;

            delete run;
        }

        tournament(eva, par_eva, score);

        one_crossing(par_eva, eva);

        fprintf(dataf, "%d,%d,%d\n", gene, play_method, win_sum);

        printf("\n");
    }

    // output eva
    for (i = 0; i < child; i++){
        for (j = 0; j < board_size - 1; j++){
            fprintf(evaf, "%f,", eva[i][j]);
        }
        fprintf(evaf, "%f\n", eva[i][j]);
    }

    // tidying up
    fclose(dataf);
    fclose(evaf);
    for (i = 0; i < child; i++){
        delete[] eva[i];
    }
    for (i = 0; i < parent; i++){
        delete[] par_eva[i];
    }
    delete[] eva;
    delete[] par_eva;

    return 0;
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
        for (j = 0; j < board_size; j++){
            new_eva[i][j] = eva[place][j];
        }
    }
}

void one_crossing(double ** par_eva, double ** chi_eva){
    int i, j;
    int mama, papa;
    int cut_place;

    for (i = 0; i < child; i++){
        cut_place = rand() % board_size;
        mama = rand() % parent;
        do{
            papa = rand() % parent;
        }while (mama == papa);

        for (j = 0; j < board_size; j++){
            if (rand() % 100 > mutation){
                if (j < cut_place)
                    chi_eva[i][j] = par_eva[mama][j];
                else
                    chi_eva[i][j] = par_eva[papa][j];
            }else{
                chi_eva[i][j] = first_eva();
            }
        }
    }
}