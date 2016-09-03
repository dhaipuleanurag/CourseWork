#!/usr/bin/python

import sys

current_lic = None
current_medallion = None
current_sum = 0

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    
    lic, medallion = line.strip().split("\t", 1)
    
    if lic == current_lic and medallion != current_medallion:
        current_sum += 1
    elif not (lic == current_lic and medallion == current_medallion):
        if current_lic != None:
            # output goes to STDOUT (stream data that the program writes)
            print "%s\t%d" %(current_lic, current_sum)
        current_sum = 1
        current_medallion = medallion
        current_lic = lic
print "%s\t%d" %(current_lic, current_sum)
        
        

        
        
        
        
        
        
        
        
        
        
        