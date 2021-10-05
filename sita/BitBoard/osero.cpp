#include "osero.h"

extern const bool BLACK;
extern const bool WHITE;

void osero::play(void){
    double start = clock();

    srand((unsigned int)time(NULL));
    bool can = true, old_can = true;
    int line, col;
    void (osero:: * player[2]) (int *, int *) = {
        play_method[this -> bmethod],
        play_method[this -> wmethod]
    };

    printf("黒: ○\n白: ●\n\n");

    this -> printb();

    while((can = check_all()) || old_can){
        if (this -> turn == static_cast<bool>(TURN::black))
            printf("黒のターンです\n");
        else
            printf("白のターンです\n");
        
        if (can){
            (this->*player[static_cast<int>(this -> turn)])(&line, &col);
            this -> put(line, col);
            this -> printb();
        }else{
            printf("置ける場所がありません\n");
        }
        this -> turn = !(this -> turn);
        old_can = can;
    }

    printf("\n試合時間: %.4lf秒\n", (clock() - start) / 1000000);
    count();
}

void osero::random(int * line, int * col){
    int x, y;

    x = rand() % this -> SIZE;
    y = rand() % this -> SIZE;

    while (!(this -> check(x, y))){
        x = rand() % this -> SIZE;
        y = rand() % this -> SIZE; 
    }

    *line = x, *col = y;

    printf("行: %d\n列: %d\n", x + 1, y + 1);
}

void osero::que(int * line, int * col){
    printf("行: ");
    scanf("%d", line);
    printf("列: ");
    scanf("%d", col);
    *line -= 1;
    *col -= 1;
    if (!(this -> check(*line, *col))){
        printf("その場所には置けません\n");
        this -> que(line, col);
    }
}

void osero::count(void){
    int black, white;

    this -> black_num = this -> popcount(this -> bw[static_cast<int>(TURN::black)]);
    this -> white_num = this -> popcount(this -> bw[static_cast<int>(TURN::white)]);

    printf("%d\n", this -> black_num);
    printf("%d\n", this -> white_num);
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
            if (this -> check(i, j)) return true;

    return false;
}

bool osero::check(int line, int col){
    if (this -> bw[static_cast<int>(TURN::black)] & static_cast<BOARD>(1) << ((line << 3) + col)) return false;
    if (this -> bw[static_cast<int>(TURN::white)] & static_cast<BOARD>(1) << ((line << 3) + col)) return false;

    int x, y;
    int line_x, col_y;
    bool my = this -> turn, opp = !(this -> turn);

    for (x = -1; x <= 1; x++){
        for (y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                line_x = line + x;
                col_y = col + y;
                while ((this -> bw[opp] & static_cast<BOARD>(1) << ((line_x << 3) + col_y))
                       && 0 <= x + line_x && x + line_x < this -> SIZE
                       && 0 <= y + col_y && y + col_y < this -> SIZE){
                    if (this -> bw[my] & static_cast<BOARD>(1) << ((line_x + x << 3) + col_y + y))
                        return true;
                    line_x += x;
                    col_y += y;
                }
            }
        }
    }

    return false;
}

void osero::put(int line, int col){
    int x, y;
    int line_x, col_y, num;
    bool my = this -> turn, opp = !(this -> turn);
    BOARD inver, place;

    this -> bw[my] = this -> bw[my] | static_cast<BOARD>(1) << ((line << 3) + col); 

    for (x = -1; x <= 1; x++){
        for (y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                inver = 0;
                line_x = line + x;
                col_y = col + y;
                place = static_cast<BOARD>(1) << ((line_x << 3) + col_y);
                while ((this -> bw[opp] & place)
                       && 0 <= x + line_x && x + line_x < this -> SIZE
                       && 0 <= y + col_y && y + col_y < this -> SIZE){
                    inver = inver | place;
                    line_x += x;
                    col_y += y;
                    place = static_cast<BOARD>(1) << ((line_x << 3) + col_y);
                }
                if (this -> bw[my] & place){
                    this -> bw[my] = this -> bw[my] + inver;
                    this -> bw[opp] = this -> bw[opp] - inver;
                }
            }
        }
    }
}

osero::osero(int player_b, int player_w){
    this -> bw[static_cast<int>(TURN::black)] = 0x810000000;
    this -> bw[static_cast<int>(TURN::white)] = 0x1008000000;
    this -> bmethod = player_b;
    this -> wmethod = player_w;

    return;
}

osero:: ~osero(){
    return;
}

void osero::printb(void){
    BOARD i;
    int num = 0;

    printf("\n  ");
    for (int i = 0; i < this -> SIZE; i++) printf(" %d ", i + 1);

    printf("\n -------------------------\n");
    for (i = 1; i != static_cast<BOARD>(1) << 63; i = i << 1){
        if (num % this -> SIZE == 0){
        // if (!(0b111 & (num >> 3))){
            // num++;
            printf("%d", (num >> 3) + 1);
        }
        if (this -> bw[static_cast<int>(TURN::black)] & i) printf("|○");
        else if (this -> bw[static_cast<int>(TURN::white)] & i) printf("|●");
        else printf("|  ");
        if (num % this -> SIZE == 7) printf("|\n -------------------------\n");
        num++;
    }
    if (this -> bw[static_cast<int>(TURN::black)] & i) printf("|○");
    else if (this -> bw[static_cast<int>(TURN::white)] & i) printf("|●");
    else printf("|  ");
    printf("|\n -------------------------\n");

    return;
}