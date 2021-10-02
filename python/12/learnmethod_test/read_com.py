from sklearn.model_selection import train_test_split
from sklearn.tree import DecisionTreeClassifier

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

model = DecisionTreeClassifier(criterion="entropy",
                               splitter="best",
                               max_depth=15,
                               min_samples_split=2,
                               min_samples_leaf=35,
                               random_state=0,
                               max_leaf_nodes=150)

model.fit(x_train, y_train)
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test))