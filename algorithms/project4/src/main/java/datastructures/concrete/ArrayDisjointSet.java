package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IList;

/**
 * See IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private int[] pointers;
    private IDictionary<T, Integer> nodeInventory;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[10];
        nodeInventory = new ChainedHashDictionary<>();
    }

    @Override
    public void makeSet(T item) {
        if (nodeInventory.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        
        int size = nodeInventory.size();
        // if pointers is full, increase the size
        if (pointers.length == size) {
            int[] newPointers = new int[pointers.length * 2];
            for (int i = 0; i < pointers.length; i++) {
                newPointers[i] = pointers[i];
            }
            pointers = newPointers;
        }
        
        pointers[size] = -1;
        nodeInventory.put(item, size);
    }

    @Override
    public int findSet(T item) {
        // Error if it is not in a set
        if (!nodeInventory.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        
        IList<Integer> children = new DoubleLinkedList<>();
        int currentIndex = nodeInventory.get(item);
        while (pointers[currentIndex] >= 0) {
            children.add(currentIndex);
            currentIndex = pointers[currentIndex];
        }
        
        // Compress so that all children of this parent point directly to it
        for (int child : children) {
            pointers[child] = currentIndex;
        }
        
        return currentIndex;
    }

    @Override
    public void union(T item1, T item2) {
        // if the rank is the same, break arbitrarily
        int parent1 = findSet(item1);
        int parent2 = findSet(item2);
        
        // same case
        if (parent1 == parent2) {
            throw new IllegalArgumentException();
        }
        
        int rank1 = pointers[parent1];
        int rank2 = pointers[parent2];
        
        // add to whichever has greater rank
        if (rank1 <= rank2) {
            pointers[parent2] = parent1;
            // update the rank of parent1
            if (rank1 == rank2) {
                pointers[parent1]--;
            }
        } else {
            pointers[parent1] = parent2;
        }
    }
}
