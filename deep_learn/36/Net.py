import chainer.links as L
import chainer.functions as F
from chainer import Chain

n_multi = 2
func = F.tanh

class Net(Chain):
    def __init__(self):
        n_in = 192
        n_hidden = 192
        n_out = 1
        super().__init__()
        with self.init_scope():
            self.l1 = L.Linear(n_in, n_hidden)
            self.l2 = L.Linear(n_hidden, n_hidden)
            self.l3 = L.Linear(n_hidden, n_hidden)
            self.l4 = L.Linear(n_hidden, n_hidden)
            self.l5 = L.Linear(n_hidden, n_hidden)
            self.l6 = L.Linear(n_hidden, n_hidden)
            self.l7 = L.Linear(n_hidden, n_hidden)
            self.l8 = L.Linear(n_hidden, n_hidden)
            self.l9 = L.Linear(n_hidden, n_hidden)
            self.l10 = L.Linear(n_hidden, n_out)
        
    def __call__(self, x):
        h = n_multi * func(self.l1(x))
        h = n_multi * func(self.l2(h))
        h = n_multi * func(self.l3(h))
        h = n_multi * func(self.l4(h))
        h = n_multi * func(self.l5(h))
        h = n_multi * func(self.l6(h))
        h = n_multi * func(self.l7(h))
        h = n_multi * func(self.l8(h))
        h = n_multi * func(self.l9(h))
        h = self.l10(h)

        return h