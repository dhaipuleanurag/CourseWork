#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t")
    
    value = value.split(",")
    total_amount = value[16]
    
    try:
        total_amount = float(total_amount)
    except ValueError:
        continue    
    
    if total_amount <= 10:        
        print 1