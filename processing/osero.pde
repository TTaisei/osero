// #define
final int NONE = 0;
final int BLACK = 1;
final int WHITE = 2;
final int SIZE = 8;
final int COLT = 100;
final int GSIZE = COLT * (SIZE + 1);

int[][] board = new int[SIZE][SIZE];
boolean turn = true;

void settings() {
  size(GSIZE, GSIZE);
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
  noStroke();
  
  board[SIZE / 2 - 1][SIZE / 2 - 1] = WHITE;
  board[SIZE / 2][SIZE / 2] = WHITE;
  board[SIZE / 2 - 1][SIZE / 2] = BLACK;
  board[SIZE / 2][SIZE / 2 - 1] = BLACK;
  
  fill(255);
  textSize(30);
  textAlign(CENTER);
  text("black turn", GSIZE / 2 - COLT / 2, COLT / 3);
  
  printb();
}

void draw(){
  ;
}
 
void mousePressed(){
  int line = (mouseY - COLT / 2) / COLT;
  int col = (mouseX - COLT / 2) / COLT;
  if (check(line, col)){
    put(line, col);
    printb();
    turn = !turn;
    if (check_all()){
      turn_print("");
    }else{
      turn = !turn;
      if (check_all()) turn_print("not place. again, ");
      else count();
    }
  }
}

void count(){
  int black = 0, white = 0;
  
  for (int i = 0; i < SIZE; i++){
    for (int j = 0; j < SIZE; j++){
      if (board[i][j] == BLACK) black++;
      else if (board[i][j] == WHITE) white++;
    }
  }
  
  fill(0);
  rect(0, 0, GSIZE, COLT / 2);
  fill(255);
  text("black: " + str(black) + ", white: " + str(white), GSIZE / 2 - COLT / 2, COLT / 3);
  if (black > white) text("black won!", GSIZE / 2 - COLT / 2, GSIZE - COLT / 6);
  else if (black < white) text("white won!", GSIZE / 2 - COLT / 2, GSIZE - COLT / 6);
  else text("draw", GSIZE / 2 - COLT / 2, GSIZE - COLT / 6);
}

void turn_print(String string){
  fill(0);
  rect(0, 0, GSIZE, COLT / 2);
  fill(255);
  if (turn) text(string + "black turn", GSIZE / 2 - COLT / 2, COLT / 3);
  else text(string + "white turn", GSIZE / 2 - COLT / 2, COLT / 3);
}

boolean check_all() {
  for (int i = 0; i < SIZE; i++)
    for (int j = 0; j < SIZE; j++)
      if (check(i, j)) return true;
  
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

boolean check(int line, int col){
  if (line > SIZE || col > SIZE) return false;
  else if(line < 0 || col < 0) return false;
  else if(board[line][col] != NONE) return false;  

  int my, opp;
  int x_for, y_for;
  if (turn){
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
