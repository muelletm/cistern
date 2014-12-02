#!/bin/bash

set -ue

#python ../../cmd/extract_spmrl.py /mounts/data/proj/marmot/treebanks/spmrl_2013/
python ../../cmd/train_models.py ../../ sprml.de.json . 5
