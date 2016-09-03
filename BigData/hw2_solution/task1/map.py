#!/usr/bin/python

import sys

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    line = line.strip()
    result = line.split(",")
    if 'medallion' not in result:
        if len(result) == 11:
            key = ",".join(result[0:4])
            value = ",".join(result[4:])
            print '%s\t%s' % (key, 'fares'+value)
        elif len(result) == 14:
            key = result[0:3]
            key.append(result[5])
            key = ",".join(key)
            value = result[6:]
            value.insert(0, result[4])
            value.insert(0, result[3])
            value = ",".join(value)
            print '%s\t%s' % (key, 'trips'+value)

