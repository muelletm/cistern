# 1.2: need to make sure opt.o goes in the right order to get the right scope on the command-line arguments
# Use this for Linux
ifeq ($(shell uname),Linux)
	files=$(subst .cc,.o,basic/logging.cc $(shell /bin/ls basic/*.cc | grep -v logging.cc))
else
	files=$(subst .cc,.o,basic/opt.cc $(shell /bin/ls basic/*.cc | grep -v opt.cc))
endif

all: marlin-cluster

marlin-cluster: marlin_cluster.o $(files)
	g++ -std=c++11 -Wall -g -O3 -o marlin-cluster marlin_cluster.o $(files)

%.o: %.cc
	g++ -std=c++11 -Wall -g -O3 -o $@ -c $<


clean:
	rm -rf marlin-cluster basic/*.o *.o
