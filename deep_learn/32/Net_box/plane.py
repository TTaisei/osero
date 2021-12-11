from chainer import Chain
import chainer.links as L
import chainer.functions as F

class Net(Chain):
    def __init__(self):
        super().__init__()
        n_in = 128
        n_hidden = 128
        n_out = 1
        with self.init_scope():
            self.l1 = L.Linear(n_in, n_hidden)
            self.l2 = L.Linear(n_hidden, n_hidden)
            self.l3 = L.Linear(n_hidden, n_out)

    def __call__(self, x):
        h = F.tanh(self.l1(x))
        h = F.tanh(self.l2(h))
        h = self.l3(h)

        return h