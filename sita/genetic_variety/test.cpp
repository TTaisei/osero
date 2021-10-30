#include "osero.h"

int main(void){
    osero * run = new osero(
        INT(PLAY_WAY::human),
        INT(PLAY_WAY::nmost)
    );

    run->read_goal[1] = 2;

    run->play();

    delete run;
    return 0;
}