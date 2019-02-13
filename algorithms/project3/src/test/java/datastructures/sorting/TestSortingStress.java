package datastructures.sorting;

import misc.BaseTest;
import misc.Searcher;

import org.junit.Test;
import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;
import static org.junit.Assert.assertTrue;
import java.util.PriorityQueue;

public class TestSortingStress extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }
    
    @Test(timeout=10*SECOND)
    public void testSmallHeap() {
        // keep the size to 1 and keep adding and removing elements
        // should run in constant time
        IPriorityQueue<Integer> heap = makeInstance();
        heap.insert(3);
        for (int i = 0; i < 9000000; i++) {
            heap.insert((int) (i*Math.random()));
            heap.removeMin();
        }
        assertEquals(1, heap.size());
    }
    
    @Test(timeout=10*SECOND)
    public void testBulkInsertAscending() {
        // ascending should be constant time (except when increasing size of heap)
        IPriorityQueue<Integer> heap = makeInstance();
        for (int i = 0; i < 2000000; i++) {
            heap.insert(i);
        }
        assertEquals(2000000, heap.size());
    }
    
    @Test(timeout=10*SECOND)
    public void testRemoveStress() {
        // bulk insert ascending then remove
        IPriorityQueue<Integer> heap = makeInstance();
        for (int i = 0; i < 800000; i++) {
            heap.insert((int) (i*Math.random()));
        }
        for (int i = 0; i < 800000; i++) {
            heap.removeMin();
        }
        assertTrue(heap.isEmpty());
    }

    @Test(timeout=10*SECOND)
    public void testInsertBulkDescending() {
        // will be more expensive than insert ascending 
        IPriorityQueue<Integer> heap = makeInstance();
        for (int i = 900000; i > 0; i--) {
            heap.insert(i);
        }
        assertEquals(900000, heap.size());
    }
    
    @Test(timeout=10*SECOND)
    public void testBulkOrder() {
        IPriorityQueue<Integer> heap = makeInstance();
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (int i = 850000; i > 0; i--) {
            int value = (int) (10395 * Math.random());
            queue.add(value);
            heap.insert(value);
        }
        for (int i = 0; i < heap.size(); i++) {
            assertEquals(heap.removeMin(), queue.poll());
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testHeavyData() {
        IPriorityQueue<Long> heap = makeInstance();
        PriorityQueue<Long> queue = new PriorityQueue<>();
        for (int i = 500000; i > 0; i--) {
            long value = (long) (10395 * (Math.random() + 1000000000000000L));
            queue.add(value);
            heap.insert(value);
        }
        for (int i = 0; i < heap.size(); i++) {
            assertEquals(heap.removeMin(), queue.poll());
        }
    }
    
    // Top-K tests
    @Test(timeout=10*SECOND)
    public void testSortLarge() {
        int k = 10;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < k; i++) {
            list.add(i);
        }
        for (int i = 0; i < 500000; i++) {
            list.add(0);
        }
        
        IList<Integer> top = Searcher.topKSort(k, list);
        
        assertEquals(k, top.size());
        for (int i = 0; i < k; i++) {
            assertEquals(i, top.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testSortDecendingBulk() {
        int k = 10;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 500000; i >= 0; i--) {
            list.add(i);
        }
        
        IList<Integer> top = Searcher.topKSort(k, list);
        
        assertEquals(k, top.size());
        for (int i = 500000-1-k; i < k; i++) {
            assertEquals(i, top.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testSortAcendingBulk() {
        int k = 10;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 500000; i++) {
            list.add(i);
        }
        
        IList<Integer> top = Searcher.topKSort(k, list);
        
        assertEquals(k, top.size());
        for (int i = 0; i < k; i++) {
            assertEquals(500000-k+i, top.get(i));
        }
    }
    
    @Test(timeout=10*SECOND)
    public void testSortBulkLargeK() {
        int k = 50000;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i <50000; i++) {
            list.add(i);
        }
        
        IList<Integer> top = Searcher.topKSort(k, list);
        
        assertEquals(k, top.size());
        for (int i = 0; i < k; i++) {
            assertEquals(i, top.get(i));
        }
    }
}
