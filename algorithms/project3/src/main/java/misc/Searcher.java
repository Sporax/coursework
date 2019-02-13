package misc;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

public class Searcher {
    /**
     * This method takes the input list and returns the top k elements
     * in sorted order.
     *
     * So, the first element in the output list should be the "smallest"
     * element; the last element should be the "biggest".
     *
     * If the input list contains fewer then 'k' elements, return
     * a list containing all input.length elements in sorted order.
     *
     * This method must not modify the input list.
     *
     * @throws IllegalArgumentException  if k < 0
     */
    public static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
        // Implementation notes:
        //
        // - This static method is a _generic method_. A generic method is similar to
        //   the generic methods we covered in class, except that the generic parameter
        //   is used only within this method.
        
        if (k < 0) {
            throw new IllegalArgumentException();
        } else if (k > input.size()) {
            // if k is more than the size, we don't want to throw an exception
            k = input.size();
        }
        
        IList<T> output = new DoubleLinkedList<T>();
        IPriorityQueue<T> sortHeap = new ArrayHeap<>();
        if (k == 0) {
            return output;
        }

        for (T item : input) {
            if (sortHeap.size() < k) {
                // we want to keep our heap full
                sortHeap.insert(item);
            } else if (item.compareTo(sortHeap.peekMin()) > 0) {
                // item is 'greater than' all items in the heap
                // 'dequeue' all items and 'enqueue' the new item
                sortHeap.insert(item);
                sortHeap.removeMin();
                // adding first will append to the list (i.e. k+1 items now)
                // then we replace the first with the new one and percolate down (so k items left)
            }
        }
        
        for (int i = 0; i < k; i++) {
            output.add(sortHeap.removeMin());
        }
        return output;
    }
}
