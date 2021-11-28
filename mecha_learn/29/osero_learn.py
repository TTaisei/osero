from pandas import DataFrame
from BitBoard import osero

class learn(osero):
    def __init__(self, black_method, white_method, check_point,\
                 seed_num=0, read_goal=[1, 1], eva=None):
        super().__init__(black_method, white_method, seed_num, read_goal, eva)
        self.check_point = check_point
    
    def play(self) -> DataFrame:
        can, old_can = True, True
        turn_num = 0
        data = {}
        
        self.first_data_set(data)
        
        can = self.check_all()
        while can or old_can:
            if can:
                turn_num += 1
                if self.turn:
                    self.think[self.black_method]()
                else:
                    self.think[self.white_method]()
                if turn_num in self.check_point:
                    self.data_set(data, turn_num)
            self.turn = not self.turn
            old_can = can
            can = self.check_all()
        
        self.count_last()
        for i in range(len(data["turn_num"])):
            if data["turn"][i]:
                data["last_score"].append(self.score)
            else:
                data["last_score"].append(-self.score)
        return DataFrame(data)
    
    def first_data_set(self, data: DataFrame) -> None:
        data["turn_num"] = []
        data["turn"] = []
        data["last_score"] = []
        for i in range(64):
            data["my%d" % i] = []
            data["opp%d" % i] = []
    
    def count_last(self) -> None:
        black = self.popcount(self.bw["b_u"])\
                + self.popcount(self.bw["b_d"])
        white = self.popcount(self.bw["w_u"])\
                + self.popcount(self.bw["w_d"])
        
        self.score = black - white
    
    def search_put(self) -> int:
        num = 0
        
        for i in range(8):
            for j in range(8):
                if self.check(i, j, self.bw, not self.turn):
                    num += 1
        
        return num
    
    def data_set(self, data: DataFrame, turn_num: int) -> None:
        if self.turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
        data["turn_num"].append(turn_num)
        data["turn"].append(self.turn)
        for i in range(64):
            data["my%d" % i].append(int((self.bw[my[i >= 32]] & (1 << i)) != 0))
            data["opp%d" % i].append(int((self.bw[opp[i >= 32]] & (1 << i)) != 0))