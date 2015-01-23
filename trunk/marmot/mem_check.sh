#! /bin/bash

dir=$(mktemp -d)
trainfile=$dir/train.txt

cat src/marmot/test/train_fst.txt > $trainfile

for i in $(seq 1 10); do

    wc -l $trainfile

    (
    /usr/bin/time -v java -cp bin marmot.morph.cmd.Trainer\
       -train-file form-index=1,tag-index=4,morph-index=6,$trainfile\
       -seed 42\
       -num-iterations 1\
       -shape false\
       -model-file $dir/model_${i}.marmot
     ) > $dir/log_${i} 2>&1

    grep -e 'Maximum resident set size' $dir/log_${i}

    cat $trainfile > $dir/swap
    cat $dir/swap >> $trainfile
done

rm -rf $trainfile
