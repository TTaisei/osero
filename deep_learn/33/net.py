import chainer
import chainer.functions as F
import chainer.links as L

class Net(chainer.Chain):
    def __init__(self):
        n_in = 128
        n_hidden = 128
        n_out = 1
        super().__init__()
        with self.init_scope():
            self.l1 = L.Linear(n_in, n_hidden)
            self.l2 = L.Linear(n_hidden, n_hidden)
            self.l3 = L.Linear(n_hidden, n_hidden)
            self.l4 = L.Linear(n_hidden, n_out)
        
    def __call__(self, x):
        h = F.tanh(self.l1(x))
        h = F.tanh(self.l2(h))
        h = F.tanh(self.l3(h))
        h = self.l4(h)

        return h