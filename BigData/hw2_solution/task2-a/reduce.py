#!/usr/bin/python

import sys

current_interval = None
current_sum = 0

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    
    interval, count = line.strip().split("\t", 1)
    
    try:
        count = int(count)
    except ValueError:
        continue
    
    if interval == current_interval:
        current_sum += count
    else:
        if current_interval:
            # output goes to STDOUT (stream data that the program writes)
            print "%s\t%d" %( current_interval, current_sum )
        current_interval = interval
        current_sum = count
print "%s\t%d" %( current_interval, current_sum )
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        