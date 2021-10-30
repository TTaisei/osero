#include "osero_genetic.h"

int osero_genetic::play(void){
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

    return this -> count_last();
}

int osero_genetic::count_last(void){
    int my, opp;

    my = this -> computer;
    opp = this -> player;

    my = popcount(this -> bw[my]);
    opp = popcount(this -> bw[opp]);

    return my - opp;
}

// osero_genetic::osero_genetic(int player_b, int player_w){
//     this -> bw[INT(TURN::black)] = 0x810000000;
//     this -> bw[INT(TURN::white)] = 0x1008000000;
//     this -> bmethod = player_b;
//     this -> wmethod = player_w;

//     return;
// }

// osero_genetic::osero_genetic(int player_b, double * eva, int player_w){
//     this -> bw[INT(TURN::black)] = 0x810000000;
//     this -> bw[INT(TURN::white)] = 0x1008000000;
//     this -> bmethod = player_b;
//     this -> wmethod = player_w;
//     for (int i = 0; i < 64; i++)
//         this -> eva[0][i] = eva[i];
//     this -> player = 1;
//     this -> computer = 0;

//     return;
// }

// osero_genetic::osero_genetic(int player_b, int player_w, double * eva){
//     this -> bw[INT(TURN::black)] = 0x810000000;
//     this -> bw[INT(TURN::white)] = 0x1008000000;
//     this -> bmethod = player_b;
//     this -> wmethod = player_w;
//     for (int i = 0; i < 64; i++)
//         this -> eva[1][i] = eva[i];
//     this -> player = 0;
//     this -> computer = 1;

//     return;
// }

// osero_genetic::osero_genetic(int player_b, double * eva1, int player_w, double * eva2){
//     this -> bw[INT(TURN::black)] = 0x810000000;
//     this -> bw[INT(TURN::white)] = 0x1008000000;
//     this -> bmethod = player_b;
//     this -> wmethod = player_w;
//     for (int i = 0; i < 64; i++){
//         this -> eva[0][i] = eva1[i];
//         this -> eva[1][i] = eva2[i];
//     }
//     this -> player = 0;
//     this -> computer = 1;

//     return;
// }

osero_genetic:: ~osero_genetic(){
    return;
}