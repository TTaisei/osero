playMethod = [
    "random",
    "nHand",
    "nHandCustom",
    "nLeast",
    "nMost",
    "oseroAI"
]

with open("w.txt", "w") as fp:
    for p in playMethod:
        for i in range(2):
            if p != "oseroAI":
                fp.write("playMethod.add(Osero::%s);\tplayMethodName.add(\"%s\");\treadGoals.add(%d);\n"
                         % (p, p, i+1)
                )
            else:
                fp.write("playMethod.add(OseroAI::%s);\tplayMethodName.add(\"%s\");\treadGoals.add(%d);\n"
                         % (p, p, i+1)
                )