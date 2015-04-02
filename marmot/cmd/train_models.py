#! /usr/bin/env python

import glob
import json
import os
import subprocess
import sys
import threading
import time

def stats(data, jarfile):

    lang = data['lang']
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])

    cmd_string = """ java -cp %(jar)s marmot.morph.cmd.Stats 
                      -train-file %(train)s 

          """ % { 'jar' : jarfile, 'train' : trainfile }

    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)

def eval(data, jarfile, dirname):

    lang = data['lang']
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])
    options = data.get('options', '')
    logfile = '%s.log' % lang

    cmd_string = """ java -Xmx4g -cp %(jar)s marmot.morph.cmd.CrossAnnotator
                      -train-file %(train)s 
                      -verbose true
                      %(options)s > %(logfile)s 2>&1
          """ % { 'jar' : jarfile, 'train' : trainfile, 'options' : options, 'logfile' : logfile }

    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)

def train(data, jarfile, dirname):

    lang = data['lang']
    modelfile = os.path.join(dirname, '%s.marmot' % lang)
    trainfile = '%s,%s' % (data['marmot-indexes'], data['cis-path'])
    options = data.get('options', '')

    cmd_string = """ java -Xmx4g -cp %(jar)s marmot.morph.cmd.Trainer 
                      -train-file %(train)s 
                      -model-file %(model)s
                      %(options)s
          """ % { 'jar' : jarfile, 'train' : trainfile, 'model' : modelfile, 'options' : options }

    print >> sys.stderr, 'Training %s.' % lang
    sys.stdout.flush()
    subprocess.check_call(cmd_string.replace('\n',' '), shell=True)
    assert os.path.exists(modelfile)

def get_jarfile(dirname):
    jars = glob.glob(os.path.join(dirname, 'marmot*.jar'))

    if not jars:
        print >> sys.stderr, 'Error: Did not find marmot*.jar in "%s".' % dirname
        sys.exit(1)

    # Return latest jar file.
    jars.sort()
    return jars[-1]

num_workers = 4

def main(args):

    if len(args) < 3:
        print >> sys.stderr, 'Usage: train_model.py <dir-with-marmot-jar> <json-file> <output-dir> (<num_cores>)'
        sys.exit(1)

    if len(args) > 3:
        num_workers = int(args[3])

    print num_workers

    marmot_dir = args[0]


    jarfile = get_jarfile(marmot_dir)

    threads = []

    dirname = args[2]
    if not os.path.isdir(dirname):
        print >> sys.stderr, 'Error: not a directory: %s' % dirname
        sys.exit(2)

    with open(args[1]) as f:
        for lang_data in json.load(f):
            print lang_data['lang']
            sys.stdout.flush()
            #stats(lang_data, jarfile)
            print
            sys.stdout.flush()

            t = threading.Thread(target=train, args=(lang_data, jarfile, dirname))
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
        
if __name__ == '__main__':

    main(sys.argv[1:])
