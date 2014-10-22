#! /usr/bin/env python

import json
import subprocess
import os

def train(data, jarfile):
    lang = data['lang']
    modelfile = '%s.marmot' % lang
    logfile = '%s.log' % lang
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])

    cmd_string = """ java -cp %(jar)s marmot.morph.cmd.Trainer 
                      -train-file %(train)s 
                      -model-file %(model)s
                      -num-iterations 1
                      > %(log)s
                      2>&1
          """ % { 'jar' : jarfile, 'train' : trainfile, 'model' : modelfile, 'log' : logfile }

    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)
    assert os.path.exists(modelfile)

if __name__ == '__main__':

    with open('data.json') as f:
        for lang_data in json.load(f):
            train(lang_data, '../marmot-2014-10-22.jar')
