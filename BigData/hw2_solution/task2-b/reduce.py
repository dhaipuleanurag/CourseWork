#!/usr/bin/python

import sys

sum = 0

#input comes from STDIN (stream data that goes to the program)
for count in sys.stdin:
    try:
        count = int(count)
    except ValueError:
        continue
    
    sum += count
print sum
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        