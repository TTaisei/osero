import pandas as pd
import matplotlib.pyplot as plt

########  read data  ########

filename = []
with open("filename.txt", "r") as data:
    for line in data:
        filename.append(line[:-5])

df = pd.read_csv("all_data.csv")

########  per file  ########

win_lose = []
for i in filename:
    df_ele = df[df.filename == i]
    win_lose.append(df_ele["win_lose"].mean())

fig = plt.figure(figsize=(10, 10))
plt.bar(filename, win_lose, width=0.8)
plt.xticks(rotation=10)
plt.title("per file")
# plt.show()
plt.savefig("fig/per_file")
plt.cla()

########  per computer  ########

computer = [1, 2]
win_lose = []

for i in computer:
    df_ele = df[df.computer == i]
    win_lose.append(df_ele["win_lose"].mean())

plt.bar(computer, win_lose, width=0.8)
plt.title("per computer")
# plt.show()
plt.savefig("fig/per_computer")
plt.cla()

########  per player  ########

player = [0, 1, 2]
win_lose = []
for i in player:
    df_ele = df[df.player == i]
    win_lose.append(df_ele["win_lose"].mean())

plt.bar(player, win_lose, width=0.8)
plt.title("per player")
# plt.show()
plt.savefig("fig/per_player")
plt.cla()

########  per srand_num  ########

srand_num = [i for i in range(81, 101)]
win_lose = []
for i in srand_num:
    df_ele = df[df.srand_num == i]
    win_lose.append(df_ele["win_lose"].mean())

plt.bar(srand_num, win_lose, width=0.8)
plt.title("per srand_num")
# plt.show()
plt.savefig("fig/per_srand_num")
plt.clf()
plt.close()