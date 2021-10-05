#include "osero.h"

int main(void){
    osero * run = new osero(
        static_cast<int>(PLAY_WAY::human),
        static_cast<int>(PLAY_WAY::random)
        // static_cast<int>(PLAY_WAY::random),
        // static_cast<int>(PLAY_WAY::random)
    );
    run -> play();
    delete run;

    return 0;
}