#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t")
    
    value = value.split(",")
    num_passengers = value[3]
    
    try:
        num_passengers = int(num_passengers)
    except ValueError:
        continue    
    
    print "%s\t%d" %(num_passengers, 1)