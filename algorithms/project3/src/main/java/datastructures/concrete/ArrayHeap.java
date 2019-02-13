package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * See IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    private static final int NUM_CHILDREN = 4;
    private int length;
    private int capacity;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        capacity = 10;
        length = 0;
        heap = makeArrayOfT(capacity);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int size) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[size]);
    }

    @Override
    public T removeMin() {
        if (length == 0) {
            throw new EmptyContainerException();
        }
        T min = heap[0];
        heap[0] = heap[length-1];
        heap[length-1] = null;
        length--;
        percolateDown(0);
        return min;
    }

    @Override
    public T peekMin() {
        if (length == 0) {
            throw new EmptyContainerException();
        }
        return heap[0];
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        // add element to the end if array is small enough
        if (length == capacity) {
            // expand array
            capacity *= 2;
            T[] newHeap = makeArrayOfT(capacity);
            for (int i = 0; i < length; i++) {
                newHeap[i] = heap[i];
            }
            heap = newHeap;
        }
        heap[length] = item;
        length++;
        percolateUp(length-1);
    }
    
    private void percolateDown(int index) {
        // idea: start from the top and if this is the largest, it should be at the bottom at the end
        // swap current with smallest child until done
        
        // start from the top and work our way down
        int childLevel = NUM_CHILDREN * index + 1;
        if (childLevel < length) {
            int smallestChild = childLevel;
            for (int n = 0; n < NUM_CHILDREN; n++) {
                int childIndex = childLevel + n;
                if (childIndex < length && heap[smallestChild].compareTo(heap[childIndex]) > 0) {
                    smallestChild = childIndex;
                }
            }
            // now we have the smallest child, swap it with root and recurse4, 5
            if (heap[smallestChild].compareTo(heap[index]) < 0) {
                swap(smallestChild, index);
                percolateDown(smallestChild);
            }
        }
    }

    private void percolateUp(int index) {
        // start from last tier and work our way back until sorted
        if (index > 0) {
            // swap with parent if parent is less than current
            int parentIndex = (index - 1) / NUM_CHILDREN;
            if (heap[index].compareTo(heap[parentIndex]) < 0) {
                // this is less than the parent so swap them
                swap(parentIndex, index);
                // recurse all the way up
                percolateUp(parentIndex);
            }
        }
    }
    
    private void swap(int firstIndex, int secondIndex) {
        T temp = heap[firstIndex];
        heap[firstIndex] = heap[secondIndex];
        heap[secondIndex] = temp;
    }
    
    @Override
    public int size() {
        return this.length;
    }
}
