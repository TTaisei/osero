from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.tree import DecisionTreeClassifier
from sklearn.neighbors import KNeighborsClassifier

state = ["win", "lose"]



for i in state:
    
    x = []
    y = []
    
    filename = "whitedata%s.txt" % i
    
    with open(filename, "r") as data:
        for num in data:
            x.append([])
            for j in range(3):
                x[-1].append(int(num[0:3]))
                num = num[3:]
            y.append(int(num.strip("\n")))
            if y[-1] == -1:
                del x[-1]
                del y[-1]
            
    # print(x)
    # print(y)
    
    print(filename, ": ")

    x_train, x_test, y_train, y_test = train_test_split(x, y,
                                                        test_size=0.3,
                                                        random_state=0)

    model = LogisticRegression()
    model.fit(x_train, y_train)
    print("LogisticRegression:")
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