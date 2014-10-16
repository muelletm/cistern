#! /usr/bin/bash

set -ue

ant

FILES="*.sh *.jar build.xml README src"
DATE=$(date +"%y-%m-%d")
NAME=hmmla
ROOT=$(mktemp -d)

DIR_PATH=$ROOT/${NAME}-${DATE}
mkdir -p $DIR_PATH
cp -r $FILES $DIR_PATH

tar czvf ${NAME}-${DATE}.tar.gz -C $ROOT ${NAME}-${DATE}

rm -rf $ROOT
