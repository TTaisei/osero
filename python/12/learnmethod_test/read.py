from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier
import matplotlib.pyplot as plt
import numpy as np

savedir = "pic/%s"

x = []
y = []

with open("whitedatawin.txt", "r") as data:
    for num in data:
        x.append([])
        for i in range(3):
            x[-1].append(int(num[0:3]))
            num = num[3:]
        y.append(int(num.strip("\n")))
        if y[-1] == -1:
            del x[-1]
            del y[-1]
        
# print(x)
# print(y)

########  learning  ########

x_train, x_test, y_train, y_test = train_test_split(x, y,
                                                    test_size=0.3,
                                                    random_state=0)

########  criterion  ########

criterion = ["entropy", "gini"]

for i in range(len(criterion)):
    model = DecisionTreeClassifier(criterion=criterion[i],
                                   random_state=0)
    model.fit(x_train, y_train)
    print(criterion[i], ": ")
    print("train score:\t", model.score(x_train, y_train))
    print("test score:\t", model.score(x_test, y_test), "\n")

########  splitter  ########

splitter = ["best", "random"]

for i in range(len(splitter)):
    model = DecisionTreeClassifier(splitter=splitter[i],
                                   random_state=0)
    model.fit(x_train, y_train)
    print(splitter[i], ": ")
    print("train score:\t", model.score(x_train, y_train))
    print("test score:\t", model.score(x_test, y_test), "\n")

########  max_features  ########

features = []
train = []
test = []

for i in range(1, 4):
    model = DecisionTreeClassifier(max_features=i,
                                   random_state=0)
    model.fit(x_train, y_train)
    features.append(i)
    train.append(model.score(x_train, y_train))
    test.append(model.score(x_test, y_test))

fig = plt.figure(figsize=(10, 10))
plt.plot(features, train, label="train")
plt.plot(features, test, label="test")
plt.title("max_features")
plt.xlabel("features")
plt.ylabel("accuracy")
plt.legend()
plt.savefig(savedir % "max_features")

########  max_depth  ########

depth = []
train = []
test = []

for i in range(1, 41):
    model = DecisionTreeClassifier(max_depth=i,
                                   random_state=0)
    model.fit(x_train, y_train)
    depth.append(i)
    train.append(model.score(x_train, y_train))
    test.append(model.score(x_test, y_test))

fig = plt.figure(figsize=(10, 10))
plt.plot(depth, train, label="train")
plt.plot(depth, test, label="test")
plt.title("max_depth")
plt.xlabel("depth")
plt.ylabel("accuracy")
plt.legend()
plt.savefig(savedir % "max_depth")

########  min_samples_split  ########

split = []
train = []
test = []

for i in np.arange(0.1, 1.1, 0.1):
    model = DecisionTreeClassifier(min_samples_split=i,
                                   random_state=0)
    model.fit(x_train, y_train)
    split.append(i)
    train.append(model.score(x_train, y_train))
    test.append(model.score(x_test, y_test))

for i in range(2, 31):
    model = DecisionTreeClassifier(min_samples_split=i,
                                   random_state=0)
    model.fit(x_train, y_train)
    split.append(i)
    train.append(model.score(x_train, y_train))
    test.append(model.score(x_test, y_test))

fig = plt.figure(figsize=(10, 10))
plt.plot(split, train, label="train")
plt.plot(split, test, label="test")
plt.title("min_samples_split")
plt.xlabel("samples_split")
plt.ylabel("accuracy")
plt.legend()
plt.savefig(savedir % "min_samples_split")

########  min_samples_leaf  ########

leaf = []
train = []
test = []

for i in range(1, 101):
    model = DecisionTreeClassifier(min_samples_leaf=i,
                                   random_state=0)
    model.fit(x_train, y_train)
    leaf.append(i)
    train.append(model.score(x_train, y_train))
    test.append(model.score(x_test, y_test))

fig = plt.figure(figsize=(10, 10))
plt.plot(leaf, train, label="train")
plt.plot(leaf, test, label="test")
plt.title("min_samples_leaf")
plt.xlabel("samples_leaf")
plt.ylabel("accuracy")
plt.legend()
plt.savefig(savedir % "min_samples_leaf")

########  max_leaf_nodes  ########

nodes = []
train = []
test = []

for i in range(2, 501):
    model = DecisionTreeClassifier(max_leaf_nodes=i,
                                   random_state=0)
    model.fit(x_train, y_train)
    nodes.append(i)
    train.append(model.score(x_train, y_train))
    test.append(model.score(x_test, y_test))

fig = plt.figure(figsize=(10, 10))
plt.plot(nodes, train, label="train")
plt.plot(nodes, test, label="test")
plt.title("max_leaf_nodes")
plt.xlabel("leaf_nodes")
plt.ylabel("accuracy")
plt.legend()
plt.savefig(savedir % "max_leaf_nodes")

plt.clf()
plt.close()