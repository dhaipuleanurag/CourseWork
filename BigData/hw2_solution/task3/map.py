#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    line = line.strip()
    if "\t" in line:
        key_tripfare, value = line.strip().split("\t", 1)
        value = value.split(",")
        key_tripfare = key_tripfare.split(",")
        value = key_tripfare[1:] + value
        value = ",".join(value)
        print '%s\t%s' % (key_tripfare[0], 'tripfare'+value)
    else:
        line = line.split(",")
        if ('medallion' not in line) and ('DMV_license_plate' not in line):
            value = ",".join(line[1:])
            print '%s\t%s' % (line[0], 'vehicles'+value)
