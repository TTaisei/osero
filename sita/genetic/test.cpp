#include "osero_genetic.h"

double eva[64];

int main(void){
    for (int i = 0; i < 64; i++) eva[i] = 1.0;
    osero_genetic * run = new osero_genetic(
        static_cast<int>(PLAY_WAY::nhand),
        eva,
        static_cast<int>(PLAY_WAY::random)
    );

    run -> read_goal = 2;
    run -> print = true;
    run -> play();

    return 0;
}