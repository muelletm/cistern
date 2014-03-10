CC = g++
CFLAGS = -lm -fopenmp -pthread -march=native -Wall -Wno-unused-result -g -O0 #-Ofast

OBJ = edge.o matrix.o testset.o mmio.o arithmetic.o

all: similarity

%.o: %.cpp
	$(CC) $(CFLAGS) -c -o $@ $<

similarity: similarity.cpp $(OBJ)
	$(CC) $(CFLAGS) -o $@ $^
	
clean:
	rm -rf $(OBJ)