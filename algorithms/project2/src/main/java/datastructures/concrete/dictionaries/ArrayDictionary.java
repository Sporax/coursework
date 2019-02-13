package datastructures.concrete.dictionaries;

import java.util.Iterator;
import java.util.NoSuchElementException;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

/**
 * See IDictionary for more details on what this class should do
 */
public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private Pair<K, V>[] pairs;
    
    // You're encouraged to add extra fields (and helper methods) though!
    private int size;     // size is equivalent to length
    private int capacity; // capacity is array size
    
    public ArrayDictionary() {
        this.size = 0;
        this.capacity = 10;
        this.pairs = makeArrayOfPairs(capacity);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain Pair<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        // It turns out that creating arrays of generic objects in Java
        // is complicated due to something known as 'type erasure'.
        //
        // We've given you this helper method to help simplify this part of
        // your assignment. Use this helper method as appropriate when
        // implementing the rest of this class.
        //
        // You are not required to understand how this method works, what
        // type erasure is, or how arrays and generics interact. Do not
        // modify this method in any way.
        return (Pair<K, V>[]) (new Pair[arraySize]);

    }

    private boolean compareGeneric(K data, K other) {
        return data == other || (data != null && data.equals(other));
    }
    
    // Returns the value corresponding to the given key.
    @Override
    public V get(K key) {
        for (int i = 0; i < size; i++) {
            if (compareGeneric(pairs[i].key, key)) {
                return pairs[i].value;
            }
        }
        throw new NoSuchKeyException();
    }

    /**
     * Adds the key-value pair to the dictionary. If the key already exists in the dictionary,
     * replace its value with the given one.
     */
    @Override
    public void put(K key, V value) {
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (compareGeneric(pairs[i].key, key)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            // replace key-value pair with new key-value pair
            pairs[index].value = value;
        } else {
            if (size == capacity) {
                // increase capacity, copy over old pairs to new pair array
                capacity = capacity * 2;
                Pair<K, V>[] newPairs = makeArrayOfPairs(capacity);
                for (int i = 0; i < size; i++) {
                    newPairs[i] = pairs[i];
                }
                pairs = newPairs;
            }
            // add a pair to array[size]
            pairs[size] = new Pair<K, V>(key, value);
            size++;
        }
    }

    /**
     * Remove the key-value pair corresponding to the given key from the dictionary.
     *
     * @throws NoSuchKeyException if the dictionary does not contain the given key.
     */
    @Override
    public V remove(K key) {
        for (int i = 0; i < size; i++) {
            if (compareGeneric(pairs[i].key, key)) {
                // replace this pair with the last pair
                V returnValue = pairs[i].value;
                pairs[i] = pairs[size-1];
                pairs[size-1] = null;
                size--;
                return returnValue;
            }
        }
        throw new NoSuchKeyException();
    }

    /**
     * Returns 'true' if the dictionary contains the given key and 'false' otherwise.
     */
    @Override
    public boolean containsKey(K key) {
        for (int i = 0; i < size; i++) {
            if (compareGeneric(pairs[i].key, key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the number of key-value pairs stored in this dictionary.
     */
    @Override
    public int size() {
        return this.size;
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<>(this);
    }
    
    // create new static class to implement iteration
    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {
        private int index;
        private int size;
        private Pair<K, V>[] pairs;
        
        public ArrayDictionaryIterator(ArrayDictionary<K, V> dict) {
            this.index = 0;
            this.size = dict.size;
            this.pairs = dict.pairs;
        }
        
        public boolean hasNext() {
            return index < size;
        }
        
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            K key = pairs[index].key;
            V value = pairs[index].value;
            this.index++;
            return new KVPair<K, V>(key, value);
        }
    }
}
