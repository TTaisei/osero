#include <stdio.h>
#include <time.h>
#include <stdbool.h>

#define NONE 0
#define WHITE 1
#define BLACK 2
#define SIZE 8

int board[SIZE][SIZE] = {NONE};
bool turn = true;

void setup(void);
void print(void);
void que(int * line, int * col);
void put(int line, int col);
void count(void);
bool check(int line, int col);
bool check_all(void);
bool all_none(void);

////////////////  main  ////////////////
int main(){
    int line, col;
    bool my_check = true, old_check = true;

    // check_allで反転させられるので
    turn = false;
    setup();
    print();

    long now = time(NULL);
    while ((my_check = check_all()) || all_none()){
        if (turn) printf("\n黒のターンです\n");
        else printf("\n白のターンです\n");
        if (my_check){
            que(&line, &col);
            while (!check(line, col)){
                printf("その位置には置けません\n");
                que(&line, &col);
            }
            put(line, col);
            print();
        }else{
            printf("置ける場所がありません\n");
        }
        if (!my_check && !old_check) break;
        old_check = my_check;
    }

    printf("\n試合時間: %ld秒\n", time(NULL) - now);
    count();

    return 0;
}
////////////////////////////////////////

bool all_none(void){
    int i, j;
    bool check = false;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++)
            if (board[i][j] == NONE) check = true;
        if (check) break;
    }
    
    if (i == SIZE && j == SIZE) return false;
    else return true;
}

void count(void){
    int i, j;
    int black = 0, white = 0;

    for (i = 0; i < SIZE; i++){
        for (j = 0; j < SIZE; j++){
            if (board[i][j] == BLACK) black++;
            else if (board[i][j] == WHITE) white++;
        }
    }

    printf("黒の数: %2d\n白の数: %2d\n", black, white);

    if (black > white) printf("黒の勝ちです\n");
    else if (black < white) printf("白の勝ちです\n");
    else printf("引き分けです\n");
}

bool check_all(void){
    // 置ける　 true
    // 置けない false
    int i = 0, j = 0;

    turn = !turn;

    while (!check(i + 1, j + 1) && j != SIZE){
        i++;
        if (i == SIZE){
            i = 0;
            j++;
        }
    }

    if (j == SIZE) return false;
    else return true;
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
            if (board[i][j] == NONE) printf("|  ");
            else if (board[i][j] == WHITE) printf("|●");
            else if (board[i][j] == BLACK) printf("|○");
        }
        printf("|\n -------------------------\n");
    }
}

void que(int * line, int * col){
    printf("行: ");
    scanf("%d", line);
    printf("列: ");
    scanf("%d", col);
}

void setup(void){
    printf("黒: ○\n白: ●\n\n");
    board[SIZE / 2 - 1][SIZE / 2 - 1] = WHITE;
    board[SIZE / 2][SIZE / 2] = WHITE;
    board[SIZE / 2 - 1][SIZE / 2] = BLACK;
    board[SIZE / 2 ][SIZE / 2 - 1] = BLACK;
    // print();
}

void put(int line, int col){
    int i, j;
    int my, opp;
    int pos1, pos2;

    line--;
    col--;

    if (turn == true){
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

    // turn = !turn;
    // print();
}

bool check(int line, int col){
    // 置ける　 true
    // 置けない false
    line--;
    col--;
    if (line >= SIZE || col >= SIZE){
        // printf("その位置には置けません\n");
        return false;
    }
    if (board[line][col] != NONE){
        // printf("その位置には置けません\n");
        return false;
    }

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

    // printf("その位置には置けません\n");
    return false;
}