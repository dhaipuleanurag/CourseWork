#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t")
    
    value = value.split(",")
    key = key.split(",")
    medallion = key[0]
    hack_license = key[1]
            
    print "%s\t%s" %(hack_license, medallion)