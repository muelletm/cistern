#! /usr/bin/env python

import glob
import json
import os
import subprocess
import sys

MARMOT_DIR = ".."

def train(data, jarfile):


    lang = data['lang']
    modelfile = '%s.marmot' % lang
    logfile = '%s.log' % lang
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])

    cmd_string = """ java -cp %(jar)s marmot.morph.cmd.Trainer 
                      -train-file %(train)s 
                      -model-file %(model)s
                      > %(log)s
                      2>&1
          """ % { 'jar' : jarfile, 'train' : trainfile, 'model' : modelfile, 'log' : logfile }

    print lang, trainfile
    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)
    assert os.path.exists(modelfile)

def get_jarfile(dirname):
    jars = glob.glob(os.path.join(dirname, 'marmot*.jar'))

    if not jars:
        print >> sys.stderr, 'Error: Did not find marmot*.jar in "%s".' % dirname
        sys.exit(1)

    jars.sort()
    return jars[-1]

if __name__ == '__main__':

    jarfile = get_jarfile(MARMOT_DIR)

    with open('data.json') as f:
        for lang_data in json.load(f):
            train(lang_data, jarfile)
