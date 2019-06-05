import matplotlib.pyplot as plt
import sys
import csv

x = []
y = []

for i in range(1, (int(sys.argv[2]) + 1)):
    with open(sys.argv[1] + str(i) + ".txt",'r') as csvfile:
        plots = csv.reader(csvfile, delimiter=';')
        for row in plots:
            x.append(float(row[0]))
            y.append(float(row[1]))
    plt.plot(x, y, c='r', zorder = 0)
    x = []
    y = []

with open(sys.argv[3] + ".txt",'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=' ')
    for row in plots:
        x.append(float(row[0]))
        y.append(float(row[1]))
plt.scatter(x, y, c='g', zorder = 1)
x = []
y = []

with open("output0.txt",'r') as csvfile:
    plots = csv.reader(csvfile, delimiter=';')
    for row in plots:
        x.append(float(row[0]))
        y.append(float(row[1]))
plt.plot(x, y, c='k', label="K=" + str(int(sys.argv[2])), zorder = 2)
x = []
y = []

plt.xlabel('X')
plt.ylabel('Y')
plt.title('RBF')
plt.grid(True)
plt.legend()
plt.show()
plt.savefig("Out" + str(int(sys.argv[2]) - 1) + ".png")