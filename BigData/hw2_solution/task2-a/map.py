#!/usr/bin/python

import sys

ranges = [[0,4], [4.01,8], [8.01,12], [12.01, 16], [16.01, 20], [20.01, 24], [24.01, 28], [28.01, 32], [32.01, 36], [36.01, 40], [40.01, 44], [44.01, 48], [48.01, 'infinite']]

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t")
    
    value = value.split(",")
    fare = value[11]
    try:
        fare = float(fare)
    except ValueError:
        continue    
    interval = 0
    num_interavals = len(ranges)    
    
    for i in range(num_interavals - 1):
        if fare >= ranges[i][0] and fare <= ranges[i][1]:
            interval = i
    if fare >= ranges[num_interavals - 1][0]:
            interval = num_interavals - 1
    print '%s,%s\t%s' % (ranges[interval][0], ranges[interval][1], 1)