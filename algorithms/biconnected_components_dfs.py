'''
Author: Saagar Saini
Run using Python3
Uses Depth-First Search to find all articulation points using a given graph and lists biconnected components in linear time.
'''

import sys, re, time

# list of vertices
vertices = list()
# dict of vertex name -> list of edges
edges = dict()

class Node():
    def __init__(self, data, dfsnum=-1):
        self.dfsnum = dfsnum
        self.low = 0
        self.data = data
        global edges
        edges[data] = list()

    def __str__(self):
        return str(self.data)

    def __hash__(self):
        return hash(str(self)) 


dfscounter = 1
def dfs(v, articulation_points, explored, subgraphs, parent=None):
    ''' Does a Depth-First Search, taking in a starting vertex and an empty
        set to be filled with vertices that are articulation points. '''
    global dfscounter
    # set values
    v.dfsnum = dfscounter
    dfscounter += 1
    v.low = v.dfsnum
    # keep track of # children for the root node
    v.children = 0
    
    # iterate through adjacent edges
    for edge in edges[v.data]:
        x = vertices[edge]
        
        # if neighbor not explored yet, explore it
        if x.dfsnum == -1:
            v.children += 1
            explored.append((v.data, x.data))
            dfs(x, articulation_points, explored, subgraphs, parent=v)
            
            # set low value
            v.low = min(v.low, x.low)

            # test for articulation points
            if (not parent and v.children > 1) or \
               (parent and x.low >= v.dfsnum):
                articulation_points.add(str(v))

                # when we find an articulation point, we also find a biconnected subgraph
                new_subgraph = list()
                pair = (-1, -1)
                # continue popping of edges until we reach the one explored to start the subgraph
                while pair != (v.data, x.data):
                    pair = explored.pop()
                    new_subgraph.append(pair)
                # now we have found a cycle and can stop
                subgraphs.append(new_subgraph)
            
        elif parent and x is not parent and x.dfsnum < v.low: # case for back-edges
            v.low = x.dfsnum
            explored.append((v.data, x.data))


def setup_graph(arg):
    ''' Take the command line string of edges and vertices and set up
        a graph representation '''
    global vertices, edges, n

    # parse string based input file into a list of (u,v) edge pairs
    # and a number of vertices
    (n, edge_list) = parse_file(arg)
    
    # create vertices
    vertices = list()
    for i in range(n):
        vertices.append(Node(i))
    
    # create an adjacency list
    for u,v in edge_list:
        edges[u].append(v)
        edges[v].append(u)


def parse_file(arg):
    ''' Take a string file and parse it into two two components,
        returning a list of edges and the number of vertices '''
    arg = arg.split('\n')[:-1]
    n = int(arg[0])
    edges = list()
    for edge in arg[1:]:
        # add every pair (x,y) formatted to the list of edges
        (u,v) = re.findall(r'\d+', edge)
        edges.append((int(u), int(v)))
    return (n, edges)


def print_results(runtime, articulation_points, biconnected_components):
    print('Number of nodes: %s' % n)
    print('Number of edges: %s' % sum(len(x) for x in edges.values()))
    print('Number of biconnected components: %s' % len(biconnected_components))
    print('Number of articulation points: %s' % len(articulation_points))
    print('Articulation points: ', articulation_points)
    print('Edges in biconnected components:')
    for edge in biconnected_components:
        print('\t', edge)
    print('Runtime: %s seconds' % runtime)


def run_dfs():
    global dfscounter
    dfscounter = 1
    articulations = set()
    explored_edges = list()
    bi_subgraphs = list()
    start_time = time.time()
    dfs(vertices[0], articulations, explored_edges, bi_subgraphs)
    
    # clear out the remaining edges
    new_subgraph = list()
    while len(explored_edges) > 0:
        pair = explored_edges.pop()
        new_subgraph.append(pair)
    bi_subgraphs.append(new_subgraph)

    time_taken = time.time() - start_time
    # print results
    print_results(time_taken, articulations, bi_subgraphs)


if __name__ == '__main__':
    ''' Take an input file to read as a command line argument and parse it '''
    if len(sys.argv) > 1:
        input_file = sys.argv[1]
        # check if the file exists
        with open(input_file, 'r') as f:
            contents = f.read()
            setup_graph(contents)
            run_dfs()
    else:
        print('Usage: python3 program_name input_file_name')

