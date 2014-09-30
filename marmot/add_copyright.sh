#!/bin/bash

set -ue

for file in $(grep -L "GPLv3" $(find -name "*.java")); do

   	echo $file
	sed -i '1s/^/\/\/ Copyright 2014 Thomas Müller\n\/\/ This file is part of MarMoT, which is licensed under GPLv3.\n\n/' $file

done
