all: marmot_aspell

marmot_aspell: marmot_aspell.o
	libtool --mode=link gcc -std=c99 -Wunused -Wall -Werror -g -O3 -o marmot_aspell marmot_aspell.c /usr/lib/libaspell.la
	#gcc -static -std=c99 -Wunused -Wall -Werror -g -O3 -laspell -o marmot_aspell marmot_aspell.o

%.o: %.c
	gcc -std=c99 -Wunused -Wall -Werror -g -O3 -o $@ -c $<

clean:
	rm -rf marmot_aspell.o
