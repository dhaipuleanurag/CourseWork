#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t")
    
    value = value.split(",")
    key = key.split(",")
    date = key[3][:10]
    revenue = float(value[11]) + float(value[12]) + float(value[14])
    tolls = float(value[15])
        
    print "%s\t%f\t%f" %(date, revenue, tolls)