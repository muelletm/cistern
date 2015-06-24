#!/usr/bin/env python

import json
import sys

def main(args):

    infile, outfile = args

    with open(infile) as f:
        affixes = json.load(f)

    with open(outfile, 'w') as f:
        for affix in affixes:
            assert affix.startswith('-') or affix.endswith('-')

            if affix.startswith('-'):
                affix = affix[1:]
            else:
                affix = affix[:-1]

            f.write(affix.encode('utf-8'))
            f.write('\n')

if __name__ == '__main__':

    main(sys.argv[1:])
