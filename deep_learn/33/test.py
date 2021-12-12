from random import randint

import chainer
import numpy as np

from net import Net

loaded_net = Net()

chainer.serializers.load_npz("deep_AI.net", loaded_net)

# board = np.array([[1 for i in range(128)]], dtype=np.float32)
board = np.array([[randint(0, 1) for i in range(128)]], dtype=np.float32)
print(board)
print(loaded_net(board).array[0][0])