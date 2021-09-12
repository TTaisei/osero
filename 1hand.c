#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stdbool.h>

#define NONE 0
#define WHITE 1
#define BLACK 2
#define SIZE 8

typedef struct BOARD{
    int score;
    char board[SIZE][SIZE];
} Board_state;

char board_global[SIZE][SIZE] = {NONE};
bool turn;

void setup(void);
void print(void);
void que(int * line, int * col);
void put(int line, int col, Board_state * now);
void put_global(int line, int col);
void stop(void);
bool check(int line, int col, Board_state * now);
bool check_global(int line, int col);
bool check_all(void);
bool all_none(void);
void count(Board_state * now);
void count_last(void);
void think(void);
int board_add(int line, int col);

////////////////  main  ////////////////
int main(){
    int line, col;
    bool my_check = true, old_check = true;
    srand((unsigned int)time(NULL));

    // check_allで反転させられるので
    turn = false;
    setup();

    long now = time(NULL);
    while ((my_check = check_all()) || all_none()){
        if (turn) printf("\nあなたのターンです\n");
        else printf("\nわたしのターンです\n");
        if (my_check){
            if (turn){
                que(&line, &col);
                while (!check_global(line, col)){
                    printf("その位置には置けません\n");
                    que(&line, &col);
                }
                put_global(line, col);
            }else{
                think();
            }
        }else{
            printf("置ける場所がありません\n");
        }
        if (!my_check && !old_check) break;
        old_check = my_check;
    }

    printf("\n試合時間: %ld秒\n", time(NULL) - now);
    count_last();

    return 0;
}
////////////////////////////////////////

void think(void){
    int i, j, num = 0, subnum = 0;
    int score, max_score = -SIZE * SIZE;
    int line[SIZE >> 1] = {-1}, col[SIZE >> 1] = {-1};
    int line_put[SIZE], col_put[SIZE];

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (check_global(i + 1, j + 1)){
                line[num] = i;
                col[num] = j;
                num++;
            }
        }
    }

    if (num == 0){
        printf("置ける場所がありません\n");
        return;
    }

    for (i = 0; i < num; i++){
        score = board_add(line[i], col[i]);
        if ((line[i] == 0 && col[i] == 0)
             || (line[i] == 0 && col[i] == SIZE - 1)
             || (line[i] == SIZE - 1 && col[i] == 0)
             || (line[i] == SIZE - 1 && col[i] == SIZE - 1))
            score = SIZE * SIZE;
        if (max_score < score){
            max_score = score;
            subnum = 0;
            line_put[0] = line[i];
            col_put[0] = col[i];
        }else if (max_score == score){
            subnum++;
            line_put[subnum] = line[i];
            col_put[subnum] = col[i];
        }
    }

    if (subnum){
        subnum = rand() % (subnum + 1);
        line_put[0] = line_put[subnum];
        col_put[0] = col_put[subnum];
    }

    printf("行: %d\n列: %d\n", line_put[0] + 1, col_put[0] + 1);

    put_global(line_put[0] + 1, col_put[0] + 1);

    return;
}

int board_add(int line, int col){
    int i, j;
    char * line_array;
    Board_state leaf;

    if (&leaf == NULL) stop();

    for (i = 0; i < SIZE; i++){
        line_array = (&leaf) -> board[i];
        for (j = 0; j < SIZE; j++){
            line_array[j] = board_global[i][j];
        }
    }
    put(line, col, &leaf);
    count(&leaf);
    return (&leaf) -> score;
}

void stop(void){
    printf("メモリ確保に失敗しました\n");
    exit(-1);
}

bool all_none(void){
    int i, j;
    bool check = false;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++)
            if (board_global[i][j] == NONE) check = true;
        if (check) break;
    }
    
    if (i == SIZE && j == SIZE) return false;
    else return true;
}

void count(Board_state * now){
    int i, j;
    int my = 0, opp = 0;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (now -> board[i][j] == WHITE) my++;
            else if (now -> board[i][j]  == BLACK) opp++;
        }
    }
    now -> score =  my - opp;
}

void count_last(void){
    int i, j;
    int black = 0, white = 0;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (board_global[i][j] == BLACK) black++;
            else if (board_global[i][j] == WHITE) white++;
        }
    }

    printf("あなたの数: %2d\nわたしの数: %2d\n", black, white);

    if (black > white) printf("あなたの勝ちです\n");
    else if (black < white) printf("わたしの勝ちです\n");
    else printf("引き分けです\n");
}

bool check_all(void){
    // 置ける　 true
    // 置けない false
    int i = 1, j = 1;

    turn = !turn;

    while (!check_global(i, j) && j != SIZE + 1){
        i++;
        if (i == SIZE + 1){
            i = 0;
            j++;
        }
    }

    if (j == SIZE + 1) return false;
    else return true;
}

void que(int * line, int * col){
    printf("行: ");
    scanf("%d", line);
    printf("列: ");
    scanf("%d", col);
}

void setup(void){
    printf("あなた :○\nわたし :●\n\n");
    board_global[SIZE / 2 - 1][SIZE / 2 - 1] = WHITE;
    board_global[SIZE / 2][SIZE / 2] = WHITE;
    board_global[SIZE / 2 - 1][SIZE / 2] = BLACK;
    board_global[SIZE / 2 ][SIZE / 2 - 1] = BLACK;
    print();
}

void put_global(int line, int col){
    int i, j;
    int my, opp;
    int pos1, pos2;

    line--;
    col--;

    if (turn){
        my = BLACK, opp = WHITE;
        board_global[line][col] = BLACK;
    }else{
        my = WHITE, opp = BLACK;
        board_global[line][col] = WHITE;
    }

    // 縦
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line - 1 >= 0 && board_global[line - 1][col] == opp){
        while (line - i >= 0){
            if (board_global[line - i][col] == NONE) break;
            if (board_global[line - i][col] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && board_global[line + 1][col] == opp){
        while (line + i < SIZE){
            if (board_global[line + i][col] == NONE) break;
            if (board_global[line + i][col] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board_global[line - i][col] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board_global[line + i][col] = my;
        }
    }

    // 横
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (col - 1 >= 0 && board_global[line][col - 1] == opp){
        while (col - i >= 0){
            if (board_global[line][col - i] == NONE) break;
            if (board_global[line][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (col + 1 < SIZE && board_global[line][col + 1] == opp){
        while (col + i < SIZE){
            if (board_global[line][col + i] == NONE) break;
            if (board_global[line][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board_global[line][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board_global[line][col + i] = my;
        }
    }

    // 右上がり
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line + 1 < SIZE && col - 1 >= 0 && board_global[line + 1][col - 1] == opp){
        while (line + i < SIZE && col - i >= 0){
            if (board_global[line + i][col - i] == NONE) break;
            if (board_global[line + i][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line - 1 >= 0 && col + 1 < SIZE && board_global[line - 1][col + 1] == opp){
        while (line - i >= 0 && col + i < SIZE){
            if (board_global[line - i][col + i] == NONE) break;
            if (board_global[line - i][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board_global[line + i][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board_global[line - i][col + i] = my;
        }
    }

    // 右下がり
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line - 1 >= 0 && col - 1 >= 0 && board_global[line - 1][col - 1] == opp){
        while (line - i >= 0 && col - i >= 0){
            if (board_global[line - i][col - i] == NONE) break;
            if (board_global[line - i][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && col + 1 < SIZE && board_global[line + 1][col + 1] == opp){
        while (line + i < SIZE && col + i < SIZE){
            if (board_global[line + i][col + i] == NONE) break;
            if (board_global[line + i][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            board_global[line - i][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            board_global[line + i][col + i] = my;
        }
    }

    print();
}

bool check_global(int line, int col){
    // 置ける　 true
    // 置けない false
    line--;
    col--;
    if (line >= SIZE || col >= SIZE){
        // printf("その位置には置けません\n");
        return false;
    }
    if (board_global[line][col] != NONE){
        // printf("その位置には置けません\n");
        return false;
    }

    int my, opp;
    int i = 2;
    if (turn) my = BLACK, opp = WHITE;
    else my = WHITE, opp = BLACK;

    // 縦
    if (line - 1 >= 0 && board_global[line - 1][col] == opp){
        while (line - i >= 0){
            if (board_global[line - i][col] == NONE) break;
            if (board_global[line - i][col] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && board_global[line + 1][col] == opp){
        while (line + i < SIZE){
            if (board_global[line + i][col] == NONE) break;
            if (board_global[line + i][col] == my) return true;
            i++;
        }
        i = 2;
    }

    // 横
    if (col - 1 >= 0 && board_global[line][col - 1] == opp){
        while (col - i >= 0){
            if (board_global[line][col - i] == NONE) break;
            if (board_global[line][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (col + 1 < SIZE && board_global[line][col + 1] == opp){
        while (col + i < SIZE){
            if (board_global[line][col + i] == NONE) break;
            if (board_global[line][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // 右上がり
    if (line + 1 < SIZE && col - 1 >= 0 && board_global[line + 1][col - 1] == opp){
        while (line + i < SIZE && col - i >= 0){
            if (board_global[line + i][col - i] == NONE) break;
            if (board_global[line + i][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line - 1 >= 0 && col + 1 < SIZE && board_global[line - 1][col + 1] == opp){
        while (line - i >= 0 && col + i < SIZE){
            if (board_global[line - i][col + i] == NONE) break;
            if (board_global[line - i][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // 右下がり
    if (line - 1 >= 0 && col - 1 >= 0 && board_global[line - 1][col - 1] == opp){
        while (line - i >= 0 && col - i >= 0){
            if (board_global[line - i][col - i] == NONE) break;
            if (board_global[line - i][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && col + 1 < SIZE && board_global[line + 1][col + 1] == opp){
        while (line + i < SIZE && col + i < SIZE){
            if (board_global[line + i][col + i] == NONE) break;
            if (board_global[line + i][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // printf("その位置には置けません\n");
    return false;
}

void print(void){
    int num[SIZE] = {1, 2, 3, 4, 5, 6, 7, 8};
    int i, j;

    printf("\n  ");
    for (i = 0; i < SIZE; i++) printf(" %d ", num[i]);

    printf("\n -------------------------\n");
    for (i = 0; i < SIZE; i++){
        printf("%d", num[i]);
        for (j = 0; j < SIZE; j++){
            if (board_global[i][j] == NONE) printf("|  ");
            else if (board_global[i][j] == WHITE) printf("|●");
            else if (board_global[i][j] == BLACK) printf("|○");
        }
        printf("|\n -------------------------\n");
    }
}

void put(int line, int col, Board_state * now){
    int i, j;
    int my, opp;
    int pos1, pos2;

    if (turn){
        my = BLACK, opp = WHITE;
        now -> board[line][col] = BLACK;
    }else{
        my = WHITE, opp = BLACK;
        now -> board[line][col] = WHITE;
    }

    // 縦
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line - 1 >= 0 && now -> board[line - 1][col] == opp){
        while (line - i >= 0){
            if (now -> board[line - i][col] == NONE) break;
            if (now -> board[line - i][col] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && now -> board[line + 1][col] == opp){
        while (line + i < SIZE){
            if (now -> board[line + i][col] == NONE) break;
            if (now -> board[line + i][col] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            now -> board[line - i][col] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            now -> board[line + i][col] = my;
        }
    }

    // 横
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (col - 1 >= 0 && now -> board[line][col - 1] == opp){
        while (col - i >= 0){
            if (now -> board[line][col - i] == NONE) break;
            if (now -> board[line][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (col + 1 < SIZE && now -> board[line][col + 1] == opp){
        while (col + i < SIZE){
            if (now -> board[line][col + i] == NONE) break;
            if (now -> board[line][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            now -> board[line][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            now -> board[line][col + i] = my;
        }
    }

    // 右上がり
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line + 1 < SIZE && col - 1 >= 0 && now -> board[line + 1][col - 1] == opp){
        while (line + i < SIZE && col - i >= 0){
            if (now -> board[line + i][col - i] == NONE) break;
            if (now -> board[line + i][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line - 1 >= 0 && col + 1 < SIZE && now -> board[line - 1][col + 1] == opp){
        while (line - i >= 0 && col + i < SIZE){
            if (now -> board[line - i][col + i] == NONE) break;
            if (now -> board[line - i][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            now -> board[line + i][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            now -> board[line - i][col + i] = my;
        }
    }

    // 右下がり
    pos1 = -1;
    pos2 = -1;
    i = 2;
    if (line - 1 >= 0 && col - 1 >= 0 && now -> board[line - 1][col - 1] == opp){
        while (line - i >= 0 && col - i >= 0){
            if (now -> board[line - i][col - i] == NONE) break;
            if (now -> board[line - i][col - i] == my){
                pos1 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && col + 1 < SIZE && now -> board[line + 1][col + 1] == opp){
        while (line + i < SIZE && col + i < SIZE){
            if (now -> board[line + i][col + i] == NONE) break;
            if (now -> board[line + i][col + i] == my){
                pos2 = i;
                break;
            }
            i++;
        }
        i = 2;
    }
    if (pos1 != -1){
        for (i = 1; i < pos1; i++){
            now -> board[line - i][col - i] = my;
        }
    }
    if (pos2 != -1){
        for (i = 1; i < pos2; i++){
            now -> board[line + i][col + i] = my;
        }
    }
}

bool check(int line, int col, Board_state * now){
    // 置ける　 true
    // 置けない false
    if (line >= SIZE || col >= SIZE){
        // printf("その位置には置けません\n");
        return false;
    }
    if (now -> board[line][col] != NONE){
        // printf("その位置には置けません\n");
        return false;
    }

    int my, opp;
    int i = 2;
    if (turn) my = BLACK, opp = WHITE;
    else my = WHITE, opp = BLACK;

    // 縦
    if (line - 1 >= 0 && now -> board[line - 1][col] == opp){
        while (line - i >= 0){
            if (now -> board[line - i][col] == NONE) break;
            if (now -> board[line - i][col] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && now -> board[line + 1][col] == opp){
        while (line + i < SIZE){
            if (now -> board[line + i][col] == NONE) break;
            if (now -> board[line + i][col] == my) return true;
            i++;
        }
        i = 2;
    }

    // 横
    if (col - 1 >= 0 && now -> board[line][col - 1] == opp){
        while (col - i >= 0){
            if (now -> board[line][col - i] == NONE) break;
            if (now -> board[line][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (col + 1 < SIZE && now -> board[line][col + 1] == opp){
        while (col + i < SIZE){
            if (now -> board[line][col + i] == NONE) break;
            if (now -> board[line][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // 右上がり
    if (line + 1 < SIZE && col - 1 >= 0 && now -> board[line + 1][col - 1] == opp){
        while (line + i < SIZE && col - i >= 0){
            if (now -> board[line + i][col - i] == NONE) break;
            if (now -> board[line + i][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line - 1 >= 0 && col + 1 < SIZE && now -> board[line - 1][col + 1] == opp){
        while (line - i >= 0 && col + i < SIZE){
            if (now -> board[line - i][col + i] == NONE) break;
            if (now -> board[line - i][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // 右下がり
    if (line - 1 >= 0 && col - 1 >= 0 && now -> board[line - 1][col - 1] == opp){
        while (line - i >= 0 && col - i >= 0){
            if (now -> board[line - i][col - i] == NONE) break;
            if (now -> board[line - i][col - i] == my) return true;
            i++;
        }
        i = 2;
    }
    if (line + 1 < SIZE && col + 1 < SIZE && now -> board[line + 1][col + 1] == opp){
        while (line + i < SIZE && col + i < SIZE){
            if (now -> board[line + i][col + i] == NONE) break;
            if (now -> board[line + i][col + i] == my) return true;
            i++;
        }
        i = 2;
    }

    // printf("その位置には置けません\n");
    return false;
}