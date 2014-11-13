#! /usr/bin/env python

import re
import sys

TAG_PATTERN = re.compile('^[\w-][0-9]+[\w-]$')

def join(tag_string):
    features = ""
    for tag in tag_string.split('|'):
        if TAG_PATTERN.match(tag):
            features += tag[-1]
    return features

def main(args):

    if len(args) != 2:
        print >> sys.stderr, 'Usage: remap_mte.py <MARMOT_OUTPUT_FILE> <OUTPUT_FILE>'

    marmot_output = args[0]
    output = args[1]

    with open(marmot_output) as f, open(output, 'w') as f_out:
        for line in f:
            tokens = line.split()

            if tokens:
                tokens[7] = join(tokens[7])
                f_out.write('\t'.join(tokens))
                f_out.write('\n')

            else:
                    
                f_out.write('\n')
        
if __name__ == '__main__':

    main(sys.argv[1:])
