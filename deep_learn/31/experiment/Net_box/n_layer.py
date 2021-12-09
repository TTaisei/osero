from chainer import Chain
import chainer.links as L
import chainer.functions as F

n_in = 128
n_hidden = 128
n_out = 1

class Net2(Chain):
    def __init__(self):
        super().__init__()
        with self.init_scope():
            self.l1 = L.Linear(n_in, n_hidden)
            self.l2 = L.Linear(n_hidden, n_out)
    
    def __call__(self, x):
        h = F.tanh(self.l1(x))
        h = self.l2(h)
        
        return h


class Net3(Chain):
    def __init__(self):
        super().__init__()
        with self.init_scope():
            self.l1 = L.Linear(n_in, n_hidden)
            self.l2 = L.Linear(n_hidden, n_hidden)
            self.l3 = L.Linear(n_hidden, n_out)
    
    def __call__(self, x):
        h = F.tanh(self.l1(x))
        h = F.tanh(self.l2(h))
        h = self.l3(h)
        
        return h


class Net4(Chain):
    def __init__(self):
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
    

class Net5(Chain):
    def __init__(self):
        super().__init__()
        with self.init_scope():
            self.l1 = L.Linear(n_in, n_hidden)
            self.l2 = L.Linear(n_hidden, n_hidden)
            self.l3 = L.Linear(n_hidden, n_hidden)
            self.l4 = L.Linear(n_hidden, n_hidden)
            self.l5 = L.Linear(n_hidden, n_out)
    
    def __call__(self, x):
        h = F.tanh(self.l1(x))
        h = F.tanh(self.l2(h))
        h = F.tanh(self.l3(h))
        h = F.tanh(self.l4(h))
        h = self.l5(h)
        
        return h