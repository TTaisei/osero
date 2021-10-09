#include "osero_genetic.h"

extern const bool BLACK;
extern const bool WHITE;

bool osero_genetic::play(void){
    bool can = true, old_can = true;
    int line, col;

    // srand(10);
    srand(0);
    void (osero_genetic:: * player[2]) (int *, int *) = {
        play_method[this -> bmethod],
        play_method[this -> wmethod]
    };

    if (this -> print) this -> printb();

    while((can = check_all()) || old_can){    
        if (can){
            (this->*player[static_cast<int>(this -> turn)])(&line, &col);
            this -> put(this -> bw, line, col, this -> turn);
            if (this -> print) this -> printb();
        }
        this -> turn = !(this -> turn);
        old_can = can;
    }

    if (this -> mode)
        return this -> count_last();
    else
        return true;
}

void osero_genetic::random(int * line, int * col){
    int x, y;

    x = rand() % this -> SIZE;
    y = rand() % this -> SIZE;

    while (!(this -> check(this -> bw, x, y))){
        x = rand() % this -> SIZE;
        y = rand() % this -> SIZE; 
    }

    *line = x, *col = y;
}

void osero_genetic::nhand(int * line, int * col){
    int i = 0, j = 0;
    double score = 0, max_score = -(this -> SIZE << 3);
    int line_ans = 0, col_ans = 0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (check(this -> bw, i, j)){
            board_leaf[0] = this -> bw[0];
            board_leaf[1] = this -> bw[1];
            put(board_leaf, i, j, this -> turn);
            score = this -> board_add(board_leaf, 1, this -> turn);
            if (score == -100.0) score = count(board_leaf);
            if (score > max_score){
                max_score = score;
                line_ans = i;
                col_ans = j;
            }
        }
        i++;
        if (i == 8) i = 0, j++;
        place = place << 1;
    }

    *line = line_ans;
    *col = col_ans;
}

double osero_genetic::board_add(BOARD * now, int num, bool turn){
    if (num == this -> read_goal) return count(now);

    int i = 0, j = 0, put_num = 0;
    double score_ele, score = 1.0;
    BOARD place = 1;
    BOARD board_leaf[2];

    while (place){
        if (check(now, i, j)){
            board_leaf[0] = now[0];
            board_leaf[1] = now[1];
            put(board_leaf, i, j, !turn);
            score_ele = this -> board_add(board_leaf, num + 1, turn);
            if (score_ele == -100.0) score_ele = count(now);
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

double osero_genetic::count(BOARD * now){
    int my = static_cast<int>(this -> turn);
    int opp = static_cast<int>(!(this -> turn));
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

bool osero_genetic::count_last(void){
    int my, opp;

    my = this -> computer;
    opp = this -> player;

    my = popcount(this -> bw[my]);
    opp = popcount(this -> bw[opp]);

    return my > opp;
}

int osero_genetic::popcount(BOARD now){
    now = now - ((now >> 1) & 0x5555555555555555);
    now = (now & 0x3333333333333333) + ((now >> 2) & 0x3333333333333333);
    now = (now + (now >> 4)) & 0x0f0f0f0f0f0f0f0f;
    now = now + (now >> 8);
    now = now + (now >> 16);
    now = now + (now >> 32);

    return now & 0x7f;
}

bool osero_genetic::check_all(void){
    int i, j;

    for (i = 0; i < SIZE; i++)
        for (j = 0; j < SIZE; j++)
            if (this -> check(this -> bw, i, j)) return true;

    return false;
}

bool osero_genetic::check(BOARD * now, int line, int col){
    if (now[static_cast<int>(TURN::black)] & static_cast<BOARD>(1) << ((line << 3) + col)) return false;
    if (now[static_cast<int>(TURN::white)] & static_cast<BOARD>(1) << ((line << 3) + col)) return false;

    int x, y;
    int line_x, col_y;
    int my = static_cast<int>(this -> turn),
        opp = static_cast<int>(!(this -> turn));

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

void osero_genetic::put(BOARD * now, int line, int col, bool turn){
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

void osero_genetic::printb(void){
    BOARD place = 1;
    int num = 0;

    printf("\n  ");
    for (int i = 0; i < this -> SIZE; i++) printf(" %d ", i + 1);

    printf("\n -------------------------\n");
    while (place){
        if (num % this -> SIZE == 0) printf("%d", (num >> 3) + 1);
        if (this -> bw[static_cast<int>(TURN::black)] & place) printf("|○");
        else if (this -> bw[static_cast<int>(TURN::white)] & place) printf("|●");
        else printf("|  ");
        if (num % this -> SIZE == 7) printf("|\n -------------------------\n");
        num++;
        place = place << 1;
    }

    return;
}

osero_genetic::osero_genetic(int player_b, int player_w){
    this -> bw[static_cast<int>(TURN::black)] = 0x810000000;
    this -> bw[static_cast<int>(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    this -> mode = 0;

    return;
}

osero_genetic::osero_genetic(int player_b, double * eva, int player_w){
    this -> bw[static_cast<int>(TURN::black)] = 0x810000000;
    this -> bw[static_cast<int>(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    for (int i = 0; i < 64; i++)
        this -> eva[0][i] = eva[i];
    this -> player = 1;
    this -> computer = 0;
    this -> mode = 1;

    return;
}

osero_genetic::osero_genetic(int player_b, int player_w, double * eva){
    this -> bw[static_cast<int>(TURN::black)] = 0x810000000;
    this -> bw[static_cast<int>(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    for (int i = 0; i < 64; i++)
        this -> eva[1][i] = eva[i];
    this -> player = 0;
    this -> computer = 1;
    this -> mode = 1;

    return;
}

osero_genetic::osero_genetic(int player_b, double * eva1, int player_w, double * eva2){
    this -> bw[static_cast<int>(TURN::black)] = 0x810000000;
    this -> bw[static_cast<int>(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;
    for (int i = 0; i < 64; i++){
        this -> eva[0][i] = eva1[i];
        this -> eva[1][i] = eva2[i];
    }
    this -> player = 0;
    this -> computer = 1;
    this -> mode = 0;

    return;
}

osero_genetic:: ~osero_genetic(){
    return;
}