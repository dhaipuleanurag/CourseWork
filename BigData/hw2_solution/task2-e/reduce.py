#!/usr/bin/python

import sys

current_num_days = None
current_medallion = None
current_date = None
current_num_trips = 0
current_num_days = 0

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    
    medallion, date = line.strip().split("\t", 1)
    
    if medallion == current_medallion and date == current_date:
        current_num_trips += 1
    elif medallion == current_medallion and date != current_date:
        current_num_trips += 1
        current_num_days += 1
    else:
        if current_medallion != None:
            # output goes to STDOUT (stream data that the program writes)
            print "%s\t%d,%s" %( current_medallion, current_num_trips, '{0:.2f}'.format(1.*current_num_trips/current_num_days))
        current_date = date
        current_medallion = medallion
        current_num_trips = 1
        current_num_days = 1
print "%s\t%d,%s" %( current_medallion, current_num_trips, '{0:.2f}'.format(1.*current_num_trips/current_num_days))
        
        

        
        
        
        
        
        
        
        
        
        
        
