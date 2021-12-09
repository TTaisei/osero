from random import randint, seed
from copy import deepcopy

class osero:
    PLAY_WAY = {\
        "human": 0,
        "random": 1,
        "nhand": 2,
        "nhand_custom": 3,
        "nleast": 4,
        "nmost": 5
    }
    
    def __init__(self, black_method, white_method,\
                 seed_num=0, read_goal=[1, 1], eva=[0, 0]):
        self.think = [\
            self.human,
            self.random,
            self.nhand,
            self.nhand_custom,
            self.nleast,
            self.nmost
        ]
        if (black_method == osero.PLAY_WAY["nhand_custom"]\
            or white_method == osero.PLAY_WAY["nhand_custom"])\
            and eva is None:
            raise ValueError("designate eva")
        self.black_method = black_method
        self.white_method = white_method
        self.read_goal = read_goal
        self.eva = eva
        if black_method == osero.PLAY_WAY["nhand"]:
            self.eva[1] = [1] * 64
        if white_method == osero.PLAY_WAY["nhand"]:
            self.eva[0] = [1] * 64
#         seed(seed_num)
        self.setup()
    
    def setup(self) -> None:
        self.turn = True
        self.bw = {"b_u": 0x10000000,
                   "b_d": 0x8,
                   "w_u": 0x8000000,
                   "w_d": 0x10}
    
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
            else:
                print("not place. once ", end="")
            self.turn = not self.turn
            old_can = can
            can = self.check_all()
            
        self.count_last()
        
    def count_last(self) -> None:
        black = self.popcount(self.bw["b_u"])\
                + self.popcount(self.bw["b_d"])
        white = self.popcount(self.bw["w_u"])\
                + self.popcount(self.bw["w_d"])
        
        print("black: %d\nwhite: %d" % (black, white))
        if black > white:
            print("black win!")
        elif black < white:
            print("white win!")
        else:
            print("draw!")
    
    def count(self, now: dict, turn: bool) -> float:
        if turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
            
        is_d = False
        place = 1
        score = 0
        for i in range(32):
            if now[my[is_d]] & place:
                score += self.eva[turn][i]
            elif now[opp[is_d]] & place:
                score -= self.eva[turn][i]
            place = place << 1
        
        is_d = True
        place = 1
        for i in range(32, 64):
            if now[my[is_d]] & place:
                score += self.eva[turn][i]
            elif now[opp[is_d]] & place:
                score -= self.eva[turn][i]
            place = place << 1
        
        return score
    
    def nhand(self) -> None:
        self.nhand_custom()
    
    def nhand_custom(self) -> None:
        max_score = -100.0
        line_ans, col_ans = [-1], [-1]
        place_num = 0
        is_d = False
        
        for i in range(8):
            for j in range(8):
                if self.check(i, j, self.bw, self.turn):
                    board_leaf = deepcopy(self.bw)
                    self.put(i, j, board_leaf, self.turn)
                    score = self.board_add(\
                        board_leaf,
                        not self.turn,
                        1
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

        self.put(line_ans[0], col_ans[0], self.bw, self.turn)
        
    def board_add(self, now: dict, turn: bool, num: int) -> float:
        if num == self.read_goal[self.turn]:
            return self.count(now, self.turn)
        
        score = 0
        place_num = 0
        for i in range(8):
            for j in range(8):
                if self.check(i, j, now, turn):
                    place_num += 1
                    board_leaf = deepcopy(now)
                    self.put(i, j, board_leaf, turn)
                    score += self.board_add(\
                        board_leaf,
                        not turn,
                        num + 1
                    )
        
        if place_num:
            return score / place_num
        else:
            return self.count(now, self.turn)
    
    def nleast(self) -> None:
        min_place_num = 100
        line_ans, col_ans = [-1], [-1]
        place_num = 0
        
        for i in range(8):
            for j in range(8):
                if self.check(i, j, self.bw, self.turn):
                    board_leaf = deepcopy(self.bw)
                    self.put(i, j, board_leaf, self.turn)
                    place = self.check_place(\
                        board_leaf,
                        not self.turn,
                        not self.turn,
                        1
                    )
                    if place < min_place_num:
                        min_place_num = place
                        place_num = 0
                        line_ans = [i]
                        col_ans = [j]
                    elif place == min_place_num:
                        line_ans.append(i)
                        col_ans.append(j)
                        place_num += 1
        
        if place_num:
            place_num = randint(0, place_num)
            line_ans[0] = line_ans[place_num]
            col_ans[0] = col_ans[place_num]
        
        self.put(line_ans[0], col_ans[0], self.bw, self.turn)
    
    def nmost(self) -> None:
        max_place_num = -1
        line_ans, col_ans = [-1], [-1]
        place = 0
        
        for i in range(8):
            for j in range(8):
                if self.check(i, j, self.bw, self.turn):
                    board_leaf = deepcopy(self.bw)
                    self.put(i, j, board_leaf, self.turn)
                    place = self.check_place(\
                        board_leaf,
                        not self.turn,
                        self.turn,
                        1
                    )
                    if place > max_place_num:
                        max_place_num = place
                        place_num = 0
                        line_ans = [i]
                        col_ans = [j]
                    elif place == max_place_num:
                        place_num += 1
                        line_ans.append(i)
                        col_ans.append(j)
        
        if place_num:
            place_num = randint(0, place_num)
            line_ans[0] = line_ans[place_num]
            col_ans[0] = col_ans[place_num]
        self.put(line_ans[0], col_ans[0], self.bw, self.turn)
    
    def check_place(self, now: dict, turn: bool,\
                    tar_turn: bool, num: int) -> float:
        place_num = 0
        
        if turn == tar_turn:
            if num == self.read_goal[self.turn]:
                for i in range(8):
                    for j in range(8):
                        if self.check(i, j, now, turn):
                            place_num += 1
                return place_num
            else:
                place_sum = 0
                for i in range(8):
                    for j in range(8):
                        if self.check(i, j, now, turn):
                            board_leaf = deepcopy(now)
                            self.put(i, j, board_leaf, turn)
                            place_sum += self.check_place(\
                                board_leaf,
                                not turn,
                                tar_turn,
                                num + 1
                            )
                            place_num += 1
                if place_num:
                    return place_sum / place_num
                else:
                    return 0
        else:
            place_sum = 0
            for i in range(8):
                for j in range(8):
                    if self.check(i, j, now, turn):
                        board_leaf = deepcopy(now)
                        self.put(i, j, board_leaf, turn)
                        place_sum = self.check_place(\
                            board_leaf,
                            not turn,
                            tar_turn,
                            num
                        )
            if place_sum:
                return place_sum
            else:
                return self.check_place(\
                    deepcopy(now),
                    not turn,
                    tar_turn,
                    num
                )
    
    def random(self) -> None:
        while True:
            line = randint(0, 8)
            col = randint(0, 8)
            if self.check(line, col, self.bw, self.turn):
                break
        self.put(line, col, self.bw, self.turn)
    
    def human(self) -> None:
        while True:
            line = input("line:\t")
            col = input("col:\t")
            try:
                line = int(line)
                col = int(col)
            except:
                line, col = -1, -1
            if self.check(line - 1, col - 1, self.bw, self.turn):
                break
            print("can't put that place.once input.")
        self.put(line - 1, col - 1, self.bw, self.turn)
    
    def put(self, line: int, col: int, now: dict, turn: bool) -> None:
        if turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
            
        if line < 4:
            is_d = False
            now[my[is_d]] += 1 << (line << 3) + col
        else:
            is_d = True
            now[my[is_d]] += 1 << ((line - 4) << 3) + col
        
        for x in range(-1, 2):
            for y in range(-1, 2):
                if x == 0 and y == 0:
                    continue
                inver = [0, 0]
                line_x = line + x
                col_y = col + y
                if line_x < 0 or line_x >= 8\
                   or col_y < 0 or col_y>= 8:
                    continue
                if line_x < 4:
                    is_d = False
                else:
                    line_x = line_x - 4
                    is_d = True
                place = 1 << (line_x << 3) + col_y
                while now[opp[is_d]] & place\
                      and 0 <= y + col_y and y + col_y < 8:
                    inver[is_d] = inver[is_d] | place
                    if line_x + x >= 4 and not is_d:
                        line_x = 0
                        is_d = True
                    elif line_x + x < 0 and is_d:
                        line_x = 3
                        is_d = False
                    elif line_x + x > 4 or line_x + x < 0:
                        break
                    else:
                        line_x += x
                    col_y += y
                    place = 1 << (line_x << 3) + col_y
                if now[my[is_d]] & place:
                    now[my[is_d]] += inver[is_d]
                    now[my[not is_d]] += inver[not is_d]
                    now[opp[is_d]] -= inver[is_d]
                    now[opp[not is_d]] -= inver[not is_d]

    def check(self, line: int, col: int, now: dict, turn: bool) -> bool:
        if line >= 8 or line < 0\
           or col >= 8 or col < 0:
            return False
        
        place = (line << 3) + col
        if line < 4:
            if now["b_u"] & 1 << place\
               or now["w_u"] & 1 << place:
                return False
        else:
            if self.bw["b_d"] & 1 << (place - 32)\
               or now["w_d"] & 1 << (place - 32):
                return False
            
        if turn:
            my = ["b_u", "b_d"]
            opp = ["w_u", "w_d"]
        else:
            my = ["w_u", "w_d"]
            opp = ["b_u", "b_d"]
        
        for x in range(-1, 2):
            for y in range(-1, 2):
                if x == 0 and y == 0:
                    continue
                line_x = line + x
                col_y = col + y
                if line_x < 0 or col_y < 0\
                   or line_x >= 8 or col_y >= 8:
                    continue
                if line_x < 4:
                    is_d = False
                else:
                    line_x = line_x - 4
                    is_d = True
                while now[opp[is_d]] & 1 << (line_x << 3) + col_y\
                      and 0 <= y + col_y and y + col_y < 8:
                    if line_x + x >= 4 and not is_d:
                        line_x = 0
                        is_d = True
                    elif line_x + x < 0 and is_d:
                        line_x = 3
                        is_d = False
                    elif line_x + x >= 4 or line_x + x < 0:
                        break
                    else:
                        line_x += x
                    col_y += y
                    if now[my[is_d]] & 1 << ((line_x << 3) + col_y):
                        return True
                
        return False

    def check_all(self) -> bool:
        for i in range(8):
            for j in range(8):
                if self.check(i, j, self.bw, self.turn):
                    return True
        
        return False
    
    def popcount(self, now: int) -> int:
        now = now - ((now >> 1) & 0x55555555)
        now = (now & 0x33333333) + ((now >> 2) & 0x33333333)
        now = (now + (now >> 4)) & 0x0f0f0f0f
        now = now + (now >> 8)
        now = now + (now >> 16)
        
        return now & 0x7f
    
    def printb(self) -> None:
        print(end="  ")
        for i in range(8):
            print(" %d " % (i + 1), end="")
        print("\n -------------------------")
        
        num = 1
        for i in range(32):
            if i % 8 == 0:
                print("%d" % ((i >> 3) + 1), end="")
            if self.bw["b_u"] & num:
                print("|〇", end="")
            elif self.bw["w_u"] & num:
                print("|●", end="")
            else:
                print("|  ", end="")
            if i % 8 == 7:
                print("|\n -------------------------")
            num = num << 1
        
        num = 1
        for i in range(32):
            if i % 8 == 0:
                print("%d" % ((i >> 3) + 5), end="")
            if self.bw["b_d"] & num:
                print("|〇", end="")
            elif self.bw["w_d"] & num:
                print("|●", end="")
            else:
                print("|  ", end="")
            if i % 8 == 7:
                print("|\n -------------------------")
            num = num << 1

if __name__ == "__main__":
    eva = [[1] * 64 for i in range(2)]

    o = osero(\
        osero.PLAY_WAY["nmost"],
        osero.PLAY_WAY["nmost"],
        read_goal=[2, 2],
    #     eva=eva
    )
    o.play()