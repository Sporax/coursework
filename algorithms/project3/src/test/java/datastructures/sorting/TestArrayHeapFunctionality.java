package datastructures.sorting;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import misc.BaseTest;
import misc.exceptions.EmptyContainerException;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import org.junit.Test;

public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }
    @Test(timeout=SECOND)
    public void testMultipleSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 0; i < 378; i++) {
            heap.insert(i);
        }
        for (int i = 0; i < 321; i++) {
            heap.removeMin();
        }
        assertEquals(378-321, heap.size());
        assertTrue(!heap.isEmpty());
    }    
    // tests for a working queue
    @Test(timeout=SECOND)
    public void testSinglePeek() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(3, heap.peekMin());
    }
    @Test(timeout=SECOND)
    public void testSingleRemove() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(3, heap.removeMin());
        assertTrue(heap.isEmpty());
    }
    @Test(timeout=SECOND)
    public void testMultipleOrder() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        heap.insert(2);
        heap.insert(4);
        heap.insert(5);
        heap.insert(1);
        assertEquals(5, heap.size());
        assertEquals(1, heap.removeMin());
        assertEquals(2, heap.removeMin());
        assertEquals(3, heap.removeMin());
        assertEquals(4, heap.removeMin());
        assertEquals(5, heap.removeMin());
        assertTrue(heap.isEmpty());
    }
    
    // run a bunch of empty case tests
    @Test(timeout=SECOND)
    public void testRemoveEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.removeMin();
            fail("Did not throw an EmptyContainerException");
        } catch (EmptyContainerException e) {
            // this is expected
        }
    }
    @Test(timeout=SECOND)
    public void testRemoveUntilEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 23; i > 5; i -= 3) {
            heap.insert(i);
        }
        int size = heap.size();
        for (int i = 0; i < size; i++) {
            heap.removeMin();
        }
        assertTrue(heap.isEmpty());
        try {
            heap.removeMin();
            fail("Did not throw an EmptyContainerException");
        } catch (EmptyContainerException e) {
            // this is expected
        }
    }
    @Test(timeout=SECOND)
    public void testInsertNull() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        try {
            heap.insert(null);
            fail("Did not throw an IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // this is expected
        }
    }
    @Test(timeout=SECOND)
    public void testPeekEmpty() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(3, heap.removeMin());
        assertTrue(heap.isEmpty());
    }
    @Test(timeout=SECOND)
    public void testRemoveUntilEmptyThenPeek() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        for (int i = 23; i > 5; i -= 3) {
            heap.insert(i);
        }
        int size = heap.size();
        for (int i = 0; i < size; i++) {
            heap.removeMin();
        }
        try {
            heap.peekMin();
            fail("Did not throw an EmptyContainerException");
        } catch (EmptyContainerException e) {
            // this is expected
        }
    }

    // test adding about 800 elements to check if it resizes
    @Test(timeout=SECOND)
    public void testResize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertEquals(0, heap.size());
        for (int i = 0; i < 800; i++) {
            heap.insert(i);
        }
        assertEquals(800, heap.size());
    }
    
    // test size updation
    @Test(timeout=SECOND)
    public void testRemoveUpdatesSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        heap.insert(4);
        heap.insert(5);
        heap.insert(1);
        assertEquals(4, heap.size());
        heap.removeMin();
        assertEquals(3, heap.size());
    }
    @Test(timeout=SECOND)
    public void testEmptyRemoveUpdatesSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        int size = heap.size();
        try {
            heap.removeMin();
            fail("Did not throw an EmptyContainerException");
        } catch (EmptyContainerException e) {
            // do nothing
        }
        assertEquals(size, heap.size());
    }
    @Test(timeout=SECOND)
    public void testInsertUpdatesSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertEquals(0, heap.size());
        heap.insert(3);
        assertEquals(1, heap.size());
    }
    @Test(timeout=SECOND)
    public void testPeekUpdatesSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        heap.insert(4);
        heap.insert(5);
        heap.insert(1);
        assertEquals(4, heap.size());
        heap.peekMin();
        assertEquals(4, heap.size());
    }
    @Test(timeout=SECOND)
    public void testInitializingUpdatesSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertEquals(0, heap.size());
    }
    @Test(timeout=SECOND)
    public void addingManyElementsUpdatesSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        assertEquals(0, heap.size());
        int numElements = 0;
        for (int i = 123; i > 8; i -= 5) {
            heap.insert(i);
            numElements++;
        }
        assertEquals(numElements, heap.size());
        assertTrue(!heap.isEmpty());
    }
    // test comparable for different types of elements -- 
    //   integer comparison shouldn't be built in
    @Test(timeout=SECOND)
    public void testIntegerOrdering() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        List<Integer> integers = new ArrayList<>();
        for (int i = 0; i < 190; i++) {
            int integer = (int) (100 * Math.random());
            integers.add(integer);
            heap.insert(integer);
        }
        Collections.sort(integers);
        for (int i = 0; i < integers.size(); i++) {
            assertEquals(heap.removeMin(), integers.get(i));
        }
        assertTrue(heap.isEmpty());
    }
    @Test(timeout=SECOND)
    public void testStringOrdering() {
        IPriorityQueue<String> heap = makeInstance();
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 190; i++) {
            String value = (100 * Math.random()) + "";
            strings.add(value);
            heap.insert(value);
        }
        Collections.sort(strings);
        for (int i = 0; i < strings.size(); i++) {
            assertEquals(heap.removeMin(), strings.get(i));
        }
        assertTrue(heap.isEmpty());
    }
    @Test(timeout=SECOND)
    public void testCharOrdering() {
        IPriorityQueue<Character> heap = this.makeInstance();
        List<Character> characters = new ArrayList<>();
        for (int i = 0; i < 190; i++) {
            char value = (char) (20 * Math.random() + 45);
            characters.add(value);
            heap.insert(value);
        }
        Collections.sort(characters);
        for (int i = 0; i < characters.size(); i++) {
            assertEquals(heap.removeMin(), characters.get(i));
        }
        assertTrue(heap.isEmpty());
    }
    @Test(timeout=SECOND)
    public void testDoubleOrdering() {
        IPriorityQueue<Double> heap = makeInstance();
        List<Double> doubles = new ArrayList<>();
        for (int i = 0; i < 190; i++) {
            double value = 33.3 * Math.random();
            doubles.add(value);
            heap.insert(value);
        }
        Collections.sort(doubles);
        for (int i = 0; i < doubles.size(); i++) {
            assertEquals(heap.removeMin(), doubles.get(i));
        }
        assertTrue(heap.isEmpty());
    }
    
    // test duplicate elements
    @Test(timeout=SECOND)
    public void addDuplicates() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(9);
        heap.insert(3);
        heap.insert(3);
        heap.insert(4);
        heap.insert(3);
        heap.insert(5);
        assertEquals(3, heap.peekMin());
        assertEquals(6, heap.size());
    }
    @Test(timeout=SECOND)
    public void removeDuplicates() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(9);
        heap.insert(3);
        heap.insert(3);
        heap.insert(4);
        heap.insert(3);
        heap.insert(5);
        assertEquals(3, heap.removeMin());
        assertEquals(3, heap.removeMin());
        assertEquals(3, heap.removeMin());
        assertEquals(4, heap.removeMin());
        assertEquals(5, heap.removeMin());
        assertEquals(9, heap.removeMin());
    }
    @Test(timeout=SECOND)
    public void removeDuplicatePreservesOrder() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(9);
        heap.insert(3);
        heap.insert(3);
        heap.insert(4);
        heap.insert(3);
        heap.insert(5);
        assertEquals(3, heap.removeMin());
        assertEquals(3, heap.removeMin());
        assertEquals(3, heap.removeMin());
        heap.insert(0);
        assertEquals(0, heap.removeMin());
        assertEquals(4, heap.removeMin());
        assertEquals(5, heap.removeMin());
        assertEquals(9, heap.removeMin());
    }
}
