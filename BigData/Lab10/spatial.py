import timeit
import pandas as pd
from rtree import index

class Point:
	def __init__(self,x,y):
		self.x = x
		self.y = y

class Polygon:
	def __init__(self,points):
		self.points = points
		self.nvert = len(points)

		minx = maxx = points[0].x
		miny = maxy = points[0].y
		for i in xrange(1,self.nvert):
			minx = min(minx,points[i].x)
			miny = min(miny,points[i].y)
			maxx = max(maxx,points[i].x)
			maxy = max(maxy,points[i].y)

		self.bound = (minx,miny,maxx,maxy)


	def contains(self,pt):
		firstX = self.points[0].x
		firstY = self.points[0].y
		testx = pt.x
		testy = pt.y
		c = False
		j = 0
		i = 1
		nvert = self.nvert
		while (i < nvert) :
			vi = self.points[i]
			vj = self.points[j]
			
			if(((vi.y > testy) != (vj.y > testy)) and (testx < (vj.x - vi.x) * (testy - vi.y) / (vj.y - vi.y) + vi.x)):
				c = not(c)

			if(vi.x == firstX and vi.y == firstY):
				i = i + 1
				if (i < nvert):
					vi = self.points[i];
					firstX = vi.x;
					firstY = vi.y;
			j = i
			i = i + 1
		return c

	def bounds(self):
		return self.bound

#Define the polygons for JFK and LGA
def create_polygon():
	data1=open('Lab10/neighborhoods.txt')
	list2=[]
	list1=[]
	for line in data1:
		line=line.strip()
		if 'JFK' in line:
			list1=[]
			continue
		if 'LGA' in line:
			list2.append(list1)
			list1=[]
			continue
		if ',' not in line:
			continue
		list1.append(Point(float(line.split(',')[1]),float(line.split(',')[0])))
	list2.append(list1)
	return Polygon(list2[1]), Polygon(list2[0])

#Creating the index for the taxi data
def create_idx():
	actual_idx = index.Index()
	
	sample=pd.read_table('Lab10/taxigreen(06-15)_table.csv',sep=',')
	sample=sample[1:]
	for i in xrange(len(sample)):
		lat=sample.Dropoff_latitude[i+1]
		lat=float(lat)
		lon=sample.Dropoff_longitude[i+1]
		lon=float(lon)
		p_point = Point(lat,lon)
		actual_idx.insert(i,(p_point.x, p_point.y, p_point.x, p_point.y))
		
	return actual_idx	

#Simple test for a point in Polygon without using Rtree
#(In the below method, for time calculation purposes code to calculate time for one airport was commented for another)
def simplePolygonTest():
	print("Point in polygon test")
	# Create a simple polygon
	poly_jfk, poly_lga = create_polygon()
	
	start_time = timeit.default_timer()
	cnt_jfk=0
	cnt_lga=0
	sample=pd.read_table('Lab10/taxigreen(06-15)_table.csv',sep=',')
	sample=sample[1:]
	for i in range(len(sample)):
		lat=sample.Dropoff_latitude[i+1]
		lat=float(lat)
		lon=sample.Dropoff_longitude[i+1]
		lon=float(lon)
		if poly_lga.contains(Point(lat,lon)):
			cnt_lga=cnt_lga+1
		#if poly_jfk.contains(Point(lat,lon)):
			#cnt_jfk=cnt_jfk+1
	elapsed = timeit.default_timer() - start_time
	print(elapsed)
	#print(cnt_jfk)
	print(cnt_lga)
	

#Method to check if a point is in polygon or not using Rtree
def simpleRTree():
	j = 0
	points_in_poly = []
	actual_idx = create_idx()
	poly_jfk, poly_lga = create_polygon()

	start_time = timeit.default_timer()
	
	#intersect = list(actual_idx.intersection(poly_jfk.bounds()))
	intersect = list(actual_idx.intersection(poly_lga.bounds()))

	intersect.sort()

	trips = open('taxigreen(06-15)_table.csv', 'r')
	for i, t in enumerate(trips):
		if intersect and j == len(intersect):
			break
		elif intersect and i == intersect[j]:
			splitted = t.replace('\n', '').replace('\r', '').split(',')
			d_lat = float(splitted[2])
			d_long = float(splitted[3])
			trip_do = Point(d_lat, d_long)
			#if poly_jfk.contains(trip_do):
				#points_in_poly.append(i)
			if poly_lga.contains(trip_do):
				points_in_poly.append(i)	
			j += 1		
				
	elapsed = timeit.default_timer() - start_time
	print(elapsed)
	
	print(len(points_in_poly))

simplePolygonTest()
simpleRTree()

