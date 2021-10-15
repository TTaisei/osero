import pandas as pd
import matplotlib.pyplot as plt

########  read data  ########

filename = []
with open("filename.txt", "r") as data:
    for line in data:
        filename.append(line[:-5])

df = pd.read_csv("all_data.csv")

########  per file and random  ########

win_lose = []
for i in filename:
    df_ele = df[df.filename == i]
    df_ele = df_ele[df_ele.player == 0]
    win_lose.append(df_ele["win_lose"].mean())

fig = plt.figure(figsize=(10, 10))
plt.bar(filename, win_lose, width=0.8)
plt.xticks(rotation=10)
plt.title("per file and random")
# plt.show()
plt.savefig("fig_custom/per_file_and_random")
plt.cla()

########  per srand_num and random  ########

srand_num = [i for i in range(81, 101)]
win_lose = []
for i in srand_num:
    df_ele = df[df.srand_num == i]
    df_ele = df_ele[df_ele.player == 0]
    win_lose.append(df_ele["win_lose"].mean())

fig = plt.figure(figsize=(10, 10))
plt.bar(srand_num, win_lose, width=0.8)
plt.title("per srand_num and random")
# plt.show()
plt.savefig("fig_custom/per_srand_num_and_random")
plt.clf()
plt.close()