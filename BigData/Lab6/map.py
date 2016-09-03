#!/usr/bin/python

import sys

# input comes from STDIN 
for line in sys.stdin:
    l = line.strip()
    # output goes to STDOUT 
    print "%d\t%s" %( 1, l)