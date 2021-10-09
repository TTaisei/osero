import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("data_1hand_rand.csv")

x = df["num"]
y = df["win_per"]

fig = plt.figure()
plt.plot(x, y)
plt.show()
plt.clf()
plt.close()