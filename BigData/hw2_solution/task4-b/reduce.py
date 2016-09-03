import sys
curr_key=None
lst=[]
total_revenue=0
agent_name=''
for line in sys.stdin:
	attr=line.strip().split('\t')
	agent_name=attr[0]
	try:
		revenue=float(attr[1])
	except ValueError:
		continue
	if agent_name==curr_key:
		total_revenue=total_revenue+revenue
	else:
		if curr_key:
			lst.append([curr_key,total_revenue])
		curr_key=agent_name
		total_revenue=revenue
lst.append([curr_key,total_revenue])
total=len(lst)
lst.sort(key= lambda x: -x[1])
ran = 20
if total < 20:
    ran = total
for i in range(ran):
	print"%s\t%.2f" %(lst[i][0],lst[i][1])