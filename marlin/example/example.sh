#! /bin/bash

set -ue

../marlin_count --text example.txt.bz2 --bigrams bigrams --words words 
../marlin_cluster --words words --bigrams bigrams --output classes --c 100 --steps 5
rm words bigrams
