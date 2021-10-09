import pandas as pd
import matplotlib.pyplot as plt

df = pd.read_csv("data_1hand_rand.csv")
per = df["win_per"]

mother_num = 10
sum = 0
num = 0
x = []
y = []
for i in per:
    sum += i
    num += 1
    if num == mother_num:
        num = 0
        y.append(sum / mother_num)
        sum = 0
    
x = [(i + 1) * mother_num for i in range(len(y))]

fig = plt.figure
plt.title("mother_num: %d" % mother_num)
plt.plot(x, y)
plt.show()
plt.clf()
plt.close()