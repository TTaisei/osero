import pandas as pd
import glob

for filename in glob.glob("*.csv"):
    df = pd.read_csv(filename)
    per = df["win_per"]

    print(filename, ": ")
    print("mean:", per.mean())
    print("var: ", per.var())
    print()