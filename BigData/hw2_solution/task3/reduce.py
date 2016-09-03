#!/usr/bin/python

import sys

current_key = None
current_output = ""
tripfares = []
vehicles = []

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    key, value = line.strip().split("\t", 1)
        
    if key == current_key:
        if value.startswith("tripfare"):
            tripfares.append(value[8:])
        elif value.startswith("vehicles"):
             vehicles.append(value[8:])
        
    else:
         if current_key != None:
             if len(tripfares) > 0 and len(vehicles) > 0:
                for tripfare in tripfares:
                    for vehicle in vehicles:
                        print "%s\t%s,%s" %(current_key, tripfare, vehicle)
    
         current_key = key
         tripfares = []
         vehicles= []
         if value.startswith("tripfare"):
            tripfares.append(value[8:])
         elif value.startswith("vehicles"):
             vehicles.append(value[8:])
        
if len(tripfares) > 0 and len(vehicles) > 0:
    for tripfare in tripfares:
        for vehicle in vehicles:
            print "%s\t%s,%s" %(current_key, tripfare, vehicle) 

        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        