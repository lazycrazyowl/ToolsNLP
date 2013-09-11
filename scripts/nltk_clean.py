#!/usr/bin/env python
import sys
from nltk.util import clean_html

if (len(sys.argv)) !=3:
  sys.stderr.write('Usage <input file> <output file>!\n')
  sys.exit(1)

html = ''

inpFile = open(sys.argv[1], 'r');
outFile = open(sys.argv[2], 'w');

for line in inpFile.readlines():
  html = html + line

outFile.write(clean_html(html) + '\n')
