from copy import deepcopy

import numpy as np
from chainer.datasets import TupleDataset, split_dataset_random
from chainer.iterators import SerialIterator
import chainer
import chainer.functions as F
from chainer.optimizers import AdaBound
from chainer.optimizer_hooks import GradientHardClipping as GHC
from chainer.dataset import concat_examples

from BitBoard import osero
from osero_learn import learn
from Net import Net

PLAY_WAY = deepcopy(osero.PLAY_WAY)
del PLAY_WAY["human"]
PLAY_WAY = PLAY_WAY.values()

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

check_point = [i for i in range(1, 61)]

class deep_learn:
    def __init__(self):
        self.PLAY_WAY = PLAY_WAY
        self.eva = eva
        self.data = []
        self.result = []
        self.osero = learn(0, 0, check_point=check_point, eva=[0, 0])
        self.num = 1
        self.n_epoch = 40
        self.batch_size = 200
        self.Net = Net
        self.bound = 2
        self.optimizer = AdaBound()
        self.hook_f = GHC(-self.bound, self.bound)
        self.xlabel1, self.xlabel2 = "epoch", "epoch"
        self.ylabel1, self.ylabel2 = "mean squared error", "mean abolute error"
        self.fig_name1, self.fig_name2 = "MSE of each epoch", "MAE of each epoch"
        self.fig = None
        self.ax = None
    
    def set_data(self):
        self.data = []
        self.result = []

        for i in range(self.num):
            for black in self.PLAY_WAY:
                for white in self.PLAY_WAY:
                    self.osero.setup()
                    self.osero.black_method = black
                    self.osero.white_method = white
                    if black == osero.PLAY_WAY["nhand"]:
                        self.osero.eva[1] = eva
                    elif black == osero.PLAY_WAY["nhand_custom"]:
                        self.osero.eva[1] = eva_custom
                    if white == osero.PLAY_WAY["nhand"]:
                        self.osero.eva[0] = eva
                    elif white == osero.PLAY_WAY["nhand_custom"]:
                        self.osero.eva[0] = eva_custom
                    data_ele, result_ele = self.osero.play()
                    for data_each_turn in data_ele:
                        self.data.append(data_each_turn)
                    for result_each_turn in result_ele:
                        self.result.append(result_each_turn)

        self.data = np.array(self.data, dtype=np.float32)
        self.result = np.array(self.result, dtype=np.float32)

        self.dataset = TupleDataset(self.data, self.result)
        train_val, self.test = split_dataset_random(\
            self.dataset,
            int(len(self.dataset) * 0.7),
            seed = 0
        )
        self.train, self.valid = split_dataset_random(\
            train_val,
            int(len(train_val) * 0.7),
            seed = 0
        )
        self.train_iter = SerialIterator(
            self.train,
            batch_size = self.batch_size,
            repeat = True,
            shuffle = True
        )
    
    def fit(self):
        net = self.Net()

        optimizer = self.optimizer
        optimizer.setup(net)

        for param in net.params():
            if param.name != "b":
                param.update_rule.add_hook(self.hook_f)
        
        results_train = {
            "MSE": [],
            "MAE": []
        }
        results_valid = {
            "MSE": [],
            "MAE": []
        }

        self.train_iter.reset()

        for epoch in range(self.n_epoch):
            while True:
                train_batch = self.train_iter.next()
                
                x_train, t_train = concat_examples(train_batch)

                y_train = net(x_train)
                MSE_train = F.mean_squared_error(y_train, t_train)
                MAE_train = F.mean_absolute_error(y_train, t_train)

                net.cleargrads()
                MSE_train.backward()

                self.optimizer.update()

                if self.train_iter.is_new_epoch:
                    with chainer.using_config("train", False), chainer.using_config("enable_backprop", False):
                        x_valid, t_valid = concat_examples(self.valid)
                        y_valid = net(x_valid)
                        MSE_valid = F.mean_squared_error(y_valid, t_valid)
                        MAE_valid = F.mean_absolute_error(y_valid, t_valid)

                    results_train["MSE"].append(MSE_train.array)
                    results_train["MAE"].append(MAE_train.array)
                    results_valid["MSE"].append(MSE_valid.array)
                    results_valid["MAE"].append(MAE_valid.array)

                    break
        
        self.results_train = results_train
        self.results_valid = results_valid
        self.net = net

    def plot(self, row: int) -> None:
        self.ax[row-1][0].plot(self.results_train["MSE"], label="train")
        self.ax[row-1][0].plot(self.results_valid["MSE"], label="valid")
        self.ax[row-1][0].legend()
        self.ax[row-1][0].set_xlabel(self.xlabel1)
        self.ax[row-1][0].set_ylabel(self.ylabel1)
        self.ax[row-1][0].set_title(self.fig_name1)

        self.ax[row-1][1].plot(self.results_train["MAE"], label="train")
        self.ax[row-1][1].plot(self.results_valid["MAE"], label="valid")
        self.ax[row-1][1].legend()
        self.ax[row-1][1].set_xlabel(self.xlabel2)
        self.ax[row-1][1].set_ylabel(self.ylabel2)
        self.ax[row-1][1].set_title(self.fig_name2)
    
    def cal_test_error(self):
        x_test, t_test = concat_examples(self.test)
        with chainer.using_config("train", False),\
             chainer.using_config("enable_backprop", False):
            y_test = self.net(x_test)
            MSE_test = F.mean_squared_error(y_test, t_test)
            MAE_test = F.mean_absolute_error(y_test, t_test)
        return MSE_test, MAE_test