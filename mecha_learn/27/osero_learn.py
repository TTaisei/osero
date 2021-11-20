from pandas import DataFrame
from BitBoard import osero

class learn(osero):
    def __init__(self, black_method, white_method,\
                 read_goal=[1, 1], eva=None):
        super().__init__(black_method, white_method, read_goal, eva)
    
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
                if turn_num % 10 == 0:
                    self.data_set(data, turn_num)
            self.turn = not self.turn
            old_can = can
            can = self.check_all()
        
        self.count_last()
        data["last_score"] = [self.score] * (turn_num // 10)
        return DataFrame(data)
    
    def first_data_set(self, data: DataFrame) -> None:
        data["turn"] = []
        for i in range(64):
            data["b%d" % i] = []
            data["w%d" % i] = []
    
    def count_last(self) -> None:
        black = self.popcount(self.bw["b_u"])\
                + self.popcount(self.bw["b_d"])
        white = self.popcount(self.bw["w_u"])\
                + self.popcount(self.bw["w_d"])
        
        self.score = black - white
    
    def data_set(self, data: DataFrame, turn_num: int) -> None:
        data["turn"].append(turn_num)
        for i in range(32):
            data["b%d" % i].append(int((self.bw["b_u"] & (1 << i)) != 0))
            data["w%d" % i].append(int((self.bw["w_u"] & (1 << i)) != 0))
        for i in range(32):
            data["b%d" % (i + 32)].append(int((self.bw["b_d"] & (1 << i)) != 0))
            data["w%d" % (i + 32)].append(int((self.bw["w_d"] & (1 << i)) != 0))