'''
Author: Saagar Saini
Run using Python3
Uses an O(n(logn)**2) algorithm to find the shortest pair of points with divide and conquer
'''

import re, sys
from datetime import datetime

MIN_POINTS = [[0.0, 0.0], [0.0, 0.0]]

class Point():
    def __init__(self, x, y):
        self.x = float(x)
        self.y = float(y)

def distance(point1, point2):
    return (point2.y - point1.y) ** 2 + (point2.x - point1.x) ** 2

def closest_pair(points):
    '''Assume points is sorted by x-values'''
    n = len(points)
    if n <= 1:
        # base case
        return float("inf")

    # construct new lists dividing points
    left_points = []
    right_points = []
    for i in range(n//2):
        left_points.append(points[i])
    for j in range(n//2, n):
        right_points.append(points[j])

    # recurse
    d = min(closest_pair(left_points), closest_pair(right_points))
    L = left_points[-1].x if len(left_points) > 0 else 0

    # delete points further than \del from L
    S = [p for p in points if abs(p.x) <= L + d]

    # sort list by y values
    S = sorted(S, key=lambda p: p.y)
    m = len(S) - 1
    for i in range(len(S)):
        k = 1
        while i+k <= m and S[i+k].y < S[i].y + d:
            dist = distance(S[i], S[i+k])
            if dist < d:
                d = dist
                global MIN_POINTS
                MIN_POINTS = [[S[i].x, S[i].y], [S[i+k].x, S[i+k].y]]
            k += 1
    return d

def main(filename):
    f = open(filename)
    contents = f.read()
    f.close()
    start_time = datetime.now()
    points_list = re.findall(r'\S+', contents)
    assert len(points_list) % 2 == 0
    it = iter(points_list)
    points_list = [Point(x,y) for x,y in zip(it,it)]
    d = closest_pair(sorted(points_list, key=lambda p: p.x))
    end_time = datetime.now()
    time_taken = end_time - start_time
    print('Version 2')
    print('Size of input n: ', len(points_list))
    if d == float('inf'):
        # size of input is 1, so no pair of points is successfully found
        print('Only one point provided in the input, so no pair found')
    else:
        print('Coordinates of closest points: ', MIN_POINTS)
        print('Distance: ', d)
    print('Time taken: %s (Hours:Minutes:Seconds.milliseconds)' % time_taken)

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print("Usage: python3 program_name input_file_name")
    else:
        filename = sys.argv[1]
        main(filename)

