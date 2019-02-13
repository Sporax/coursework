'''
Author: Saagar Saini
Run using Python3
Naive search to find the closest pair of points
'''

import re, sys

from datetime import datetime

class Point():
    def __init__(self, x, y):
        self.x = float(x)
        self.y = float(y)

    def __str__(self):
        return 'x: %s, y: %s' % (self.x,self.y)

def distance(point1, point2):
    return (point2.y - point1.y) ** 2 + (point2.x - point1.x) ** 2

def compute_point_distances(point_list):
    dist = float('inf')
    p1 = None
    p2 = None
    for i in range(len(point_list)):
        for j in range(i+1,len(point_list)):
            d = distance(point_list[i], point_list[j])
            if d < dist:
                dist = d
                p1 = point_list[i]
                p2 = point_list[j]
    return (dist,p1,p2)

def main(filename):
    f = open(filename)
    contents = f.read()
    f.close()
    start_time = datetime.now()
    points_list = re.findall(r'\S+', contents)
    assert len(points_list) % 2 == 0
    it = iter(points_list)
    points_list = [Point(x,y) for x,y in zip(it,it)]
    d,p1,p2 = compute_point_distances(points_list)
    end_time = datetime.now()
    time_taken = end_time - start_time
    print('Version 1')
    print('Size of input n: ', len(points_list))
    if d == float('inf'):
        # size of input is 1, so no pair of points is successfully found
        print('Only one point provided in the input, so no pair found')
    else:
        coords = [[p1.x, p1.y], [p2.x, p2.y]]
        print('Coordinates of closest points: ', coords)
        print('Distance: ', d)
    print('Time taken: %s (Hours:Minutes:Seconds.milliseconds)' % time_taken)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python3 program_name input_file_name")
    else:
        filename = sys.argv[1]
        main(filename)
