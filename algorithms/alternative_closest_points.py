'''
Author: Saagar Saini
Run using Python3
Alternative implementation of divide and conquer, designed to be faster than O(n(logn)**2)
'''

import re, sys
from datetime import datetime

MIN_POINTS = [[0.0, 0.0], [0.0, 0.0]]

class Point():
    def __init__(self, x, y):
        self.x = float(x)
        self.y = float(y)

    def __str__(self):
        return str(self.x) + ' ' + str(self.y)

def distance(point1, point2):
    return (point2.y - point1.y) ** 2 + (point2.x - point1.x) ** 2

def merge(list1, list2):
    '''Merge two sorted lists'''
    result = list()
    i = 0
    j = 0
    while i < len(list1) and j < len(list2):
        if list1[i].y < list2[j].y:
            result.append(list1[i])
            i += 1
        else:
            result.append(list2[j])
            j += 1
    while i < len(list1):
        result.append(list1[i])
        i += 1
    while j < len(list2):
        result.append(list2[j])
        j += 1
    return result

def closest_pair(p_x):
    '''Assume p_x is sorted by x-values, implicitly sort p_y using
       a merge sort along with divide-and conquer'''
    n = len(p_x)
    if n <= 1:
        # base case
        return [float("inf"), p_x]

    # construct new lists dividing points
    q_x = list()
    r_x = list()
    for i in range(n//2):
        q_x.append(p_x[i])
    for j in range(n//2, n):
        r_x.append(p_x[j])

    # recurse
    d1, q_y = closest_pair(q_x)
    d2, r_y = closest_pair(r_x)
    d = min(d1,d2)
    L = q_x[-1].x if len(q_x) > 0 else 0

    # merge two y lists
    p_y = merge(q_y, r_y)
    # filter points farther than L - d <= p <= L + d
    S = [p for p in p_y if abs(p.x) <= L + d]
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
    return [d, p_y]

def main(filename):
    f = open(filename)
    contents = f.read()
    f.close()
    start_time = datetime.now()
    points_list = re.findall(r'\S+', contents)
    assert len(points_list) % 2 == 0
    it = iter(points_list)
    points_list = [Point(x,y) for x,y in zip(it,it)]
    p_x = sorted(points_list, key=lambda p: p.x)
    d,p = closest_pair(p_x)
    end_time = datetime.now()
    time_taken = end_time - start_time
    print('Version 3')
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
