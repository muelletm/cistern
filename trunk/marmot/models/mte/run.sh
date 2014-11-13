#!/bin/bash
 
set -ue

ANA_PATH='/mounts/data/proj/marmot/treebanks/mteV4-2010-05-14/ana'

if [ ! -e mte.json ]; then
    python ../../cmd/extract_and_map_mte.py $ANA_PATH bg cs en et fa hu pl ro sk sl sr
fi

python ../../cmd/train_models.py ../../ mte.json .
