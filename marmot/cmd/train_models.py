#! /usr/bin/env python

import glob
import json
import os
import subprocess
import sys
import threading
import time

MARMOT_DIR = ".."

def stats(data, jarfile):

    lang = data['lang']
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])

    cmd_string = """ java -cp %(jar)s marmot.morph.cmd.Stats 
                      -train-file %(train)s 

          """ % { 'jar' : jarfile, 'train' : trainfile }

    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)

def train(data, jarfile):

    lang = data['lang']
    modelfile = '%s.marmot' % lang
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])

    cmd_string = """ java -cp %(jar)s marmot.morph.cmd.Trainer 
                      -train-file %(train)s 
                      -model-file %(model)s
          """ % { 'jar' : jarfile, 'train' : trainfile, 'model' : modelfile }

    print >> sys.stderr, 'Training %s.' % lang
    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)
    assert os.path.exists(modelfile)

def get_jarfile(dirname):
    jars = glob.glob(os.path.join(dirname, 'marmot*.jar'))

    if not jars:
        print >> sys.stderr, 'Error: Did not find marmot*.jar in "%s".' % dirname
        sys.exit(1)

    jars.sort()
    return jars[-1]

num_workers = 5

if __name__ == '__main__':

    jarfile = get_jarfile(MARMOT_DIR)

    threads = []
    with open('data.json') as f:
        for lang_data in json.load(f):
            print lang_data['lang']
            stats(lang_data, jarfile)
            print

            t = threading.Thread(target=train, args=(lang_data, jarfile))
            threads.append(t)

    while threads:
        current_threads = []

        for i in range(num_workers):
            if threads:
                t = threads.pop()
                t.start()
                time.sleep(1)
                current_threads.append(t)

        for t in current_threads:
            t.join()
        
