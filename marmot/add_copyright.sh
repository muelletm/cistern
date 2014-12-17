#!/bin/bash

set -u

for file in $(grep -L "GPLv3" $(find src -name "*.java")); do
	match=$(expr match $file "^.*/thirdparty/.*$")

	if [ $match -eq 0 ]; then
		echo $file
		sed -i '1s/^/\/\/ Copyright 2014 Thomas Müller\n\/\/ This file is part of MarMoT, which is licensed under GPLv3.\n\n/' $file
	fi
done
