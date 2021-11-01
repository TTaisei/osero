#include "osero.h"

void osero::play(void){
    bool can = true, old_can = true;
    int line, col;

    srand(this -> srand_num);
    void (osero:: * player[2]) (int *, int *) = {
        play_method[this -> bmethod],
        play_method[this -> wmethod]
    };

    this -> printb();

    while((can = check_all()) || old_can){    
        if (can){
            (this->*player[INT(this -> turn)])(&line, &col);
            this -> put(this -> bw, line, col, this -> turn);
            this -> printb();
        }
        this -> turn = !(this -> turn);
        old_can = can;
    }

    this -> count_last();
}

void osero::random(int * line, int * col){
    int x, y;

    do{
        x = rand() % this -> SIZE;
        y = rand() % this -> SIZE;
    } while (!(this -> check(this -> bw, x, y, this -> turn)));

    *line = x, *col = y;
}

void osero::nleast(int * line, int * col){
    int i = 0, j = 0;
    int place_num, min_place_num = 100;
    int line_ans[this -> SIZE << 1], col_ans[this -> SIZE << 1];
    int num = 0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (check(this -> bw, i, j, this -> turn)){
            board_leaf[0] = this -> bw[0];
            board_leaf[1] = this -> bw[1];
            this -> put(board_leaf, i, j, this -> turn);
            place_num = this -> check_place(board_leaf, 1, !(this -> turn), !(this -> turn));
            if (place_num < min_place_num){
                min_place_num = place_num;
                line_ans[0] = i;
                col_ans[0] = j;
                num = 0;
            }else if (place_num == min_place_num){
                num++;
                line_ans[num] = i;
                col_ans[num] = j;
            }
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    if (num){
        int place = rand() % (num + 1);
        line_ans[0] = line_ans[place];
        col_ans[0] = col_ans[place];
    }

    *line = line_ans[0];
    *col = col_ans[0];
}

void osero::nmost(int * line, int * col){
    int i = 0, j = 0;
    int place_num, max_place_num = -1;
    int line_ans[this -> SIZE << 1], col_ans[this -> SIZE << 1];
    int num = 0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (check(this -> bw, i, j, this -> turn)){
            board_leaf[0] = this -> bw[0];
            board_leaf[1] = this -> bw[1];
            this -> put(board_leaf, i, j, turn);
            place_num = this -> check_place(board_leaf, 1, !(this -> turn), this -> turn);
            if (place_num > max_place_num){
                max_place_num = place_num;
                line_ans[0] = i;
                col_ans[0] = j;
                num = 0;
            }else if (place_num == max_place_num){
                num++;
                line_ans[num] = i;
                col_ans[num] = j;
            }
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    if (num) {
        int place = rand() % (num + 1);
        line_ans[0] = line_ans[place];
        col_ans[0] = col_ans[place];
    }

    *line = line_ans[0];
    *col = col_ans[0];
}

int osero::check_place(BOARD * now, int num, bool turn, bool tar_turn){
    int i = 0, j = 0;
    int put_num = 0;
    BOARD place = 1;

    if (turn == tar_turn){
        if (num == this -> read_goal[INT(this -> turn)]){
            while (place){
                if (this -> check(now, i, j, turn)){
                    put_num++;
                }
                i++;
                if (i == 8) i = 0, j++;
                place = place << 1;
            }

            return put_num;
        }else{
            int place_sum = 0;
            BOARD board_leaf[2];

            while (place){
                if (this -> check(now, i, j, turn)){
                    board_leaf[0] = now[0];
                    board_leaf[1] = now[1];
                    put(board_leaf, i, j, turn);
                    place_sum += this -> check_place(board_leaf, num + 1, !turn, tar_turn);
                    put_num++;
                }
                i++;
                if (i == 8) i = 0, j++;
                place = place << 1;
            }

            if (put_num)
                return place_sum / put_num;
            else
                return 0;
        }
    }else{
        int place_sum;
        BOARD board_leaf[2];

        while (place){
            if (this -> check(now, i, j, turn)){
                board_leaf[0] = now[0];
                board_leaf[1] = now[1];
                put(now, i, j, turn);
                place_sum = this -> check_place(board_leaf, num, !turn, tar_turn);
            }
            i++;
            if (i == 8) i = 0, j++;
            place = place << 1;
        }

        return place_sum;
    }
}

void osero::nhand(int * line, int * col){
    int i = 0, j = 0;
    int score = 0, max_score = -100;
    int line_ans[this -> SIZE << 1], col_ans[this -> SIZE << 1];
    int num = 0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (this -> check(this -> bw, i, j, this -> turn)){
            board_leaf[0] = this -> bw[0];
            board_leaf[1] = this -> bw[1];
            put(board_leaf, i, j, this -> turn);
            score = this -> board_add(board_leaf, 1, !(this -> turn), false);
            if (score == -100){
                int my, opp;
                my = popcount(board_leaf[INT(this -> turn)]);
                opp = popcount(board_leaf[INT(!(this -> turn))]);
                score = my - opp;
            }
            if (score > max_score){
                max_score = score;
                line_ans[0] = i;
                col_ans[0] = j;
                num = 0;
            }else if (score == max_score){
                num++;
                line_ans[num] = i;
                col_ans[num] = j;
            }
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    if (num){
        int place = rand() % (num + 1);
        line_ans[0] = line_ans[place];
        col_ans[0] = col_ans[place];
    }

    *line = line_ans[0];
    *col = col_ans[0];
}

void osero::nhand_custom(int * line, int * col){
    int i = 0, j = 0;
    double score = 0.0, max_score = -100;
    int line_ans[this -> SIZE << 1], col_ans[this -> SIZE << 1];
    int num = 0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (this -> check(this -> bw, i, j, this -> turn)){
            board_leaf[0] = this -> bw[0];
            board_leaf[1] = this -> bw[1];
            put(board_leaf, i, j, this -> turn);
            score = this -> board_add(board_leaf, 1, !(this -> turn), true);
            if (score == -100.0) score = count(board_leaf);
            if (score > max_score){
                max_score = score;
                line_ans[0] = i;
                col_ans[0] = j;
                num = 0;
            }else if (score == max_score){
                num++;
                line_ans[num] = i;
                col_ans[num] = j;
            }
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    if (num) {
        int place = rand() % (num + 1);
        line_ans[0] = line_ans[place];
        col_ans[0] = col_ans[place];
    }

    *line = line_ans[0];
    *col = col_ans[0];
}

double osero::board_add(BOARD * now, int num, bool turn, bool iscustom){
    if (num == this -> read_goal[INT(this -> turn)]){
        if (iscustom){
            return count(now);
        }else{
            int my, opp;
            my = popcount(now[INT(this -> turn)]);
            opp = popcount(now[INT(!(this -> turn))]);
            return my - opp;
        }
    }

    int i = 0, j = 0, put_num = 0;
    double score_ele, score = 1.0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (check(now, i, j, turn)){
            board_leaf[0] = now[0];
            board_leaf[1] = now[1];
            put(board_leaf, i, j, turn);
            score_ele = this -> board_add(board_leaf, num + 1, !turn, iscustom);
            if (score_ele == -100.0){
                if (iscustom){
                    score_ele = count(now);
                }else{
                    int my, opp;
                    my = popcount(now[INT(this -> turn)]);
                    opp = popcount(now[INT(!(this -> turn))]);
                    score_ele = my - opp;
                }
            }
            score += score_ele;
            put_num++;
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    if (put_num) return score / put_num;
    else return -100.0;
}

void osero::human(int * line, int * col){
    printf("line: ");
    scanf("%d", line);
    printf("col: ");
    scanf("%d", col);

    *line -= 1;
    *col -= 1;

    while (!(this -> check(this -> bw, *line, *col, this -> turn))){
        printf("It cannot that putting that place. Please once choose.\n");
        printf("line: ");
        scanf("%d", line);
        printf("col: ");
        scanf("%d", col);

        *line -= 1;
        *col -= 1;
    }
}

double osero::count(BOARD * now){
    int my = INT(this -> turn);
    int opp = INT(!(this -> turn));
    int i = 0;
    double score = 0;
    double * myeva = this -> eva[my];
    BOARD place = 1;

    while (place){
        if (now[my] & place)
            score += myeva[i];
        else if (now[opp] & place)
            score -= myeva[i];
        i++;
        place = place << 1;
    }

    return score;
}

void osero::count_last(void){
    int black = 0, white = 1;

    black = popcount(this -> bw[black]);
    white = popcount(this -> bw[white]);

    printf("black: %d, white: %d\n", black, white);
    if (black > white){
        printf("black win!\n");
    }else if (black < white){
        printf("white win!\n");
    }else{
        printf("draw!\n");
    }
}

int osero::popcount(BOARD now){
    now = now - ((now >> 1) & 0x5555555555555555);
    now = (now & 0x3333333333333333) + ((now >> 2) & 0x3333333333333333);
    now = (now + (now >> 4)) & 0x0f0f0f0f0f0f0f0f;
    now = now + (now >> 8);
    now = now + (now >> 16);
    now = now + (now >> 32);

    return now & 0x7f;
}

bool osero::check_all(void){
    int i, j;

    for (i = 0; i < SIZE; i++)
        for (j = 0; j < SIZE; j++)
            if (this -> check(this -> bw, i, j, this -> turn)) return true;

    return false;
}

bool osero::check(BOARD * now, int line, int col, bool turn){
    if (now[INT(TURN::black)] & static_cast<BOARD>(1) << ((line << 3) + col)) return false;
    if (now[INT(TURN::white)] & static_cast<BOARD>(1) << ((line << 3) + col)) return false;

    int x, y;
    int line_x, col_y;
    int my = INT(turn),
        opp = INT(!turn);

    for (x = -1; x <= 1; x++){
        for (y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                line_x = line + x;
                col_y = col + y;
                while ((now[opp] & static_cast<BOARD>(1) << ((line_x << 3) + col_y))
                       && 0 <= x + line_x && x + line_x < this -> SIZE
                       && 0 <= y + col_y && y + col_y < this -> SIZE){
                    if (now[my] & static_cast<BOARD>(1) << ((line_x + x << 3) + col_y + y))
                        return true;
                    line_x += x;
                    col_y += y;
                }
            }
        }
    }

    return false;
}

void osero::put(BOARD * now, int line, int col, bool turn){
    int x, y;
    int line_x, col_y, num;
    bool my = turn, opp = !turn;
    BOARD inver, place;

    now[my] = now[my] | static_cast<BOARD>(1) << ((line << 3) + col); 

    for (x = -1; x <= 1; x++){
        for (y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                inver = 0;
                line_x = line + x;
                col_y = col + y;
                place = static_cast<BOARD>(1) << ((line_x << 3) + col_y);
                while ((now[opp] & place)
                       && 0 <= x + line_x && x + line_x < this -> SIZE
                       && 0 <= y + col_y && y + col_y < this -> SIZE){
                    inver = inver | place;
                    line_x += x;
                    col_y += y;
                    place = static_cast<BOARD>(1) << ((line_x << 3) + col_y);
                }
                if (now[my] & place){
                    now[my] = now[my] + inver;
                    now[opp] = now[opp] - inver;
                }
            }
        }
    }
}

void osero::printb(void){
    BOARD place = 1;
    int num = 0;

    printf("\n  ");
    for (int i = 0; i < this -> SIZE; i++) printf(" %d ", i + 1);

    printf("\n -------------------------\n");
    while (place){
        if (num % this -> SIZE == 0) printf("%d", (num >> 3) + 1);
        if (this -> bw[INT(TURN::black)] & place) printf("|○");
        else if (this -> bw[INT(TURN::white)] & place) printf("|●");
        else printf("|  ");
        if (num % this -> SIZE == 7) printf("|\n -------------------------\n");
        num++;
        place = place << 1;
    }

    return;
}

void osero::reset(void){
    this -> bw[INT(TURN::black)] = 0x810000000;
    this -> bw[INT(TURN::white)] = 0x1008000000;    
}

osero::osero(){
    return;
}

osero::osero(int player_b, int player_w){
    this -> bw[INT(TURN::black)] = 0x810000000;
    this -> bw[INT(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;

    return;
}

osero::osero(int player_b, double * eva, int player_w){
    this -> bw[INT(TURN::black)] = 0x810000000;
    this -> bw[INT(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    for (int i = 0; i < 64; i++)
        this -> eva[0][i] = eva[i];
    this -> player = 1;
    this -> computer = 0;

    return;
}

osero::osero(int player_b, int player_w, double * eva){
    this -> bw[INT(TURN::black)] = 0x810000000;
    this -> bw[INT(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    for (int i = 0; i < 64; i++)
        this -> eva[1][i] = eva[i];
    this -> player = 0;
    this -> computer = 1;

    return;
}

osero::osero(int player_b, double * eva1, int player_w, double * eva2){
    this -> bw[INT(TURN::black)] = 0x810000000;
    this -> bw[INT(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    for (int i = 0; i < 64; i++){
        this -> eva[0][i] = eva1[i];
        this -> eva[1][i] = eva2[i];
    }
    this -> player = 1;
    this -> computer = 0;

    return;
}

osero:: ~osero(){
    return;
}