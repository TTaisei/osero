from BitBoard import osero

class learn(osero):
    def __init__(self, black_method, white_method, check_point,\
                 seed_num=0, read_goal=[1, 1], eva=None):
        super().__init__(black_method, white_method, seed_num, read_goal, eva)
        self.check_point = check_point
    
    def play(self) -> list:
        can, old_can = True, True
        turn_num = 0
        data = []
        
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

        last_score = [self.score] * len(data)

        return data, last_score
    
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
    
    def data_set(self, data: list, turn_num: int) -> None:
        data.append([])
        if self.turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
#         s_sum = self.popcount(self.bw[my[0]])\
#               + self.popcount(self.bw[my[1]])\
#               + self.popcount(self.bw[opp[0]])\
#               + self.popcount(self.bw[opp[1]])
#         append_num = 1 / s_sum
        append_num = 1
        for i in range(32):
            if self.bw[my[0]] & 1 << i:
                data[-1].append(append_num)
            else:
                data[-1].append(0)
            if self.bw[opp[0]] & 1 << i:
                data[-1].append(append_num)
            else:
                data[-1].append(0)
        for i in range(32):
            if self.bw[my[1]] & 1 << i:
                data[-1].append(append_num)
            else:
                data[-1].append(0)
            if self.bw[opp[1]] & 1 << i:
                data[-1].append(append_num)
            else:
                data[-1].append(0)