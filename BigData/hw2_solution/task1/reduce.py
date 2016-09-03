#!/usr/bin/python

import sys

current_key = None
current_output = ""
trips = []
fares = []

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t", 1)
    
    if key == current_key:
        if value.startswith("trips"):
            trips.append(value[5:])
        elif value.startswith("fares"):
             fares.append(value[5:])
        
    else:
         if current_key != None:
             if len(trips) > 0 and len(fares) > 0:
                for trip in trips:
                    for fare in fares:
                        print "%s\t%s,%s" %( current_key, trip, fare )
    
         current_key = key
         trips = []
         fares = []
         if value.startswith("trips"):
             trips.append(value[5:])
         elif value.startswith("fares"):
             fares.append(value[5:])
        
if len(trips) > 0 and len(fares) > 0:
    for trip in trips:
        for fare in fares:
            print "%s\t%s,%s" %( current_key, trip, fare )  

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        