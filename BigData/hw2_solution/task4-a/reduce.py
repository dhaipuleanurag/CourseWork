#!/usr/bin/python

import sys

current_v_type = None
count = 0
current_revenue = 0
current_tips_percent = 0

#input comes from STDIN (stream data that goes to the program)
for line in sys.stdin:
    
    v_type, revenue, tips = line.strip().split("\t", 2)
    
    try:
        revenue = float(revenue)
        tips_percent = float(tips)
    except ValueError:
        continue
    
    if v_type == current_v_type:
        current_revenue += revenue
        current_tips_percent += tips_percent
        count += 1
    else:
        if current_v_type:
            if current_revenue == 0:
                current_tips_percent = 0
            av_tips_percent = 0
            if count != 0:
                av_tips_percent = current_tips_percent/count*100
            # output goes to STDOUT (stream data that the program writes)
            print "%s\t%s,%s,%s" %(current_v_type, count, '{0:.2f}'.format(current_revenue), '{0:.2f}'.format(av_tips_percent))
        current_v_type = v_type
        current_revenue = revenue
        current_tips_percent = tips_percent
        count = 1
if current_v_type:
            if current_revenue == 0:
                current_tips_percent = 0
            av_tips_percent = 0
            if count != 0:
                av_tips_percent = current_tips_percent/count*100
print "%s\t%s,%s,%s" %(current_v_type, count, '{0:.2f}'.format(current_revenue), '{0:.2f}'.format(av_tips_percent))
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        