#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>

#define NONE 0
#define WHITE 1
#define BLACK 2
#define SIZE 8
#define PLAYER BLACK
// #define PLAYER WHITE
#define NUM 1000

int READ_NUM = 0;

void setup(void);
void think(int num);
void put(int line, int col, char now[SIZE][SIZE]);
void printb(void);
void copy(char parent[SIZE][SIZE], char child[SIZE][SIZE]);
void print(FILE * fpwin, FILE * fplose);
void random_put(void);
void nhand(void);
bool check(int line, int col);
bool check_nhand(int line, int col, int turn, char now[SIZE][SIZE]);
bool check_all(void);
bool win(void);
int cal_score(char now[SIZE][SIZE]);
int board_add(int line, int col, int turn, int num, char now[SIZE][SIZE]);
int count(char board[SIZE][SIZE]);

char board[SIZE][SIZE] = {NONE};
char score_arr[SIZE * SIZE - 4][3] = {0};
char point[SIZE * SIZE - 4] = {0};
int turn;

void main(void) {
    double start = clock();

    int turn_num;
    bool can, old_can;
    FILE * fpwin, * fplose;
    if (PLAYER == BLACK){
        fpwin = fopen("whitedatawin.txt", "w");
        fplose = fopen("whitedatalose.txt", "w");
    }else{
        fpwin = fopen("blackdatawin.txt", "w");
        fplose = fopen("blackdatalose.txt", "w");
    } 

    if (fpwin == NULL || fplose == NULL){
        printf("text file error\n");
        exit(-1);
    }

    for (int num = 0; num < NUM; num++){
        setup();
        srand((unsigned int)time(NULL) + num);

        while (!win()){
            turn = BLACK;
            can = true, old_can = true;
            turn_num = 0;
            setup();
            // printb();
            while ((can = check_all()) || old_can){
                if (can){
                    if (turn == PLAYER){
                        int func = rand() % 4;
                        if (func == 0){
                            random_put();
                        }else{
                            READ_NUM = func;
                            nhand();
                        }
                    }
                    else think(turn_num);
                    // printb();
                    turn_num++;
                }
                old_can = can;
                turn = 3 - turn;
            }
        }

        if (turn_num == SIZE * SIZE - 4)
          print(fpwin, fplose);
    }

    fclose(fpwin);
    fclose(fplose);
    printf("実行時間: %.4lf[s]\n", (clock() - start) / 1000000);
}

void nhand(void){
    int i, j;
    int score = 0, max_score = -SIZE * SIZE, run_num = 0;
    int line_num[SIZE << 1], col_num[SIZE << 1];

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (check(i, j)){
                score = board_add(i, j, turn, 0, board);
                if (score > max_score){
                    run_num = 0;
                    max_score = score;
                    line_num[run_num] = i;
                    col_num[run_num] = j;
                }else if (score == max_score){
                    run_num++;
                    line_num[run_num] = i;
                    col_num[run_num] = j;
                }
            }
        }
    }

    if (run_num > 0){
        run_num = rand() % (run_num + 1);
        line_num[0] = line_num[run_num];
        col_num[0] = col_num[run_num];
    }

    put(line_num[0], col_num[0], board);

    return;
}

int board_add(int line, int col, int turn, int num, char now[SIZE][SIZE]){
    int i, j;
    int score = 0, run_num = 0, score_ele;
    char board_leaf[SIZE][SIZE];
    char * line_array;

    for (i = 0; i < SIZE; i++){
        line_array = board_leaf[i];
        for (j = 0; j < SIZE; j++){
            line_array[j] = now[i][j];
        }
    }
    put(line, col, board_leaf);

    if (num == READ_NUM) return count(board_leaf);

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (check_nhand(i, j, turn, board_leaf)){
                score_ele = board_add(i, j, 3 - turn, num + 1, board_leaf);
                if (score_ele == -1) score_ele = count(board_leaf);
                score += score_ele;
                run_num++;
            }
        }
    }

    if (run_num == 0) return -1;

    return score / run_num;
}

void random_put(void){
    int line, col;

    line = rand() % SIZE, col = rand() % SIZE;

    while (!check(line, col)) line = rand() % SIZE, col = rand() % SIZE;

    put(line, col, board);
}

void think(int num){
    char line[3], col[3];
    char board_leaf[SIZE][SIZE];
    int score, top_score = -SIZE * SIZE, sec_score = top_score;
    int thr_score = top_score;
    int i, j;
    bool one = false, two = false, thr = false;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (check(i, j)){
                copy(board, board_leaf);
                put(i, j, board_leaf);
                score = cal_score(board_leaf);
                if (score > top_score){
                    top_score = score;
                    line[1] = line[0];
                    col[1] = col[0];
                    line[0] = i, col[0] = j;
                    one = true;
                }else if (score > sec_score){
                    sec_score = score;
                    line[1] = i, col[1] = j;
                    two = true;
                }else if (score > thr_score){
                    thr_score = score;
                    line[2] = i, col[2] = j;
                    thr = true;
                }
            }
        }
    }

    // if (turn == 3 - PLAYER){
    //     int my = 0, opp = 0;
    //     for (i = 0; i < SIZE; i++){
    //         for (j = 0; j < SIZE; j++){
    //             if (board[i][j] == turn) my++;
    //             else if (board[i][j] == PLAYER) opp++;
    //         }
    //     }

    //     score_arr[num][0] = my;
    //     score_arr[num][1] = opp;
    //     score_arr[num][2] = turn;
    // }    

    if (one && !two) {
        point[num] = -1;
        put(line[0], col[0], board);
    }else if (one && two && !thr){
        point[num] = -1;
        int place = rand() % 2;
        put(line[place], col[place], board);
    }else if (thr){
        int place = rand() % 3;
        put(line[place], col[place], board);
        point[num] = place;
    }

    if (turn == 3 - PLAYER){
        int my = 0, opp = 0;
        for (i = 0; i < SIZE; i++){
            for (j = 0; j < SIZE; j++){
                if (board[i][j] == turn) my++;
                else if (board[i][j] == PLAYER) opp++;
            }
        }

        score_arr[num][0] = my;
        score_arr[num][1] = opp;
        score_arr[num][2] = turn;
    }
}

int count(char board[SIZE][SIZE]){
    int i, j;
    int my, opp, score = 0;

    my = 3 - PLAYER, opp = PLAYER;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (board[i][j] == my) score++;
            else if (board[i][j] == opp) score--;
        }
    }

    return score;
}

void print(FILE * fpwin, FILE * fplose){
    int my = 3 - PLAYER;

    for (int i = 0; i < SIZE * SIZE - 4; i++){
        if (score_arr[i][2] == my){
            if (score_arr[i][0] > score_arr[i][1]){
                fprintf(fpwin, "%3d", i);
                fprintf(fpwin, "%3d%3d", score_arr[i][0], score_arr[i][1]);
                fprintf(fpwin, "%d\n", point[i]);
            }else{
                fprintf(fplose, "%3d", i);
                fprintf(fplose, "%3d%3d", score_arr[i][0], score_arr[i][1]);
                fprintf(fplose, "%d\n", point[i]);
            }
        }
    }
}

int cal_score(char now[SIZE][SIZE]){
    int my = turn, opp = 3 - turn;
    int score = 0;

    for (int i = 0; i < SIZE; i++)
        for (int j = 0; j < SIZE; j++){
            if (now[i][j] == my) score++;
            else if (now[i][j] == opp) score--;
        }
    
    return score;
}

void copy(char parent[SIZE][SIZE], char child[SIZE][SIZE]) {
    for (int i = 0; i < SIZE; i++)
        for (int j = 0; j < SIZE; j++)
            child[i][j] = parent[i][j];
}

void printb(void){
    int num[SIZE];
    int i, j;

    printf("\n  ");
    for (i = 0; i < SIZE; i++){
        num[i] = i + 1;
        printf(" %d ", num[i]);
    }

    printf("\n -------------------------\n");
    for (i = 0; i < SIZE; i++){
        printf("%d", num[i]);
        for (j = 0; j < SIZE; j++){
            if (board[i][j] == NONE) printf("|  ");
            else if (board[i][j] == WHITE) printf("|●");
            else if (board[i][j] == BLACK) printf("|○");
        }
        printf("|\n -------------------------\n");
    }
}

void setup(void){
    for (int i = 0; i < SIZE; i++)
        for (int j = 0; j < SIZE; j++)
            board[i][j] = NONE;

    board[(SIZE >> 1) - 1][(SIZE >> 1) - 1] = WHITE;
    board[SIZE >> 1][SIZE >> 1] = WHITE;
    board[(SIZE >> 1) - 1][SIZE >> 1] = BLACK;
    board[SIZE >> 1][(SIZE >> 1) - 1] = BLACK;
}

bool win(void){
    int my = 3 - PLAYER, opp = PLAYER;
    int score = 0;

    for (int i = 0; i < SIZE; i++){
        for (int j = 0; j < SIZE; j++){
            if (board[i][j] == my) score++;
            else if (board[i][j] == opp) score--;
        }
    }

    return score > 0;
}

bool check_all(){
    for (int i = 0; i < SIZE; i++)
        for (int j = 0; j < SIZE; j++)
            if (check(i, j)) return true;

    return false;
}

bool check(int line, int col){
    if (board[line][col] != NONE) return false;

    int line_x, col_y;
    int my = turn, opp = 3 - turn;

    for (int x = -1; x <= 1; x++){
        for (int y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                line_x = x;
                col_y = y;
                while (board[line + line_x][col + col_y] == opp
                       && 0 <= line + x + line_x < SIZE
                       && 0 <= col + y + col_y < SIZE){
                    if (board[line + x + line_x][col + y + col_y] == my) return true;
                    line_x += x;
                    col_y += y;
                }
            }
        }
    }

    return false;
}

bool check_nhand(int line, int col, int turn, char now[SIZE][SIZE]){
    if (board[line][col] != NONE) return false;

    int line_x, col_y;
    int my = turn, opp = 3 - turn;

    for (int x = -1; x <= 1; x++){
        for (int y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                line_x = x;
                col_y = y;
                while (board[line + line_x][col + col_y] == opp
                       && 0 <= line + x + line_x < SIZE
                       && 0 <= col + y + col_y < SIZE){
                    if (board[line + x + line_x][col + y + col_y] == my) return true;
                    line_x += x;
                    col_y += y;
                }
            }
        }
    }

    return false;
}

void put(int line, int col, char now[SIZE][SIZE]){
    int my = turn, opp = 3 - turn, num;
    int line_x, col_y;
    bool check_bool;

    now[line][col] = my;

    for (int x = -1; x <= 1; x++){
        for (int y = -1; y <= 1; y++){
            if (x != 0 || y != 0){
                num = 0;
                check_bool = false;
                line_x = x;
                col_y = y;
                while (now[line + line_x][col + col_y] == opp
                       && 0 <= line + x + line_x < SIZE
                       && 0 <= col + y + col_y < SIZE){
                    if (now[line + x + line_x][col + y + col_y] == my){
                        check_bool = true;
                        break;
                    }
                    line_x += x;
                    col_y += y;
                    num++;
                }
            }
            if (check_bool){
                for (int i = 1; i < num + 2; i++){
                    now[line + i * x][col + i * y] = my;
                }
            }
        }
    }
}