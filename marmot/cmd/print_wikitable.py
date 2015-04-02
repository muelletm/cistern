#!/usr/bin/env python

import json
import sys


def writeTable(f, name, jsonfile):

    headers = ['lang', 'source', 'model']

    print >>f, '<table style="width:40%">'
    print >>f,'<tr>'
    for header in headers:
        print >>f,'<th>%s</th>' % header
    print >>f,'</tr>'

    with open(jsonfile) as f_json:

        for lang_data in json.load(f_json):
            print >>f,'<tr>'

            rows = []

            lang = lang_data.get('lang')
            source = lang_data.get('source')

            url = lang_data.get('source-url')
            assert url

            print >>f,'<td>%s</td>' % lang
            print >>f,('<td><a href="%s">%s</a></td>' % (url, source)).encode('utf-8')
            print >>f,'<td><a href="%s/%s.marmot">%s.marmot</a></td>' % (name, lang, lang)
            print >>f,'</tr>'

    print >>f,'</table>'


def main(args):

    name, jsonfile = args

    with open('/dev/stdout/') as f:
        writeTable(f, name, jsonfile)

if __name__ == '__main__':

    main(sys.argv[1:])
