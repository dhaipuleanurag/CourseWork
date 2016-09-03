#!/usr/bin/python

import sys

current_num_passengers = None
current_sum = 0

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    
    num_passengers, count = line.strip().split("\t", 1)
    
    try:
        count = int(count)
    except ValueError:
        continue
    
    if num_passengers == current_num_passengers:
        current_sum += count
    else:
        if current_num_passengers:
            # output goes to STDOUT (stream data that the program writes)
            print "%s\t%d" %( current_num_passengers, current_sum )
        current_num_passengers = num_passengers
        current_sum = count
print "%s\t%d" %( current_num_passengers, current_sum )
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        