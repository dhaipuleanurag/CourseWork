#!/usr/bin/python

import sys

current_date = None
current_revenue = 0
current_tolls = 0

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    
    date, revenue, tolls = line.strip().split("\t", 2)
    
    try:
        revenue = float(revenue)
        tolls = float(tolls)
    except ValueError:
        continue
    
    if date == current_date:
        current_revenue += revenue
        current_tolls += tolls
    else:
        if current_date:
            # output goes to STDOUT (stream data that the program writes)
            print "%s\t%s,%s" %( current_date, '{0:.2f}'.format(current_revenue), '{0:.2f}'.format(current_tolls))
        current_date = date
        current_revenue = revenue
        current_tolls = tolls
print "%s\t%s,%s" %( current_date, '{0:.2f}'.format(current_revenue), '{0:.2f}'.format(current_tolls))
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        