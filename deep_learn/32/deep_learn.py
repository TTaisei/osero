from copy import deepcopy

import numpy as np
from chainer.datasets import TupleDataset, split_dataset_random
from chainer.iterators import SerialIterator
import chainer
import chainer.functions as F
from chainer import optimizers
from chainer.optimizer_hooks import WeightDecay
from chainer.dataset import concat_examples
import matplotlib.pyplot as plt

from BitBoard import osero
from osero_learn import learn

PLAY_WAY = deepcopy(osero.PLAY_WAY)
del PLAY_WAY["human"]
PLAY_WAY = PLAY_WAY.values()

eva = [[
     1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0,
    -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
     0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
     0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
     0.4,  0.0,  0.6,  0.0,  0.0,  0.6,  0.0,  0.4,
     0.6,  0.0,  0.8,  0.6,  0.6,  0.8,  0.0,  0.6,
    -0.6, -0.8,  0.0,  0.0,  0.0,  0.0, -0.8, -0.6,
     1.0, -0.6,  0.6,  0.4,  0.4,  0.6, -0.6,  1.0
] for i in range(2)]

check_point = [i for i in range(1, 61)]

class deep_learn:
    def __init__(self):
        self.PLAY_WAY = PLAY_WAY
        self.eva = eva
        self.data = []
        self.result = []
        self.osero = learn(0, 0, check_point)
        self.num = 1
        self.n_epoch = 50
        self.batch_size = 50
        class Inner_Net(chainer.Chain):
            pass
        self.Net = Inner_Net
        self.lr = 0.01
        self.optimizer = optimizers.SGD(lr=self.lr)
        self.WeightDecay = 0.00001
        self.hook_f = WeightDecay(self.WeightDecay)
        self.xlabel1, self.xlabel2 = "epoch", "epoch"
        self.ylabel1, self.ylabel2 = "mean squared error", "mean abolute error"
        self.fig_name1, self.fig_name2 = "MSE of each epoch", "MAE of each epoch"
        self.save_dir = "fig"
    
    def set_data(self):
        self.data = []
        self.result = []

        for i in range(self.num):
            for black in self.PLAY_WAY:
                for white in self.PLAY_WAY:
                    self.osero.setup()
                    self.osero.black_method = black
                    self.osero.white_method = white
                    self.osero.eva = self.eva
                    data_ele, result_ele = self.osero.play()
                    for data_each_turn in data_ele:
                        self.data.append(data_each_turn)
                    for result_each_turn in result_ele:
                        self.result.append([result_each_turn])

        self.data = np.array(self.data).astype(np.float32)
        self.result = np.array(self.result).astype(np.float32)

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

    def plot(self):
        fig = plt.figure(figsize=(10, 10))
        plt.plot(self.results_train["MSE"], label="train")
        plt.plot(self.results_valid["MSE"], label="valid")
        plt.legend()
        plt.xlabel(self.xlabel1)
        plt.ylabel(self.ylabel1)
        plt.title(self.fig_name1)
        plt.savefig(self.save_dir + "/" + self.fig_name1)
        plt.clf()
        plt.close()

        fig = plt.figure(figsize=(10, 10))
        plt.plot(self.results_train["MAE"], label="train")
        plt.plot(self.results_valid["MAE"], label="valid")
        plt.legend()
        plt.xlabel(self.xlabel2)
        plt.ylabel(self.ylabel2)
        plt.title(self.fig_name2)
        plt.savefig(self.save_dir + "/" + self.fig_name2)
        plt.clf()
        plt.close()
    
    def cal_test_error(self):
        x_test, t_test = concat_examples(self.test)
        with chainer.using_config("train", False),\
             chainer.using_config("enable_backprop", False):
            y_test = self.net(x_test)
            MSE_test = F.mean_squared_error(y_test, t_test)
            MAE_test = F.mean_absolute_error(y_test, t_test)
        return MSE_test, MAE_test