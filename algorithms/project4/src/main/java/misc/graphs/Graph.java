package misc.graphs;

import datastructures.concrete.ArrayDisjointSet;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.DoubleLinkedList;
import datastructures.concrete.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import datastructures.interfaces.ISet;
import misc.Searcher;
import misc.exceptions.NoPathExistsException;
/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends Edge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //import datastructures.interfaces.IPriorityQueue;

    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated then usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've contrained Graph
    //   so that E *must* always be an instance of Edge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the Edge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    // store three datastructures
    private IDictionary<V, ISet<E>> adjacencyList;
    private IList<E> edgeList;
    
    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException  if any of the edges have a negative weight
     * @throws IllegalArgumentException  if one of the edges connects to a vertex not
     *                                   present in the 'vertices' list
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.adjacencyList = new ChainedHashDictionary<>();

        // process each vertex and neighbours
        for (V vertex : vertices) {
            adjacencyList.put(vertex, new ChainedHashSet<>());
        }

        // create new list of edges
        edgeList = new DoubleLinkedList<>();
        
        // process each edge and add connections
        for (E edge : edges) {
            // negative edge weight breaks code
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();

            // second illegal exception
            if (!adjacencyList.containsKey(vertex1) || !adjacencyList.containsKey(vertex2)) {
                throw new IllegalArgumentException();
            }
            
            // add edges for both vertices
            adjacencyList.get(vertex1).add(edge);
            adjacencyList.get(vertex2).add(edge);
            
            // add edge to list of edges
            edgeList.add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.adjacencyList.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edgeList.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        // use Kruskal and topK to get a spanning tree
        
        // start by making each vertex into a disjoint set
        IDisjointSet<V> verticesSet = new ArrayDisjointSet<>();
        for (KVPair<V, ISet<E>> pair : adjacencyList) {
            verticesSet.makeSet(pair.getKey());
        }
        
        // sort all edges using topK -- this preserves internal data structures since a copy is made
        IList<E> sortedEdges = Searcher.topKSort(edgeList.size(), edgeList);
        ISet<E> result = new ChainedHashSet<>();
        
        int verticesCount = numVertices();
        for (E edge : sortedEdges) {
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();
            // add if they are different disjoint sets
            if (verticesSet.findSet(vertex1) != verticesSet.findSet(vertex2)) {
                result.add(edge);
                verticesSet.union(vertex1, vertex2);
            }
            // we only need to have processed V-1 edges to get a minimum spanning tree
            if (result.size() == verticesCount - 1) {
                break;
            }
        }
        return result;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        if (start == end) {
            return new DoubleLinkedList<>();
        }
        
        // mapping of vertex to weighted vertex
        IDictionary<V, WeightedVertex<V, E>> vertices = new ChainedHashDictionary<>();
        // keep a heap of unprocessed weighted vertices
        IPriorityQueue<WeightedVertex<V, E>> unprocessed = new ArrayHeap<>();
        // keep track of processed vertices
        ISet<V> processed = new ChainedHashSet<>();
        
        // initialize all weighted vertices
        for (KVPair<V, ISet<E>> pair : adjacencyList) {
            V vertex = pair.getKey();
            
            // give all vertices except start a weight of infinity
            double weight = vertex == start ? 0 : Double.POSITIVE_INFINITY;
            WeightedVertex<V, E> weightedVertex = new WeightedVertex<>(vertex, weight);
            
            // add to mapping
            vertices.put(vertex, weightedVertex);
        }
        // start by adding start to the unprocessed vertices
        unprocessed.insert(vertices.get(start));
        
        // get vertex with smallest distance
        WeightedVertex<V, E> closest = unprocessed.peekMin();
        while (!unprocessed.isEmpty() && closest.vertex != end) {
            closest = unprocessed.removeMin();
            // take care of duplicate vertices
            while (!unprocessed.isEmpty() && processed.contains(closest.vertex)) {
                closest = unprocessed.removeMin();
            }
            if (unprocessed.isEmpty() && processed.contains(closest.vertex)) {
                break;
            }

            // if not processed yet
            if (!processed.contains(closest.vertex)) {
                // Check each edge leaving vertex
                for (E edge : adjacencyList.get(closest.vertex)) {
                    WeightedVertex<V, E> other = vertices.get(edge.getOtherVertex(closest.vertex));
                    
                    // If the distance is shorter from current vertex, update distance
                    // and predecessor and add the end vertex to the queue with new values
                    double distance = closest.distance + edge.getWeight();
                    if (distance < other.distance) {
                        other.distance = distance;
                        other.predecessor = edge;
                        
                        // "instead of trying to update costs, you should add duplicate 
                        //  elements and account for this separately"
                        unprocessed.insert(other);
                    }
                }
                processed.add(closest.vertex);
            }
        }
        
        // Error if never got to end
        if (vertices.get(end).predecessor == null) {
            throw new NoPathExistsException();
        }
        
        // Makes list of edges in shortest path
        IList<E> shortestPath = new DoubleLinkedList<>();
        WeightedVertex<V, E> currentVertex = closest;
        while (currentVertex.vertex != start) {
            E edge = currentVertex.predecessor;
            shortestPath.insert(0, edge);
            currentVertex = vertices.get(edge.getOtherVertex(currentVertex.vertex));
        }
        
        return shortestPath;
    }
    
    private static class WeightedVertex<V, E> implements Comparable<WeightedVertex<V, E>> {
        // Note that predecessor is the edge that leads to the prior vertex
        // not the vertex itself. This makes it easier to build the set later
        
        // fields are public inside private class (this is OK)
        public V vertex;
        public E predecessor;
        public Double distance;
        
        public WeightedVertex(V vertex, double distance) {
            this(vertex, distance, null);
        }
        
        public WeightedVertex(V vertex, double distance, E parent) {
            this.vertex = vertex;
            this.distance = distance;
            this.predecessor = parent;
        } 
        
        @Override
        public int compareTo(WeightedVertex<V, E> other) {
            return (int) (this.distance - other.distance);
        }
    }
}
