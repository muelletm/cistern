#!/usr/bin/env python

import json
import sys

def main(args):

    header = ['lang', 'source-url', 'source', 'comment', 'model']
    print '\t'.join(header)

    with open(args[0]) as f:
        for lang_data in json.load(f):

            rows = []

            for key in header:

                rows.append(lang_data.get(key, ''))

            print '\t'.join(rows)


if __name__ == '__main__':

    main(sys.argv[1:])
