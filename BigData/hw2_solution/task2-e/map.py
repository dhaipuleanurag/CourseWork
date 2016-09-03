#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t")
    
    value = value.split(",")
    key = key.split(",")
    medallion = key[0]
    date = key[3][:10]
        
    print "%s\t%s" %(medallion, date)