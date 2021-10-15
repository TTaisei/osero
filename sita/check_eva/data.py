from glob import glob

all_eva = open("all_eva.txt", "w")
name = open("filename.txt", "w")

for filename in glob("csv/eva*.csv"):
    name.write(filename.lstrip("csv\\") + "\n")
    with open(filename, "r") as data:
        for num in data:
            num = num.rstrip("\n")
            if num != ",,,,,,,":
                while num != "":
                    if num[0] != ",":
                        all_eva.write(num[0])
                    else:
                        all_eva.write("\n")
                    num = num[1:]
                else:
                    all_eva.write("\n")

all_eva.close()
name.close()