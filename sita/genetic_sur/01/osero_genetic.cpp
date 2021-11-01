#include "osero_genetic.h"

void osero_genetic::play(void){
    bool can = true, old_can = true;
    int line, col;

    srand(this -> srand_num);
    void (osero_genetic:: * player[2]) (int *, int *) = {
        play_method[this -> bmethod],
        play_method[this -> wmethod]
    };

    // this -> printb();

    while((can = this -> check_all()) || old_can){    
        if (can){
            (this->*player[INT(this -> turn)])(&line, &col);
            this -> put(this -> bw, line, col, this -> turn);
            // this -> printb();
        }
        this -> turn = !(this -> turn);
        old_can = can;
    }

    this -> count_last();
}

void osero_genetic::count_last(void){
    int black, white;

    black = popcount(this -> bw[0]);
    white = popcount(this -> bw[1]);

    this -> bw_score[0] = black;
    this -> bw_score[1] = white;
}

osero_genetic:: ~osero_genetic(){
    return;
}