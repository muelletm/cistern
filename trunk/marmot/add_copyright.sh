#!/bin/bash

set -ue

for file in $(grep -L "Copyright 2013 Thomas Müller" $(find -name "*.java")); do

   	echo $file
	sed -i '1s/^/\/\/ Copyright 2013 Thomas Müller\n\/\/ This file is part of MarMoT, which is licensed under GPLv3.\n\n/' $file

done