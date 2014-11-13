#!/usr/bin/env python

import json
import os
import re
import sys
import xml.dom.minidom

TAG_PATTERN = re.compile('^[\w-][0-9]+[\w-]$')

def split(tag_string):
    if tag_string.startswith('#'):
        tag_string = tag_string[1:]

    subtags = []

    first_tag = tag_string[0] 
    assert len(first_tag) == 1, first_tag

    for i,c in enumerate(tag_string):
        assert len(c) == 1, c
        subtag = '%s%d%s' % (first_tag, i, c)
        assert TAG_PATTERN.match(subtag), subtag
        subtags.append(subtag)
        subtag = '%s_%s' % (first_tag, c)
        subtags.append(subtag)
        subtags.append(c)

    tag = '|'.join(subtags)

    assert join(tag) == tag_string, (tag, tag_string)
    return tag

def join(tag_string):

    features = ""
    for tag in tag_string.split('|'):
        if TAG_PATTERN.match(tag):
            features += tag[-1]

    return features

def readSentences(f):
    sentence = []

    number = 0

    for line in f:
        line = line.strip()

        lowerline = line.lower()

        if line:
            if lowerline.startswith('<s xml') or lowerline.startswith('<head xml'):
                assert not sentence, (f.name, line)
            elif lowerline.startswith('</s>') or lowerline.startswith('</head>'):
                assert sentence, (f.name, line)
                yield sentence
                sentence = []
            elif line.startswith('<w xml') or line.startswith('<c xml'):
                sentence.append(line)
            else:
                pass # print >>sys.stderr, number, line

        number += 1

    assert not sentence

def main():

    json_list = []

    ana_path = sys.argv[1]

    for lang in sys.argv[2:]:

        ana_file = os.path.join(ana_path, 'oana-%s.xml' % lang)
        marmot_file = 'oana-%s.marmot.txt' % lang

        data = ({
                 'lang' : lang, 
                 'source-url' : 'http://nl.ijs.si/ME/V4/', 
                 'source' : 'Multext-East (2010-05-14)', 
                 'cis-path' : marmot_file, 
                 'marmot-indexes' : 'form-index=0,tag-index=2,morph-index=3' 
        })

        with open(ana_file) as f_in, open(marmot_file, 'w') as f_out:

            try: 

                extract_and_map(f_in, f_out)
                json_list.append(data)            

                print >> sys.stderr, '%s done.' % lang

            except e:

                print >> sys.stderr, lang, e




    with open('mte.json', 'w') as f:
        json.dump(json_list, f)

def extract_and_map(f_in, f_out):

            for sentence in readSentences(f_in):
                for token in sentence:           

                    n = xml.dom.minidom.parseString(token).childNodes[0]
                    form = n.firstChild.data

                    lemma = form
                    if n.attributes.has_key('lemma'):
                        lemma = n.attributes.get('lemma').value

                    ana = n.nodeName
                    if n.attributes.has_key('ana'):
                        ana = n.attributes.get('ana').value
                    elif n.attributes.has_key('function'):
                        ana = n.attributes.get('function').value

                    if ana == 'w':
                        print >> sys.stderr, token

                    assert ana, ana
                    tag = split(ana)
                    assert tag, tag

                    postag = tag[0]

                    form = form.replace(' ', '_')
                    lemma = lemma.replace(' ', '_')

                    line = ('%s\t%s\t%s\t%s\n' % (form, lemma, postag, tag)).encode('utf-8')

                    f_out.write(line)
                f_out.write('\n')

                
if __name__ == '__main__':

    main()
