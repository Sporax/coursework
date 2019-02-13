package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * See the spec and IDictionary for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;

    // length is the total number of elements in the list (including buckets)
    private int length;
    private int capacity;

    public ChainedHashDictionary() {
        this.length = 0;
        this.capacity = 11;
        this.chains = this.makeArrayOfChains(this.capacity);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        // Note: You do not need to modify this method.
        // See ArrayDictionary's makeArrayOfPairs(...) method for
        // more background on why we need this method.
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int hashCode = this.hash(key);
        
        // no bucket so element doesn't exist
        if (this.chains[hashCode] == null) {
         	throw new NoSuchKeyException();
        }
        
        return this.chains[hashCode].get(key);
    }

    @Override
    public void put(K key, V value) {
        // lambda >= 1, resize
        if (this.length / this.capacity >= 1) {
        	resize();
        }
        
        // get hash for this key
        int hashCode = hash(key);
        
        // create a new bucket if there isn't one
        if (this.chains[hashCode] == null) {
        	this.chains[hashCode] = new ArrayDictionary<K, V>();
        }
        
        // only update size if an element wasn't replaced
        int size = this.chains[hashCode].size();
        this.chains[hashCode].put(key, value);
        
        if (this.chains[hashCode].size() != size) {
        	length++;
        }
    }

    @Override
    public V remove(K key) {
    	int hashCode = hash(key);
    	if (this.chains[hashCode] == null) {
    		throw new NoSuchKeyException();
    	}
    	this.length--;
    	return this.chains[hashCode].remove(key);
    }

    @Override
    public boolean containsKey(K key) {
    	int hashCode = hash(key);
    	// deal with empty bucket case
    	if (this.chains[hashCode] == null) {
    		return false;
    	}
	    return this.chains[hashCode].containsKey(key);
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains, this.length);
    }
    
    private int hash(K key) {
    	int hash;
    	// Hashes to 0 if the key is null
    	if (key == null) {
    		return 0;
    	}
    	hash = key.hashCode() % this.capacity;
        
        // mod doesn't take care of negative values
        if (hash < 0) {
        	hash = hash * -1;
        }
        return hash;
    }
    
    private void resize() {
        // use built-in methods to compute a next probable prime number (efficiently)
        BigInteger b = BigInteger.valueOf(this.capacity * 2);
        this.capacity = b.nextProbablePrime().intValue();
        // this.capacity = nextPrime(this.capacity);
    	
        IDictionary<K, V>[] newChains = this.makeArrayOfChains(this.capacity);
        for (int i = 0; i < this.chains.length; i++) {
        	if (this.chains[i] != null) {
        	    for (KVPair<K, V> pair : this.chains[i]) {
        	        K key = pair.getKey();
                    V value = pair.getValue();
        	        int hashCode = this.hash(key);
                    if (newChains[hashCode] == null) {
                    	newChains[hashCode] = new ArrayDictionary<K, V>();
                    }
                    newChains[hashCode].put(key, value);
        	    }
        	}
    	}
        this.chains = newChains;
    }

    /**
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be 
     *    true once the constructor is done setting up the class AND 
     *    must *always* be true both before and after you call any 
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int chainsIndex;
        private int remainingElements;
        private Iterator<KVPair<K, V>> chainIter;
        
        public ChainedIterator(IDictionary<K, V>[] chains, int length) {
            this.chains = chains;
            this.chainsIndex = 0;
            this.chainIter = null;
            this.remainingElements = length;
        }

        @Override
        public boolean hasNext() {
            return this.remainingElements > 0;
        }

        @Override
        public KVPair<K, V> next() {
            while (chainsIndex < chains.length) {
            	if (chains[chainsIndex] == null) {
            		chainsIndex++;
            	} else {
            		if (chainIter == null) {
            			chainIter = chains[chainsIndex].iterator();
            		} else {
            			if (this.chainIter.hasNext()) {
            				this.remainingElements--;
            				return this.chainIter.next();
            			} else {
            				chainsIndex++;
            				this.chainIter = null;
            			}
            		}
            	}
            }
            throw new NoSuchElementException();
        }
    }
}
