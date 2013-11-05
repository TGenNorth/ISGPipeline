/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.util.Arrays;
import java.util.List;
import java.util.Comparator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class ComparableIteratorTest {

    public ComparableIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of next method, of class ComparableIterator.
     */
    @Test
    public void testNextUnsortedWithValidation() {
        System.out.println("nextUnsortedWithValidation");
        Comparator<Integer> cmp = new Comparator<Integer>() {

            @Override
            public int compare(Integer t, Integer t1) {
                return t.compareTo(t1);
            }
        };
        List<Integer> unsortedList = Arrays.asList(1, 2, 1);
        ComparableIterator instance = new ComparableIterator(unsortedList.iterator(), cmp, null, true);
        assertEquals(1, instance.next());
        try {
            instance.next();
            fail("No exception was thrown for unsortedList: " + unsortedList);
        } catch (IllegalStateException e) {
        }
    }
    
    @Test
    public void testNextSortedWithValidation() {
        System.out.println("nextSortedWithValidation");
        Comparator<Integer> cmp = new Comparator<Integer>() {

            @Override
            public int compare(Integer t, Integer t1) {
                return t.compareTo(t1);
            }
        };
        List<Integer> sortedList = Arrays.asList(1, 2, 3);
        ComparableIterator instance = new ComparableIterator(sortedList.iterator(), cmp, null, true);
        assertEquals(1, instance.next());
        assertEquals(2, instance.next());
        assertEquals(3, instance.next());
        assertFalse(instance.hasNext());
    }
}
