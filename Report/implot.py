import matplotlib.pyplot as plt
import numpy as np

w, h = 100, 5;
array = [[0 for x in range(w)] for y in range(h)] 

i, j = 0, 0

with open('step.txt','r') as f:
	for line in f:
		for word in line.split():
			word = word.replace(',', '.')
			array[i][j] = float(word)
			if j == 99:
				i += 1
				j = 0
			else:
				j += 1

plt.subplot(111)
plt.title("Temperature distribution")
plt.imshow(array)

plt.subplots_adjust(left=0.05, bottom=0.0, right=0.8, top=1.0)
cax = plt.axes([0.82, 0.1, 0.075, 0.8])
plt.colorbar(cax=cax).ax.set_ylabel("Temperature [°C]")
plt.show()
