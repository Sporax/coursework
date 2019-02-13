package datastructures.sorting;

import misc.BaseTest;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import misc.Searcher;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestTopKSortFunctionality extends BaseTest {
    @Test(timeout=SECOND)
    public void testSize() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 30; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(7, list);
        assertEquals(7, top.size());
    }
    
    @Test(timeout=SECOND)
    public void testSimpleUsage() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testSimpleUsageReverse() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 19; i >= 0; i--) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(5, list);
        assertEquals(5, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(15 + i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testAlternatingDuplicates() {
        int k = 7;
        // Should end up with 6 '2's and a single '1'
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i % 3);
        }

        IList<Integer> top = Searcher.topKSort(k, list);
        assertEquals(k, top.size());
        assertEquals(1, top.get(0));
        for (int i = 1; i < top.size(); i++) {
            assertEquals(2, top.get(i));
        }
        
    }
    
    @Test(timeout=SECOND)
    public void testGeneralList() {
        int k = 4;
        IList<Integer> list = new DoubleLinkedList<>();
        List<Integer> check = new ArrayList<>();
        
        list.add(8);
        check.add(8);
        list.add(1);
        check.add(1);
        list.add(102);
        check.add(102);
        list.add(37);
        check.add(37);
        list.add(4);
        check.add(4);
        list.add(13);
        check.add(13);
        list.add(2);
        check.add(2);
        
        Collections.sort(check);
        IList<Integer> top = Searcher.topKSort(k, list);
        assertEquals(k, top.size());

        assertTrue(listEquals(top, check, k));
    }
    
    @Test(timeout=SECOND)
    public void testKSizeOfList() {
        int k = 20;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(k, list);
        assertEquals(k, top.size());
        for (int i = 0; i < top.size(); i++) {
            assertEquals(i, top.get(i));
        }
        
    }
    
    @Test(timeout=SECOND)
    public void testKTooLarge() {
        int k = 21;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        IList<Integer> top = Searcher.topKSort(k, list);
        assertEquals(20, top.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, top.get(i));
        }
    }
    
    @Test(timeout=SECOND)
    public void testEmpty() {
        int k = 10;
        IList<Integer> list = new DoubleLinkedList<>();

        IList<Integer> top = Searcher.topKSort(k, list);
        assertEquals(0, top.size());
    }
    
    @Test(timeout=SECOND)
    public void testKLessThanZero() {
        int k = -1;
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }

        
        try {
            Searcher.topKSort(k, list);
            fail();
        } catch(IllegalArgumentException e) {
            // pass
        }
    }

    @Test(timeout=SECOND)
    public void testKZero() {
        IList<Integer> list = new DoubleLinkedList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        IList<Integer> top = Searcher.topKSort(0, list);
        assertEquals(0, top.size());
    }
    
    @Test(timeout=SECOND)
    public void testStrings() {
        int k = 3;
        IList<String> list = new DoubleLinkedList<>();
        list.add("a");
        list.add("g");
        list.add("c");
        list.add("e");
        list.add("d");
        list.add("b");
        list.add("f");
        list.add("h");

        IList<String> top = Searcher.topKSort(k, list);
        assertEquals(k, top.size());
        assertEquals("f", top.get(0));
        assertEquals("g", top.get(1));
        assertEquals("h", top.get(2));
    }
    
    private boolean listEquals(IList<Integer> list, List<Integer> check, int k) {
        for (int i = 0; i < k; i++) {
            if (check.get(check.size()-k+i) != list.get(i)) {
                return false;
            }
        }
        return true;
    }
}
