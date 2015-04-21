all: marmot_aspell

marmot_aspell: marmot_aspell.o
	gcc -std=c99 -Wunused -Wall -Werror -g -O3 -laspell -o marmot_aspell marmot_aspell.o

%.o: %.c
	gcc -std=c99 -Wunused -Wall -Werror -g -O3 -o $@ -c $<

clean:
	rm -rf marmot_aspell.o
