#!/usr/bin/env python
# -*- coding: utf-8 -*-

import sys
import os
import glob
import json

def addFile(files, lang_dir, name):
    pattern = os.path.join(lang_dir, 'gold/conll/%s/*.*.gold.conll')        
    current_files = glob.glob(pattern % name)
    if not current_files:
        if name == 'train':
            current_files = glob.glob(pattern % 'train5k')

    if not current_files:
        print >> sys.stderr, 'Didn\'t traing file of pattern: %s' % pattern

    assert len(current_files) == 1, 'Ups, found mutiple files %s' % current_files   
    files.append(current_files[0])

LANG_DICT = {'polish' : 'pl', 'french' : 'fr', 'basque' : 'eu', 'german' : 'de', 'arabic' : 'ar', 'hebrew' : 'he', 'swedish' : 'sv', 'hungarian' : 'hu', 'korean' : 'ko'}
LANG_SRC = {'pl': 'Składnica Treebank', 'ar' : 'LDC Arabic Penn Treebank / Columbia Arabic Treebank', 'eu' : 'Basque Syntactic Treebank', 'fr' : 'French Treebank', 'he' : 'Modern Hebrew Treebank', 'sv' : 'Talbanken', 'hu' : 'Szeged (Dependency) Treebank', 'de' : 'Tiger 2.0', 'ko' : 'KAIST Treebank' }

def getLangCode(lang_dir):
    basename = os.path.split(lang_dir)[1]
    langname = basename.split('_')[0]

    return LANG_DICT[langname.lower()]

def replaceArabicMorphTag(tag):
    key_values = tag.split('|')
    tag = '_'
    for key_value in key_values:
        if key_value.startswith('atbpos='):
            tag = key_value[7:]

            tag = list(tag)
            for i,c in enumerate(tag):
                if c in ['.','_', '+', '-', ':']:
                   tag[i] = '|'
            tag = ''.join(tag)

    if not tag:
        tag = '_'
    return tag

BASQUE_REMOVE = ['ENT', 'HIT', 'IZAUR', 'BIZ', 'KLM', 'MAI', 'MTKAT', 'MUG', 'MW', 'NEUR', 'NMG', 'PER', 'PLU', 'ZENB' ]
FRENCH_REMOVE = ['MWEHEAD', ]

def removeKeyValuePair(kv, remove_set):
    kv = kv.upper()
    for prefix in remove_set:
        if kv.startswith(prefix):
            return True
    return False

def replaceMorphTag(tag, remove_set):
    key_values = tag.split('|')
    key_values = [kv for kv in key_values if not removeKeyValuePair(kv, remove_set) ]
    if key_values:
        tag = '|'.join(key_values)
    else:
        tag = '_'
    assert tag
    return tag

def main(spmrl_dir):

    dicts = []

    pattern = os.path.join(spmrl_dir, '*_SPMRL')
    for lang_dir in glob.glob(pattern):
        print lang_dir

        files = []
        addFile(files, lang_dir, 'train')
        addFile(files, lang_dir,'dev')
        addFile(files, lang_dir,'test')

        lang_code = getLangCode(lang_dir)
        output_file = lang_code  + '.conll'

        with open(output_file, 'w') as f_out:
            for filename in files:
                with open(filename) as f:
                    for line in f:

                        tokens = line.split()

                        if tokens:

                            if lang_code == 'ar':
                                tokens[5] = replaceArabicMorphTag(tokens[5])
                            elif lang_code ==  'eu':
                                tokens[5] = replaceMorphTag(tokens[5], BASQUE_REMOVE)
                            elif lang_code ==  'fr':
                                tokens[5] = replaceMorphTag(tokens[5], FRENCH_REMOVE)

                            f_out.write('\t'.join(tokens[:6]))

                        f_out.write('\n')

        lang_indexes = 'form-index=1,tag-index=4,morph-index=5'
        if lang_code == 'ko':
            lang_indexes = 'form-index=1,tag-index=3,morph-index=5'            

        lang_dict = ({ "lang" : lang_code,
                       "source-url" : "http://dokufarm.phil.hhu.de/spmrl2013/doku.php",
                       "source" : LANG_SRC[lang_code],
                       "cis-path" : output_file,
                        "marmot-indexes" : lang_indexes})

        if lang_code == 'ar':
            lang_dict['comment'] = 'Extracted atbpos value. Replaced ., _, :, - and + with |'

        dicts.append(lang_dict)

    with open('sprml.json', 'w') as f:
        json.dump(dicts, f)

                
if __name__ == '__main__':

    main(sys.argv[1])
