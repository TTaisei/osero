import pandas as pd
import glob
import matplotlib.pyplot as plt
import numpy as np

pd.set_option("display.max_columns", 6)

########  all data ########

i = 0

for filename in glob.glob("data*.csv"):
    win_per = filename + "_win_per"
    if i == 0:
        df = pd.read_csv(filename)
        df.columns = ["srand_num", win_per]
        i = 1
    else:
        df_ele = pd.read_csv(filename)
        df_ele.columns = ["srand_num", win_per]
        df.loc[:, win_per] = df_ele[win_per]

df.to_csv("csvdata.csv")

########  part data  ########

mother_num = 10
df_custom = []
for_num = int((max(df["srand_num"]) + 1) / mother_num)
srand_num = []
mean = []

data = open("data/data.txt", "w")

for i in range(for_num):
    # get data
    num = i * mother_num
    df_custom.append(df[num:num + mother_num])
    
    # plot data
    srand_num.append(num)
    mean_ele = df_custom[i].mean()
    mean.append(list(mean_ele[1:]))
    
    # statistics data
#     df.to_csv("data/%4d_%4d.csv" % (num, num + mother_num))
    data.write("%4d_%4d:\n" % (num, num + mother_num))
    data.write(str(df_custom[i].describe()) + "\n\n")

data.close()

# plot

mean = np.array(mean)
mean = mean.T
fig = plt.figure()
i = 0
for filename in glob.glob("data*.csv"):
    plt.plot(srand_num, mean[i], label=filename)
    i += 1
plt.legend()
plt.title("mother_num=%d" % mother_num)
plt.xlabel("srand_num")
plt.ylabel("mean win per")
# plt.show()
plt.savefig("data/fig%d" % mother_num)
plt.clf()
plt.close()

# normalized plot

fig = plt.figure()
i = 0
for filename in glob.glob("data*.csv"):
    arr_mean = mean[i].mean()
    arr_std = np.std(mean[i])
    mean[i] = (mean[i] - arr_mean) / arr_std
    plt.plot(srand_num, mean[i], label=filename)
    i += 1
plt.legend()
plt.title("mother_num=%d, normalized" % mother_num)
plt.xlabel("srand_num")
plt.ylabel("mean win per")
# plt.show()
plt.savefig("data/fig%d, normalized" % mother_num)
plt.clf()
plt.close()