LIBS=marmot.jar:lib/mallet.jar:lib/JSAP-2.1.jar

# wget ...
# tar xzf supplement.tar.gz

java -cp $LIBS marmot.segmenter.SegmenterTrainer\
  --train-path supplement/seg/eng/trn\
  --lang eng\
  --crf-mode true\
  --dict-paths supplement/additional/eng/aspell.txt,supplement/additional/eng/wordlist.txt,supplement/additional/eng/wiktionary.txt\
  --model-path eng.chipmunk.srl

echo 'books booked booking bookings rebooked' | tr ' ' '\n' > input.txt

java -cp $LIBS marmot.segmenter.Segmenter\
      --model-path eng.chipmunk.srl\
      --in-file input.txt\
      --out-file output.txt

echo output.txt

