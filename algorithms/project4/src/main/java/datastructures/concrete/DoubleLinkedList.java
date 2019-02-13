package datastructures.concrete;

import datastructures.interfaces.IList;	
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Note: For more info on the expected behavior of your methods, see
 * the source code for IList.
 */
public class DoubleLinkedList<T> implements IList<T> {
    // You may not rename these fields or change their types.
    // We will be inspecting these in our private tests.
    // You also may not add any additional fields.
    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    private boolean compareGeneric(T data, T other) {
        return data == other || (data != null && data.equals(other));
    }
    
    // Adds the given item to the *end* of this IList.
    @Override
    public void add(T item) {
        Node<T> newNode = new Node<T>(back, item, null);
        if (back == null) {
            // case for when list is empty:
            front = newNode;
            back = newNode;
        } else {
            // general case
            back.next = newNode;
            back = newNode;
        }
        this.size++;
    }

    // Removes and returns the item from the *end* of this IList.
    @Override
    public T remove() {
        Node<T> removed = back;
        if (removed == null) {
            // list is empty, throw exception
            throw new EmptyContainerException();
        } else if (removed.prev != null) {
            // general case: more than one element
            removed.prev.next = null;
            back = removed.prev;
        } else {
            // one element in the list
            front = null;
            back = null;
        }
        size--;
        return removed.data;
    }

    // Returns the item located at the given index.
    @Override
    public T get(int index) {
        return getNode(index).data;
    }

    // returns a node given an index, throws out of bounds exception
    private Node<T> getNode(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        // loop through items (efficiently) and return node
        Node<T> current;
        int counter;
        if (size > 0 && index > size / 2) {
            // loop from end
            counter = size-1;
            current = back;
            while (counter > index) {
                counter--;
                current = current.prev;
            }
        } else {
            // loop from front
            counter = 0;
            current = front;
            while (counter < index) {
                current = current.next;
                counter++;
            }
        }
        return current;
    }

    // Overwrites the element located at the given index with the new item.
    @Override
    public void set(int index, T item) {
        Node<T> newNode;
        if (index == 0) {
            // replacing first element
            newNode = new Node<T>(null, item, front.next);
            if (size == 1) {
                back = newNode;
            } else if (size > 1) {
                front.next.prev = newNode;
            }
            front = newNode;
        } else if (index == size - 1) {
            // replacing last element
            newNode = new Node<T>(back.prev, item, null);
            back.prev.next = newNode;
            back = newNode;
        } else {
            Node<T> temp = getNode(index);
            // general case
            newNode = new Node<T>(temp.prev, item, temp.next);
            temp.prev.next = newNode;
            temp.next.prev = newNode;
        }
    }

    // Inserts the given item at the given index. If there already exists an element at 
    // that index, shift over that element and any subsequent elements one index higher.
    @Override
    public void insert(int index, T item) {
        Node<T> newNode = new Node<T>(item);
        if (index == 0) { // insert at the front
            if (front == null) {  // empty list, update pointers
                front = newNode;
                back = newNode;
            } else {  // non-empty list, update front
                front.prev = newNode;
                newNode.next = front;
                front = newNode;
            }
        } else if (back != null && size == index) {
            // append to list
            newNode.prev = back;
            back.next = newNode;
            back = newNode;
        } else {    
            // General case
            Node<T> current = getNode(index);
            newNode.next = current;
            newNode.prev = current.prev;
            current.prev.next = newNode;
            current.prev = newNode;
        }
        size++;
    }

    // Deletes the item at the given index. If there are any elements located 
    // at a higher index, shift them all down by one.
    @Override
    public T delete(int index) {
    	if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> temp = front;
        if (index == 0 && front != null) {
            // delete front
            front = front.next;
            if (size > 1) {
                front.prev = null;  // can throw null pointer exception
            }
        }
        if (index == size - 1) {
            temp = back;
            // delete back
            back = back.prev;
            if (size > 1) {
                back.next = null;  // can also throw null pointer exception
            }
        }
        if (0 < index && index < size - 1) {
            // general case
            temp = getNode(index);
            temp.prev.next = temp.next;
            temp.next.prev = temp.prev;
        }
        size--;
        if (temp == null) {
            throw new EmptyContainerException();
        }
        return temp.data;
    }

    // Returns the index corresponding to the first occurrence of the given 
    // item in the list. If the item does not exist in the list, return -1
    @Override
    public int indexOf(T item) {
        // same cases as get
        int index = 0;
        Node<T> current = front;
        while (current != null && !compareGeneric(current.data, item)) {
            current = current.next;
            index++;
        }
        if (current == null) {
            return -1;
        }
        return index;
    }

    // Returns the number of elements in the container.
    @Override
    public int size() {
        return size;
    }

    // Returns 'true' if this container contains the given element, and 'false' otherwise.
    @Override
    public boolean contains(T other) {
        Node<T> current = front;
        while (current != null) {
            if (compareGeneric(current.data, other)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return this.current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (this.current == null) {
                throw new NoSuchElementException();
            }
            T data = this.current.data;
            this.current = this.current.next;
            return data;
        }
    }
}
