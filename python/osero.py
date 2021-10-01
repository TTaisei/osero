from time import time

#define
NONE = 0
WHITE = 1
BLACK = 2
SIZE = 8
PLAYER = BLACK
# PLAYER = WHITE
NUM = 1000

board = [[NONE for i in range(SIZE)] for i in range(SIZE)]
turn = BLACK

def printb():
    print(end="\n ")
    for i in range(SIZE):
        print(" ", i + 1, end="")
    print("\n -------------------------")
    for i in range(SIZE):
        print(end=str(i + 1))
        for j in range(SIZE):
            if board[i][j] == NONE:
                print(end="|  ");
            elif board[i][j] == WHITE:
                print(end="|●")
            elif board[i][j] == BLACK:
                print(end="|○")
        print("|\n -------------------------")

def setup():
    board[(SIZE >> 1) - 1][(SIZE >> 1) - 1] = WHITE
    board[SIZE >> 1][SIZE >> 1] = WHITE
    board[(SIZE >> 1) - 1][SIZE >> 1] = BLACK
    board[SIZE >> 1][(SIZE >> 1) - 1] = BLACK

def check_all() -> bool:
    for i in range(SIZE):
        for j in range(SIZE):
            if check(i, j):
                return True
    
    return False

def que():
    line = input("行: ")
    col = input("列: ")
    try:
        line = int(line)
        col = int(col)
    except:
        line = SIZE
        col = SIZE
    return line, col

def check(line: int, col: int) -> bool:
    if (board[line][col] != NONE):
        return False
    
    my, opp = turn, 3 - turn
    
    for x in range(-1, 2):
        for y in range(-1, 2):
            if x != 0 or y != 0:
                line_x = x
                col_y = y
                while (0 <= line + x + line_x < SIZE\
                       and 0 <= col + y + col_y < SIZE\
                       and board[line + line_x][col + col_y] == opp):
                    if board[line + x + line_x][col + y + col_y] == my:
                        return True
                    line_x += x
                    col_y += y
    
    return False

def put(line: int, col: int):
    my, opp = turn, 3 - turn
    
    board[line][col] = my
    
    for x in range(-1, 2):
        for y in range(-1, 2):
            if x != 0 or y != 0:
                num = 0
                check_bool = False
                line_x = x
                col_y = y
                while (0 <= line + x + line_x < SIZE\
                       and 0 <= col + y + col_y < SIZE\
                       and board[line + line_x][col + col_y] == opp):
                    if board[line + x + line_x][col + y + col_y] == my:
                        check_bool = True
                    line_x += x
                    col_y += y
                    num += 1
            if check_bool:
                for i in range(1, num + 1):
                    board[line + i * x][col + i * y] = my

def count():
    black, white = 0, 0
    
    for i in range(SIZE):
        for j in range(SIZE):
            if board[i][j] == BLACK:
                black += 1
            elif board[i][j] == WHITE:
                white += 1
    
    if black > white:
        print("黒の勝ちです")
    elif black > white:
        print("白の勝ちです")
    else:
        print("引き分けです")

def main():
    global turn
    start = time()
    
    can, old_can = True, True
    
    print("黒: ○")
    print("白: ●")
    
    setup()
    printb()
    
    while can or old_can:
        if turn == BLACK:
            print("黒のターンです")
        else:
            print("白のターンです")
        if can:
            line, col = que()
            while not check(line - 1, col - 1):
                print("その位置には置けません")
                line, col = que()
            put(line - 1, col - 1)
            printb()
        else:
            print("置ける場所がありません")
        old_can = can
        turn = 3 - turn
        can = check_all()
    
    print("\n試合時間: %d秒" % (time() - start))
    count()

if __name__ == "__main__":
    main()