#/bin/bash

set -ue

LIBS=bin:lib/mallet.jar:lib/JSAP-2.1.jar
lang=eng

# wget ...
# tar xzf supplement.tar.gz

java -cp $LIBS chipmunk.segmenter.cmd.Train\
   --train-file supplement/seg/${lang}/trn\
   --lang ${lang}\
   --crf-mode false\
   --tag-level 0\
   --dictionary-paths "supplement/additional/${lang}/aspell.txt supplement/additional/${lang}/wordlist.txt supplement/additional/${lang}/wiktionary.txt"\
   --model-file ${lang}.chipmunk.srl

echo 'books booked booking bookings rebooked bookstore' | tr ' ' '\n' > input.txt

java -cp $LIBS chipmunk.segmenter.cmd.Segment\
      --model-file ${lang}.chipmunk.srl\
      --input-file input.txt\
      --output-file output.txt

cat output.txt

