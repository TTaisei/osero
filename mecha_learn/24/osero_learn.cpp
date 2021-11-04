#include "osero_learn.h"

void osero_learn::play(void){
    bool can = true, old_can = true;
    int line, col;
    srand(this -> srand_num);

    // this -> printb();
    
    this -> turn_num = 0;
    while((can = this -> check_all()) || old_can){
        if (can){
            this -> history[turn_num]["turn"] = INT(this -> turn);
            this -> history[turn_num]["my_score"]
                = this -> cal_evascore(this -> bw, INT(this -> turn));
            this -> history[turn_num]["opp_score"]
                = this -> cal_evascore(this -> bw, INT(!(this -> turn)));
            this -> nhand_evacustom(&line, &col);
            this -> put(this -> bw, line, col, this -> turn);
            // this -> printb();
            turn_num++;
        }
        this -> turn = !(this -> turn);
        old_can = can;
    }

    this -> count_last();
}

void osero_learn::nhand_evacustom(int * line, int * col){
    int i = 0, j = 0;
    double score = 0.0;
    double top_score = -100, sec_score = top_score, thr_score = top_score;
    double opp_score[3];
    int line_ans[3], col_ans[3];
    int num = 0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (this -> check(this -> bw, i, j, this -> turn)){
            board_leaf[0] = this -> bw[0];
            board_leaf[1] = this -> bw[1];
            put(board_leaf, i, j, this -> turn);
            score = this -> cal_evascore(board_leaf, INT(this -> turn));
            if (score > top_score){
                thr_score = sec_score;
                sec_score = top_score;
                top_score = score;
                this -> history[this -> turn_num]["my_thr_score"] = thr_score;
                this -> history[this -> turn_num]["my_sec_score"] = sec_score;
                this -> history[this -> turn_num]["my_top_score"] = top_score;
                opp_score[2] = opp_score[1];
                opp_score[1] = opp_score[0];
                opp_score[0] = cal_evascore(board_leaf, INT(!(this -> turn)));
                this -> history[this -> turn_num]["opp_thr_score"] = opp_score[2];
                this -> history[this -> turn_num]["opp_sec_score"] = opp_score[1];
                this -> history[this -> turn_num]["opp_top_score"] = opp_score[0];
                line_ans[2] = line_ans[1];
                col_ans[2] = col_ans[1];
                line_ans[1] = line_ans[0];
                col_ans[1] = col_ans[0];
                line_ans[0] = i;
                col_ans[0] = j;
                num = 1;
            }else if (score > sec_score){
                thr_score = sec_score;
                sec_score = score;
                this -> history[this -> turn_num]["my_thr_score"] = thr_score;
                this -> history[this -> turn_num]["my_sec_score"] = sec_score;
                opp_score[2] = opp_score[1];
                opp_score[1] = cal_evascore(board_leaf, INT(!(this -> turn)));
                this -> history[this -> turn_num]["opp_thr_score"] = opp_score[2];
                this -> history[this -> turn_num]["opp_sec_score"] = opp_score[1];
                line_ans[2] = line_ans[1];
                col_ans[2] = col_ans[1];
                line_ans[1] = i;
                col_ans[1] = j;
                num = 2;
            }else if (score > thr_score){
                thr_score = score;
                this -> history[this -> turn_num]["my_thr_score"] = thr_score;
                opp_score[2] = cal_evascore(board_leaf, INT(!(this -> turn)));
                this -> history[this -> turn_num]["opp_thr_score"] = opp_score[2];
                line_ans[2] = i;
                col_ans[2] = j;
                num = 3;
            }
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    this -> history[this -> turn_num]["put_place"] = 0;

    if (num == 1) {
        ;
    }else{
        int put_place = rand() % num;
        this -> history[this -> turn_num]["put_place"] = put_place;
        line_ans[0] = line_ans[put_place];
        col_ans[0] = col_ans[put_place];
    }

    this -> history[this -> turn_num]["num"] = num;

    *line = line_ans[0];
    *col = col_ans[0];
}

double osero_learn::cal_evascore(BOARD * now, int turn){
    int my = turn;
    int opp = 1 - turn;
    int i = 0;
    double evascore = 0.0;
    BOARD place = 1;

    while (place){
        if (place & now[my]){
            evascore += this -> t_eva[i];
        }else if (place & now[opp]){
            evascore -= this -> t_eva[i];
        }
        i++;
        place = place << 1;
    }

    return evascore;
}

void osero_learn::count_last(void){
    int black, white;

    black = popcount(this -> bw[0]);
    white = popcount(this -> bw[1]);

    this -> bw_score[0] = black;
    this -> bw_score[1] = white;
}

osero_learn::osero_learn(){
    this -> reset();
}

osero_learn:: ~osero_learn(){
    return;
}