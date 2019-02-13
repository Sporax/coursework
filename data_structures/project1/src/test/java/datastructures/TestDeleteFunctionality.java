package datastructures;

import org.junit.FixMethodOrder;	
import org.junit.Test;
import org.junit.runners.MethodSorters;

import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This class should contain all the tests you implement to verify that
 * your 'delete' method behaves as specified.
 *
 * This test _extends_ your TestDoubleLinkedList class. This means that when
 * you run this test, not only will your tests run, all of the ones in
 * TestDoubleLinkedList will also run.
 *
 * This also means that you can use any helper methods defined within
 * TestDoubleLinkedList here. In particular, you may find using the
 * 'assertListMatches' and 'makeBasicList' helper methods to be useful.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestDeleteFunctionality extends TestDoubleLinkedList {

    @Test(timeout=SECOND)
    public void testDeleteEmpty() {
    	IList<String> list = new DoubleLinkedList<>();
    	try {
        	list.delete(0);
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testDeleteMiddle2() {
    	IList<String> list = this.makeBasicList();
    	list.delete(1);
    	list.delete(1);
    	list.delete(0);
    	try {
        	list.delete(0);
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testDeleteSingleFromFront() {
    	IList<Integer> list = new DoubleLinkedList<>();
        list.add(1);
        list.delete(0);
        assertTrue(list.size() == 0);
    }

    @Test(timeout=SECOND)
    public void testSizeUpdate() {
        IList<String> list = makeBasicList();
        list.delete(1);
        assertTrue(list.size() == 2);
    }
    
    @Test(timeout=SECOND)
    public void testDeleteFromFront() {
    	IList<String> list = new DoubleLinkedList<>();
        list.add("1");
        list.add("2");
        list.delete(0);
        this.assertListMatches(new String[] {"2"}, list);
    }
    
    @Test(timeout=SECOND)
    public void testDeleteMiddle() {
    	IList<String> list = this.makeBasicList();
    	for (int i = 5; i <= 8; i++) {
    		list.add(i + "");
    	}
    	list.delete(4);
    	this.assertListMatches(new String[] {"a", "b", "c", "5", "7", "8"}, list);
    }
    
    @Test(timeout=SECOND)
    public void testDeleteFromEnd() {
    	IList<String> list = this.makeBasicList();
    	for (int i = 5; i <= 8; i++) {
    		list.add(i + "");
    	}
    	list.delete(list.size()-1);
    	this.assertListMatches(new String[] {"a", "b", "c", "5", "6", "7"}, list);
    }
    
    @Test(timeout=SECOND)
    public void testDeleteFromEndTwice() {
    	IList<String> list = this.makeBasicList();
    	for (int i = 5; i <= 8; i++) {
    		list.add(i + "");
    	}
    	int index = list.size()-1;
    	list.delete(index);
    	
    	try {
    		list.delete(index);
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testDeleteAfterEnd() {
    	IList<String> list = this.makeBasicList();
    	for (int i = 5; i <= 8; i++) {
    		list.add(i + "");
    	}
    	int index = list.size();
    	
    	try {
    		list.delete(index);
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }
    
    @Test(timeout=SECOND)
    public void testDeleteBeforeStart() {
    	IList<String> list = this.makeBasicList();

    	try {
    		list.delete(-5);
            // We didn't throw an exception? Fail now.
            fail("Expected IndexOutOfBoundsException");
        } catch (IndexOutOfBoundsException ex) {
            // Do nothing: this is ok
        }
    }
}
