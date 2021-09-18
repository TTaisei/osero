// #define
final int NONE = 0;
final int BLACK = 1;
final int WHITE = 2;
final int SIZE = 8;
final int COLT = 100;
final int GSIZE = COLT * (SIZE + 1);

int PLAYER = BLACK;
//int PLAYER = WHITE;

int[][] board = new int[SIZE][SIZE];
boolean turn = true;
boolean player;

void settings() {
  size(GSIZE, GSIZE);
  if (PLAYER == BLACK) player = true;
  else player = false;
}

void setup() {
  background(0);
  colorMode(RGB, 256);
  
  strokeWeight(2);
  fill(0, 150, 0);
  
  for (int i = 0; i < SIZE; i++){
    for (int j = 0; j < SIZE; j++){
      board[i][j] = NONE;
      rect(COLT / 2 + i * COLT, COLT / 2 + j * COLT, COLT, COLT);
    }
  }
  board[SIZE / 2 - 1][SIZE / 2 - 1] = WHITE;
  board[SIZE / 2][SIZE / 2] = WHITE;
  board[SIZE / 2 - 1][SIZE / 2] = BLACK;
  board[SIZE / 2][SIZE / 2 - 1] = BLACK;
  
  fill(255);
  textSize(30);
  textAlign(CENTER);
  turn_print("");
  
  printb();
  
  if (!player) {
    put_random();
    printb();
    turn = !turn;
    turn_print("");
  }
}

void draw(){
  if (turn != player){
    put_random();
    printb();
    if (check_all(player)){
      turn = !turn;
      turn_print("");
    }else{
      turn_print("not place. once ");
    }
  }
  if (!(check_all(player) || check_all(!player))) count();
}

void mousePressed(){
  int line = (mouseY - COLT / 2) / COLT;
  int col = (mouseX - COLT / 2) / COLT;
  if (check(line, col, player)){
    put(line, col);
    printb();
    if (check_all(!player)){
      turn = !turn;
      turn_print("");
    }else{
      turn_print("not place. once ");
    }
  }
}

void put_random(){
  int line = int(random(SIZE));
  int col = int(random(SIZE));
  
  while (!check(line, col, !player)){
    line = int(random(SIZE));
    col = int(random(SIZE));
  }
  
  put(line, col);
}

void count(){
  int my = 0, your = 0;
  
  for (int i = 0; i < SIZE; i++){
    for (int j = 0; j < SIZE; j++){
      if (board[i][j] == PLAYER) your++;
      else if (board[i][j] == 3 - PLAYER) my++;
    }
  }
  
  fill(0);
  rect(0, 0, GSIZE, COLT / 2);
  fill(255);
  text("your: " + str(your) + ", my: " + str(my), GSIZE / 2 - COLT / 2, COLT / 3);
  if (your > my) text("you won!", GSIZE / 2 - COLT / 2, GSIZE - COLT / 6);
  else if (your < my) text("I won!", GSIZE / 2 - COLT / 2, GSIZE - COLT / 6);
  else text("draw", GSIZE / 2 - COLT / 2, GSIZE - COLT / 6);
}

void turn_print(String string){
  fill(0);
  rect(0, 0, GSIZE, COLT / 2);
  fill(255);
  if (turn == player) text(string + "your turn", GSIZE / 2 - COLT / 2, COLT / 3);
  else text(string + "my turn", GSIZE / 2 - COLT / 2, COLT / 3);
}

boolean check_all(boolean put_turn) {
  for (int i = 0; i < SIZE; i++)
    for (int j = 0; j < SIZE; j++)
      if (check(i, j, put_turn)) return true;
  
  return false;
}

void put(int line, int col){
  int my, opp;
  int x_for, y_for;
  if (turn){
    my = BLACK; opp = WHITE;
  }else{
    my = WHITE; opp = BLACK;
  }
  
  board[line][col] = my;
  
  for (int x = -1; x <= 1; x++){
    for (int y = -1; y <= 1; y++){
      if (x != 0 || y != 0){
        x_for = x; y_for = y;
        while (line + x_for + x < SIZE && line + x_for + x >= 0
        && col + y_for + y < SIZE && col + y_for + y >= 0
        && board[line + x_for][col + y_for] == opp){
          if (board[line + x_for + x][col + y_for + y] == my)
            for (int i = 1; i <= abs(x_for) || i <= abs(y_for); i++)
              board[line + i * x][col + i * y] = my;
          x_for += x; y_for += y;
        }
      }
    }
  }
}

boolean check(int line, int col, boolean put_turn){
  if (line > SIZE || col > SIZE) return false;
  else if(line < 0 || col < 0) return false;
  else if(board[line][col] != NONE) return false;  

  int my, opp;
  int x_for, y_for;
  if (put_turn){
    my = BLACK; opp = WHITE;
  }else{
    my = WHITE; opp = BLACK;
  }
  for (int x = -1; x <= 1; x++){
    for (int y = -1; y <= 1; y++){
      if (x != 0 || y != 0){
        x_for = x; y_for = y;
        while (line + x_for + x < SIZE && line + x_for + x >= 0
        && col + y_for + y < SIZE && col + y_for + y >= 0
        && board[line + x_for][col + y_for] == opp){
          if (board[line + x_for + x][col + y_for + y] == my)
            return true;
          x_for += x; y_for += y;
        }
      }
    }
  }
  
  return false;
}

void printb() {
  int i, j;
  
  noStroke();
  
  for (i = 0; i < SIZE; i++) {
    for (j = 0; j < SIZE; j++) {
      if (board[i][j] == BLACK) {
        fill(0, 0, 0);
        ellipse((j + 1) * COLT, (i + 1) * COLT, COLT * 0.8, COLT * 0.8);
      }else if (board[i][j] == WHITE){
        fill(255, 255, 255);
        ellipse((j + 1) * COLT, (i + 1) * COLT, COLT * 0.8, COLT * 0.8);
      }
    }
  }
}
