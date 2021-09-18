#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>

#define READ_NUM 5
#define SIZE 8
#define NONE 0
#define BLACK 1
#define WHITE 2

bool check(char board[SIZE][SIZE], int line, int col, bool turn);
bool all_none(char board[SIZE][SIZE]);
void count_last(char board[SIZE][SIZE]);
int count(char board[SIZE][SIZE], bool turn);
int check_all(char board[SIZE][SIZE], bool turn);
void setup(char board[SIZE][SIZE]);
void printb(char board[SIZE][SIZE]);
void que(int * line, int * col);
void put(char board[SIZE][SIZE], int line, int col, bool turn);
void think(char board[SIZE][SIZE], int * line, int * col, bool turn);
int board_add(char board[SIZE][SIZE], int line, int col, bool turn, int num);

int main(){
    int line, col, my_check = 1, old_check = 1;
    bool turn = true;
    char board[SIZE][SIZE] = {NONE};
    srand((unsigned int)time(NULL));

    setup(board);

    long now = time(NULL);
    while ((my_check = check_all(board, turn)) || all_none(board)){
        if (turn) printf("\nあなたのターンです\n");
        else printf("\nわたしのターンです\n");
        if (my_check){
            if (turn){
                que(&line, &col);
                while (!check(board, line, col, turn)){
                    printf("その位置には置けません\n");
                    que(&line, &col);
                }
                put(board, line, col, turn);
                printb(board);
            }else{
                think(board, &line, &col, turn);
                put(board, line, col, turn);
                printf("行: %d\n列: %d\n", line, col);
                printb(board);
            }
        }else{
            printf("置ける場所がありません\n");
        }
        if (my_check == 0 && old_check == 0) break;
        old_check = my_check;
        turn = !turn;
    }

    printf("\n試合時間: %ld秒\n", time(NULL) - now);
    count_last(board);

    return 0;
}

void think(char board[SIZE][SIZE], int * line, int * col, bool turn){
    int i, j;
    int score = 0, max_score = -SIZE * SIZE, run_num = 0;
    int line_num[SIZE << 1], col_num[SIZE << 1];

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (check(board, i + 1, j + 1, turn)){
                score = board_add(board, i + 1, j + 1, turn, 0);
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

    *line = line_num[0] + 1;
    *col = col_num[0] + 1;

    return;
}

int board_add(char board[SIZE][SIZE], int line, int col, bool turn, int num){
    int i, j;
    int score = 0, run_num = 0, score_ele;
    char board_leaf[SIZE][SIZE];
    char * line_array;

    for (i = 0; i < SIZE; i++){
        line_array = board_leaf[i];
        for (j = 0; j < SIZE; j++){
            line_array[j] = board[i][j];
        }
    }
    put(board_leaf, line, col, turn);

    if (num == READ_NUM) return count(board_leaf, false);

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (check(board_leaf, i + 1, j + 1, turn)){
                score_ele = board_add(board_leaf, i + 1, j + 1, !turn, num + 1);
                if (score_ele == -1) score_ele = count(board_leaf, false);
                score += score_ele;
                run_num++;
            }
        }
    }

    if (run_num == 0) return -1;

    return score / run_num;
}

int count(char board[SIZE][SIZE], bool turn){
    int i, j;
    int my, opp, score = 0;

    if (turn) my = BLACK, opp = WHITE;
    else my = WHITE, opp = BLACK;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (board[i][j] == my) score++;
            else if (board[i][j] == opp) score--;
        }
    }

    return score;
}

int check_all(char board[SIZE][SIZE], bool turn){
    // 置ける場所の数を返す
    int i = 0, j = 0, num = 0;

    while (j != SIZE){
        if (check(board, i + 1, j + 1, turn)) num++;
        i++;
        if (i == SIZE){
            i = 0;
            j++;
        }
    }

    return num;
}

bool check(char board[SIZE][SIZE], int line, int col, bool turn){
    // 置ける　 true
    // 置けない false
    line--;
    col--;
    if (line >= SIZE || col >= SIZE) return false;
    if (board[line][col] != NONE) return false;

    int my, opp;
    int i = 2;
    if (turn == true) my = BLACK, opp = WHITE;
    else my = WHITE, opp = BLACK;

    // 縦
    if (line - 1 >= 0 && board[line - 1][col] == opp){
        while (line - i >= 0){
            if (board[line - i][col] == NONE) break;
            if (board[line - i][col] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && board[line + 1][col] == opp){
        while (line + i < SIZE){
            if (board[line + i][col] == NONE) break;
            if (board[line + i][col] == my) return true;
            i++;
        }
        i = 2;
    }

    // 横
    if (col - 1 >= 0 && board[line][col - 1] == opp){
        while (col - i >= 0){
            if (board[line][col - i] == NONE) break;
            if (board[line][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (col + 1 < SIZE && board[line][col + 1] == opp){
        while (col + i < SIZE){
            if (board[line][col + i] == NONE) break;
            if (board[line][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // 右上がり
    if (line + 1 < SIZE && col - 1 >= 0 && board[line + 1][col - 1] == opp){
        while (line + i < SIZE && col - i >= 0){
            if (board[line + i][col - i] == NONE) break;
            if (board[line + i][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line - 1 >= 0 && col + 1 < SIZE && board[line - 1][col + 1] == opp){
        while (line - i >= 0 && col + i < SIZE){
            if (board[line - i][col + i] == NONE) break;
            if (board[line - i][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // 右下がり
    if (line - 1 >= 0 && col - 1 >= 0 && board[line - 1][col - 1] == opp){
        while (line - i >= 0 && col - i >= 0){
            if (board[line - i][col - i] == NONE) break;
            if (board[line - i][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && col + 1 < SIZE && board[line + 1][col + 1] == opp){
        while (line + i < SIZE && col + i < SIZE){
            if (board[line + i][col + i] == NONE) break;
            if (board[line + i][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    return false;
}

bool all_none(char board[SIZE][SIZE]){
    int i, j;
    bool check = false;

    for (i = 0; i < SIZE; i++)
        for (j = 0; j < SIZE; j++)
            if (board[i][j] == NONE) return true;
    
    return false;
}

void que(int * line, int * col){
    printf("行: ");
    scanf("%d", line);
    printf("列: ");
    scanf("%d", col);
}

void printb(char board[SIZE][SIZE]){
    int num[SIZE] = {1, 2, 3, 4, 5, 6, 7, 8};
    int i, j;

    printf("\n  ");
    for (i = 0; i < SIZE; i++) printf(" %d ", num[i]);

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

    return;
}

void setup(char board[SIZE][SIZE]){
    printf("あなた: ○\nわたし: ●\n\n");
    board[SIZE / 2 - 1][SIZE / 2 - 1] = WHITE;
    board[SIZE / 2][SIZE / 2] = WHITE;
    board[SIZE / 2 - 1][SIZE / 2] = BLACK;
    board[SIZE / 2 ][SIZE / 2 - 1] = BLACK;
    printb(board);
}

void put(char board[SIZE][SIZE], int line, int col, bool turn){
    int i, j;
    int my, opp;
    int pos1, pos2;

    line--;
    col--;

    if (turn){
        my = BLACK, opp = WHITE;
        board[line][col] = BLACK;
    }else{
        my = WHITE, opp = BLACK;
        board[line][col] = WHITE;
    }

    // 縦
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line - 1 >= 0 && board[line - 1][col] == opp){
        while (line - i >= 0){
            if (board[line - i][col] == NONE) break;
            if (board[line - i][col] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && board[line + 1][col] == opp){
        while (line + i < SIZE){
            if (board[line + i][col] == NONE) break;
            if (board[line + i][col] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board[line - i][col] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board[line + i][col] = my;
        }
    }

    // 横
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (col - 1 >= 0 && board[line][col - 1] == opp){
        while (col - i >= 0){
            if (board[line][col - i] == NONE) break;
            if (board[line][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (col + 1 < SIZE && board[line][col + 1] == opp){
        while (col + i < SIZE){
            if (board[line][col + i] == NONE) break;
            if (board[line][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board[line][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board[line][col + i] = my;
        }
    }

    // 右上がり
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line + 1 < SIZE && col - 1 >= 0 && board[line + 1][col - 1] == opp){
        while (line + i < SIZE && col - i >= 0){
            if (board[line + i][col - i] == NONE) break;
            if (board[line + i][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line - 1 >= 0 && col + 1 < SIZE && board[line - 1][col + 1] == opp){
        while (line - i >= 0 && col + i < SIZE){
            if (board[line - i][col + i] == NONE) break;
            if (board[line - i][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board[line + i][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board[line - i][col + i] = my;
        }
    }

    // 右下がり
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line - 1 >= 0 && col - 1 >= 0 && board[line - 1][col - 1] == opp){
        while (line - i >= 0 && col - i >= 0){
            if (board[line - i][col - i] == NONE) break;
            if (board[line - i][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && col + 1 < SIZE && board[line + 1][col + 1] == opp){
        while (line + i < SIZE && col + i < SIZE){
            if (board[line + i][col + i] == NONE) break;
            if (board[line + i][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board[line - i][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board[line + i][col + i] = my;
        }
    }

    return;
}

void count_last(char board[SIZE][SIZE]){
    int i, j;
    int black = 0, white = 0;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (board[i][j] == BLACK) black++;
            else if (board[i][j] == WHITE) white++;
        }
    }

    printf("あなたの数: %2d\nわたしの数: %2d\n", black, white);

    if (black > white) printf("あなたの勝ちです\n");
    else if (black < white) printf("わたしの勝ちです\n");
    else printf("引き分けです\n");

    return;
}