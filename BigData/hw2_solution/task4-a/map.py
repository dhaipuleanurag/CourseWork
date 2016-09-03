#!/usr/bin/python

import sys
import StringIO
import csv

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    csv_file = StringIO.StringIO(line)
    csv_reader = csv.reader(csv_file)
    for record in csv_reader:
        v_type = record[25]
        revenue = float(record[14]) + float(record[15]) + float(record[17])
        tips = float(record[17])
        average = 0
        if revenue == 0:
            average = 0
	else:
	    average = 1.0*tips/revenue
        print '%s\t%s\t%s' % (v_type, revenue, average)
