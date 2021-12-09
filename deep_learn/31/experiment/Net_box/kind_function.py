from chainer import Chain
import chainer.links as L
import chainer.functions as F

functions_arr = [
    F.relu,
    F.tanh,
    F.elu,
    F.leaky_relu,
    F.rrelu,
    F.selu
]

functions_name = [
    "relu",
    "tanh",
    "elu",
    "leaky_relu",
    "rrelu",
    "selu"
]

func = None

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
        h = func(self.l1(x))
        h = func(self.l2(h))
        h = self.l3(h)

        return h