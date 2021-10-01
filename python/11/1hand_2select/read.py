from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.linear_model import LogisticRegression
from sklearn.linear_model import Ridge
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import LinearSVC

x = []
y = []

with open("whitedata.txt", "r") as data:
    for num in data:
        x.append([])
        x[-1].append(int(num[0:3]))
        x[-1].append(int(num[3:6]))
        x[-1].append(int(num[6:9]))
        y.append(int(num[-2]))
        if all([x[-1][i] == 0 for i in range(3)]):
            del x[-1]
            del y[-1]
        
# print(x)
# print(y)

x_train, x_test, y_train, y_test = train_test_split(x, y,
                                                    test_size=0.3,
                                                    random_state=0)

model = LinearRegression()
model.fit(x_train, y_train)
print("LinearRegression:")
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test), "\n")

model = LogisticRegression()
model.fit(x_train, y_train)
print("LogisticRegression:")
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test), "\n")

model = Ridge(random_state=0)
model.fit(x_train, y_train)
print("Ridge:")
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test), "\n")

model = DecisionTreeClassifier(criterion="entropy",
                               max_depth=10,
                               random_state=0)
model.fit(x_train, y_train)
print("DecisionTreeClassifier:")
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test), "\n")

model = KNeighborsClassifier(n_neighbors=10)
model.fit(x_train, y_train)
print("KNeighborsClassifier:")
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test), "\n")

model = LinearSVC()
model.fit(x_train, y_train)
print("LinearSVC:")
print("train score:\t", model.score(x_train, y_train))
print("test score:\t", model.score(x_test, y_test))