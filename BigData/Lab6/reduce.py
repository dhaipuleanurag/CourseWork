#!/usr/bin/python

import sys

list_professors = []
list_courses = []

# input comes from STDIN
for line in sys.stdin:
    key, string = line.strip().split("\t", 1)
    key, num_name  = string.strip().split(",", 1)
    if int(key) == 1:
        list_professors.append(num_name)    
    else:
        list_courses.append(num_name)
        
for prof in list_professors:
    for course in list_courses:
        # output goes to STDOUT
        print "%s,%s" %( prof, course )
      