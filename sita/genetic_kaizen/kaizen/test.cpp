#include "osero_genetic.h"

int osero_genetic::mode = 1;
int osero_genetic::computer = 0;
int osero_genetic::player = 1;
int osero_genetic::srand_num = 99;
bool osero_genetic::print = true;

inline double first_eva(void){
    return DOUBLE(rand()) / (RAND_MAX >> 1) - 1;
}

int main(void){
    double evab[64], evaw[64];
    int result;
    osero_genetic * run;

    for (int i = 0; i < 64; i++){
        evab[i] = first_eva();
        evaw[i] = 1.0;
    }
    
    run = new osero_genetic(0, evab, 0, evaw);
    run -> read_goal[0] = 2;
    run -> read_goal[1] = 1;

    result = run -> play();

    printf("%d\n", result);

    delete run;

    return 0;
}