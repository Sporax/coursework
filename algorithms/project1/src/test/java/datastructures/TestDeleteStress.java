package datastructures;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

/**
 * This file should contain any tests that check and make sure your
 * delete method is efficient.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteStress extends TestDoubleLinkedList {

    @Test(timeout=15 * SECOND)
    public void testDeleteAtEndIsEfficient() {
        IList<Integer> list = new DoubleLinkedList<>();
        int cap = 5000000;
        for (int i = 0; i < cap; i++) {
            list.add(i * 2);
        }
        for (int i = 0; i < cap; i++) {
            list.delete(list.size() - 1);
        }
        assertEquals(0, list.size());
    }
    
    @Test(timeout=15 * SECOND)
    public void testDeleteAtFrontIsEfficient() {
        IList<Integer> list = new DoubleLinkedList<>();
        int cap = 5000000;
        for (int i = 0; i < cap; i++) {
            list.add(i * 2);
        }
        for (int i = 0; i < cap; i++) {
            list.delete(0);
        }
        assertEquals(0, list.size());
    }
    
    @Test(timeout=15 * SECOND)
    public void testDeleteNearFrontIsEfficient() {
        IList<Integer> list = new DoubleLinkedList<>();
        int cap = 5000000;
        for (int i = 0; i < cap; i++) {
            list.add(i * 2);
        }
        for (int i = 0; i < cap/2; i++) {
            list.delete(2);
        }
        for (int i = 0; i < cap/2 - 1; i++) {
            list.delete(1);
        }
        assertEquals(1, list.size());
    }
    
    @Test(timeout=15 * SECOND)
    public void testDeleteNearEndIsEfficient() {
        IList<Integer> list = new DoubleLinkedList<>();
        int cap = 5000000;
        for (int i = 0; i < cap; i++) {
            list.add(i * 2);
        }
        for (int i = 0; i < cap/2; i++) {
            list.delete(list.size() - 3);
        }
        for (int i = 0; i < cap/2 - 1; i++) {
            list.delete(list.size() - 2);
        }
        assertEquals(1, list.size());
    }
    
    @Test(timeout=15 * SECOND)
    public void testDeleteFromMiddleBrute() {
        // middle should be the longest to delete
        int numLeft = 2;
    	IList<Integer> list = new DoubleLinkedList<>();
        int cap = 100000;
        for (int i = 0; i < cap; i++) {
            list.add(i * 2);
        }
        for (int i = 0; i < cap-numLeft; i++) {
            list.delete(list.size()/2 + 1);
        }
        assertEquals(numLeft, list.size());
    }
    
    @Test(timeout=15 * SECOND)
    public void testDeleteOneElementManyTimes() {
        IList<Integer> list = new DoubleLinkedList<>();
        int cap = 5000000;
        for (int i = 0; i < cap; i++) {
            list.add(i * 2);
            list.delete(0);
        }
        assertEquals(0, list.size());
    }
}
