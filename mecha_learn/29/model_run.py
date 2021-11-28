import pickle

class model_run:
    def __init__(self):
        self.model = {}
    
    def model_setup(self, folda: str, turn_vari: list,\
                    alpha_arr: list) -> None:
        self.model = {}
        for turn_num in turn_vari:
            self.model[str(turn_num)] = {}
            for alpha in alpha_arr:
                with open(\
                    "%s/model%2d%2d.pickle" % (folda, turn_num, alpha),
                    mode="rb"
                ) as fp:
                    self.model[str(turn_num)][str(alpha)] = pickle.load(fp)
    
    def predict(self, turn_num: int, alpha: int, now_board: dict) -> int:
        predict = self.model[str(turn_num)][str(alpha)].predict(now_board)
        return predict[0][0]