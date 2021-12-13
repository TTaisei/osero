from BitBoard import osero
from osero_AI import osero_AI as Ridge
from osero_deep_AI import osero_deep_AI as NN

dir_name = "model"
turn_vari = [i for i in range(1, 61)]
alpha_arr = [i for i in range(3, 13)]

eva = [1 for i in range(64)]
eva_custom = [
     1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0,
    -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
     0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
     0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
     0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
     0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
    -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
     1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0
]

class battle(Ridge, NN):
    def __init__(self):
        osero.__init__(self, 0, 0)
        osero.PLAY_WAY["Ridge"] = len(osero.PLAY_WAY)
        self.think.append(self.osero_AI)
        osero.PLAY_WAY["NN"] = len(osero.PLAY_WAY)
        self.think.append(self.osero_deep_AI)
        Ridge.model_setup(self, dir_name, turn_vari, alpha_arr)
        NN.model_setup(self)
    
    def play(self) -> list:
        can, old_can = True, True
        
        can = self.check_all()
        self.turn_num = 0
        while can or old_can:
            if can:
                self.turn_num += 1
                if self.turn:
                    self.think[self.black_method]()
                else:
                    self.think[self.white_method]()
            self.turn = not self.turn
            old_can = can
            can = self.check_all()
        
        return self.count_last()

    def count_last(self) -> list:
        black = self.popcount(self.bw["b_u"])\
                + self.popcount(self.bw["b_d"])
        white = self.popcount(self.bw["w_u"])\
                + self.popcount(self.bw["w_d"])
        
        return [black, white, int(black > white), int(white > black)]