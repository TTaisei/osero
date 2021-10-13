import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("data_rand_2hand.csv")

x = df["srand_num"]
y = df["win_per"]

fig = plt.figure()
plt.plot(x, y)
plt.show()
plt.clf()
plt.close()