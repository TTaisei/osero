from copy import deepcopy
from random import randint

from pandas import DataFrame

from BitBoard import osero
from model_run import model_run

BLACK = 0
WHITE = 1

turn_vari = [i for i in range(1, 61)]
alpha_arr = [i for i in range(3, 13)]

class osero_AI(osero, model_run):
    def __init__(self, folda: str, player=BLACK, read_goal=[1, 1], eva=[0, 0],\
                 player_method=osero.PLAY_WAY["human"]):
        osero.PLAY_WAY["osero_AI"] = len(osero.PLAY_WAY)
        if player == BLACK:
            black_method = player_method
            white_method = osero.PLAY_WAY["osero_AI"]
        else:
            black_method = osero.PLAY_WAY["osero_AI"]
            white_method = player_method
        super().__init__(\
            black_method,
            white_method,
            read_goal=read_goal,
            eva=eva
        )
        self.think.append(self.osero_AI)
        self.model_setup(folda, turn_vari, alpha_arr)
    
    def play(self) -> None:
        can, old_can = True, True
        
        self.printb()
        self.turn_num = 0
        
        can = self.check_all()
        while can or old_can:
            if can:
                self.turn_num += 1
                if self.turn:
                    print("black turn")
                    self.think[self.black_method]()
                else:
                    print("white turn")
                    self.think[self.white_method]()
                self.printb()
            self.turn = not self.turn
            old_can = can
            can = self.check_all()
            
        self.count_last()
    
    def osero_AI(self) -> None:
        search_place = self.search_place()
        if search_place == -1:
            self.random()
            return
        
        max_score = -100
        line_ans, col_ans = [-1], [-1]
        place_num = 0
        
        for i in range(8):
            for j in range(8):
                if not self.check(i, j, self.bw, self.turn):
                    continue
                board_leaf = deepcopy(self.bw)
                self.put(i, j, board_leaf, self.turn)
                score =self.AI_think(\
                    board_leaf,
                    not self.turn,
                    self.turn_num + 1,
                    search_place
                )
                if score > max_score:
                    max_score = score
                    place_num = 0
                    line_ans = [i]
                    col_ans = [j]
                elif score == max_score:
                    place_num += 1
                    line_ans.append(i)
                    col_ans.append(j)
        
        if place_num:
            place = randint(0, place_num)
            line_ans[0] = line_ans[place]
            col_ans[0] = col_ans[place]
        
#         print("line:\t%d\ncol:\t%d" % (line_ans[0]+1, col_ans[0]+1))
        self.put(line_ans[0], col_ans[0], self.bw, self.turn)
    
    def AI_think(self, now_board: dict, turn: bool,\
                 turn_num: int, read_goal: int) -> float:
        if turn_num == read_goal:
            return self.predict_mean(now_board, read_goal)
        
        score = 0
        place_num = 0
        for i in range(8):
            for j in range(8):
                if not self.check(i, j, now_board, turn):
                    continue
                place_num += 1
                board_leaf = deepcopy(now_board)
                self.put(i, j, board_leaf, turn)
                score += self.AI_think(\
                    board_leaf,
                    not turn,
                    turn_num + 1,
                    read_goal
                )
        
        if place_num:
            return score / place_num
        else:
            return self.predict_mean(now_board, read_goal)
    
    def predict_mean(self, now_board: dict, read_turn: int) -> float:
        score = 0
        x = self.dict_to_DataFrame(now_board)

        for alpha in alpha_arr:
            score += self.predict(\
                read_turn,
                alpha,
                x
            )
        
        return score / len(alpha_arr)
    
    def dict_to_DataFrame(self, now_board: dict) -> DataFrame:
        data = {}
        if self.turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
        for i in range(64):
            data["my%d" % i] = [int((now_board[my[i >= 32]] & (1 << i)) != 0)]
            data["opp%d" % i] = [int((now_board[opp[i >= 32]] & (1 << i)) != 0)]

        return DataFrame(data)
    
    def search_place(self) -> int:
        for turn_num in turn_vari:
            if self.turn_num < turn_num:
                return turn_num
        
        return -1

if __name__ == "__main__":
    a = osero_AI("model", player=BLACK, player_method=osero.PLAY_WAY["human"])
    a.play()