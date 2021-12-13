from copy import deepcopy
from random import randint

import chainer
import numpy as np

from BitBoard import osero
import net

BLACK = 0
WHITE = 1

class osero_deep_AI(osero):
    def __init__(self, player=BLACK, read_goal=[1, 1], eva=[0, 0],\
                 player_method=osero.PLAY_WAY["human"]):
        osero.PLAY_WAY["osero_deep_AI"] = len(osero.PLAY_WAY)
        if player == BLACK:
            black_method = player_method
            white_method = osero.PLAY_WAY["osero_deep_AI"]
        else:
            black_method = osero.PLAY_WAY["osero_deep_AI"]
            white_method = player_method
        super().__init__(\
            black_method,
            white_method,
            read_goal=read_goal,
            eva=eva
        )
        self.think.append(self.osero_deep_AI)
        self.model_setup()
    
    def play(self) -> None:
        can, old_can = True, True
        
        self.printb()
        
        can = self.check_all()
        while can or old_can:
            if can:
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
    
    def model_setup(self) -> None:
        self.net = net.Net()
        chainer.serializers.load_npz("deep_AI.net", self.net)
    
    def osero_deep_AI(self) -> None:
        max_score = -100.
        line_ans, col_ans = [-1], [-1]
        place_num = 0
        
        for i in range(8):
            for j in range(8):
                if not self.check(i, j, self.bw, self.turn):
                    continue
                board_leaf = deepcopy(self.bw)
                self.put(i, j, board_leaf, self.turn)
                score = self.deep_AI_think(\
                    board_leaf,
                    not self.turn,
                    1,
                    self.read_goal[self.turn]
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
    
    def deep_AI_think(self, now_board: dict, turn: bool,\
                      num: int, read_goal: int) -> float:
        if num == read_goal:
            return self.forward(now_board)
        
        score = 0
        place_num = 0
        for i in range(8):
            for j in range(8):
                if not self.check(i, j, now_board, turn):
                    continue
                place_num += 1
                board_leaf = deepcopy(now_board)
                self.put(i, j, board_leaf, turn)
                score += self.deep_AI_think(\
                    board_leaf,
                    not turn,
                    num + 1,
                    read_goal
                )
        
        if place_num:
            return score / place_num
        else:
            return self.forward(now_board)
    
    def dict_to_ndarray(self, now_board: dict) -> np.ndarray:
        data = []
        if self.turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
        for i in range(32):
            data.append(int(now_board[my[0]] & 1 << i))
            data.append(int(now_board[opp[0]] & 1 << i))
        for i in range(32):
            data.append(int(now_board[my[1]] & 1 << i))
            data.append(int(now_board[opp[1]] & 1 << i))
        
        return np.array([data], dtype=np.float32)
    
    def forward(self, now_board: dict) -> float:
        x = self.dict_to_ndarray(now_board)
        return self.net(x).array[0][0]

if __name__ == "__main__":
    a = osero_deep_AI(player=BLACK, player_method=osero.PLAY_WAY["human"])
    a.play()