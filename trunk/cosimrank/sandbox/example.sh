#!/bin/sh -x

cd "$( dirname "${BASH_SOURCE[0]}" )"
../similarity \
graphs/seed/AIO_small_seed.mtx \
testsets/ts1000 \
graphs/english/AIO_small_link_A.mtx \
graphs/german/AIO_small_link_B.mtx \
graphs/english/AIO_small_dictionary_A.txt \
graphs/german/AIO_small_dictionary_B.txt
