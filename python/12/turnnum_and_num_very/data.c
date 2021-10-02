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

void setup(void);
void think(int num);
void put(int line, int col, char now[SIZE][SIZE]);
void printb(void);
void copy(char parent[SIZE][SIZE], char child[SIZE][SIZE]);
void print(FILE * fp, int num);
bool check(int line, int col);
bool check_all(void);
bool win(void);
int cal_score(char now[SIZE][SIZE]);

char board[SIZE][SIZE] = {NONE};
char score_arr[SIZE * SIZE - 4][3] = {0};
char point[SIZE * SIZE - 4] = {0};
int turn;

void main(void) {
    double start = clock();

    int turn_num;
    bool can, old_can;
    FILE * fp;
    if (PLAYER == BLACK) fp = fopen("whitedata.txt", "w");
    else fp = fopen("blackdata.txt", "w");

    if (fp == NULL){
        printf("text file error\n");
        exit(-1);
    }

    for (int num = 0; num < NUM; num++){
        setup();
        srand((unsigned int)time(NULL) + num + rand());

        while (!win()){
            turn = BLACK;
            can = true, old_can = true;
            turn_num = 0;
            setup();
            // printb();
            while ((can = check_all()) || old_can){
                if (can){
                    think(turn_num);
                    // printb();
                    turn_num++;
                }
                old_can = can;
                turn = 3 - turn;
            }    
        }

        if (turn_num == SIZE * SIZE - 4) num--;

        else print(fp, turn_num);
    }

    fclose(fp);
    printf("実行時間: %.4lf[s]\n", (clock() - start) / 1000000);
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

void print(FILE * fp, int num){
    int my = 3 - PLAYER;

    for (int i = 0; i < num; i++){
        if (score_arr[i][2] == my){
            fprintf(fp, "%3d", i);
            fprintf(fp, "%3d%3d", score_arr[i][0], score_arr[i][1]);
            fprintf(fp, "%d\n", point[i]);
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