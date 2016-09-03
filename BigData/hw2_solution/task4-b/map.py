#!/usr/bin/python

import sys
import StringIO
import csv

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    csv_file = StringIO.StringIO(line)
    csv_reader = csv.reader(csv_file)
    for record in csv_reader:
        name = record[29]
        revenue = float(record[14]) + float(record[15]) + float(record[17])
        print '%s\t%s' % (name, revenue)