from copy import deepcopy
from random import randint

from chainer.serializers import load_npz
import numpy as np

import osero
import Net

class AI(osero.osero):
    def __init__(self, black_method, white_method, seed_num=0,
                 read_goal=[1, 1], eva=[0, 0], model_name="model.net"):
        super().__init__(black_method, white_method, seed_num,
                         read_goal, eva)
        self.PLAY_WAY["AI"] = len(self.PLAY_WAY)
        self.think.append(self.AI)
        self.model_name = model_name
        self.A = 1
        self.B = 1
        self.model_load()
    
    def play(self) -> int:
        can, old_can = True, True
        
        can = self.check_all()
        while can or old_can:
            if can:
                if self.turn:
                    self.think[self.black_method]()
                else:
                    self.think[self.white_method]()
            self.turn = not self.turn
            old_can = can
            can = self.check_all()
        
        return self.count_last()
    
    def count_last(self) -> int:
        black = self.popcount(self.bw["b_u"])\
                + self.popcount(self.bw["b_d"])
        white = self.popcount(self.bw["w_u"])\
                + self.popcount(self.bw["w_d"])
        
        return black - white
    
    def AI(self) -> None:
        min_total_score = -0xffff
        line_ans, col_ans = [-1], [-1]
        place_num = 0
        
        for i in range(8):
            for j in range(8):
                if not self.check(i, j, self.bw, self.turn):
                    continue
                board_leaf = deepcopy(self.bw)
                self.put(i, j, board_leaf, self.turn)
                score, place = self.cal_score_place(\
                    board_leaf,
                    not self.turn,
                    not self.turn,
                    1
                )
                total_score = self.A * score + self.B / (place + 1)
                if total_score > min_total_score:
                    min_total_score = total_score
                    place_num = 0
                    line_ans = [i]
                    col_ans = [j]
                elif total_score == min_total_score:
                    line_ans.append(i)
                    col_ans.append(j)
                    place_num += 1
        
        if place_num:
            place_num = randint(0, place_num)
            line_ans[0] = line_ans[place_num]
            col_ans[0] = col_ans[place_num]
        
        self.put(line_ans[0], col_ans[0], self.bw, self.turn)
    
    def cal_score_place(self, now: dict, turn: bool,\
                        tar_turn: bool, num: int) -> tuple:
        place_num = 0
        
        if turn == tar_turn:
            if num == self.read_goal[self.turn]:
                for i in range(8):
                    for j in range(8):
                        if self.check(i, j, now, turn):
                            place_num += 1
                score = self.predict(now)
                return score, place_num
            else:
                score_sum = 0
                place_sum = 0
                for i in range(8):
                    for j in range(8):
                        if not self.check(i, j, now, turn):
                            continue
                        board_leaf = deepcopy(now)
                        self.put(i, j, board_leaf, turn)
                        score, place = self.cal_score_place(\
                            board_leaf,
                            not turn,
                            tar_turn,
                            num + 1
                        )
                        score_sum += score
                        place_sum += place
                        place_num += 1
                if place_num:
                    return score_sum / place_num, place_sum / place_num
                else:
                    return 0, 0
        else:
            score_sum = 0
            place_sum = 0
            for i in range(8):
                for j in range(8):
                    if not self.check(i, j, now, turn):
                        continue
                    board_leaf = deepcopy(now)
                    self.put(i, j, board_leaf, turn)
                    score, place = self.cal_score_place(\
                        board_leaf,
                        not turn,
                        tar_turn,
                        num
                    )
                    score_sum += score
                    place_sum += place
                    place_num += 1
            if place_num:
                return score_sum / place_num, place_sum / place_num
            else:
                return self.cal_score_place(\
                    deepcopy(now),
                    not turn,
                    tar_turn,
                    num
                )
        
    def predict(self, now: dict) -> float:
        now = self.dict_to_ndarray(now)
        ans = self.model(now)
        return float(ans.array)

    def model_load(self) -> None:
        self.model = Net.Net()
        load_npz(self.model_name, self.model)
    
    def dict_to_ndarray(self, now: dict) -> np.array:
        data = []

        if self.turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            opp = ["b_u", "b_d"]
            my = ["w_u", "w_d"]
        
        for i in range(2):
            for j in range(32):
                if self.bw[my[i]] & 1 << j:
                    data.append(1)
                    data.append(0)
                    data.append(0)
                elif self.bw[opp[i]] & 1 << j:
                    data.append(0)
                    data.append(1)
                    data.append(0)
                else:
                    data.append(0)
                    data.append(0)
                    data.append(1)

        return np.array([data], dtype=np.float32) 


if __name__ == "__main__":
    a = AI(0, 0)