/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class SkimmingIteratorTest {

    private final Comparator<Integer> intCmp = new Comparator<Integer>() {

        @Override
        public int compare(Integer t, Integer t1) {
            return t.compareTo(t1);
        }
    };
    
    public SkimmingIteratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Test of next method, of class SkimmingIterator.
     */
    @Test
    public void testNext() {
        System.out.println("next");
        List<String> names = new ArrayList<String>();
        List<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();
        
        names.add("A");
        iters.add(Arrays.asList(1, 2, 3).iterator());
        names.add("B");
        iters.add(Arrays.asList(2, 3).iterator());
        names.add("C");
        iters.add(Arrays.asList(3, 4).iterator());
        
        SkimmingIterator<Integer> instance = new SkimmingIterator<Integer>(names, iters, intCmp);
        Map<String, Integer> expResult = new HashMap<String, Integer>();
        expResult.put("A", 1);
        Map result = instance.next();
        assertEquals(expResult, result);
        
        expResult = new HashMap<String, Integer>();
        expResult.put("A", 2);
        expResult.put("B", 2);
        result = instance.next();
        assertEquals(expResult, result);
        
        expResult = new HashMap<String, Integer>();
        expResult.put("A", 3);
        expResult.put("B", 3);
        expResult.put("C", 3);
        result = instance.next();
        assertEquals(expResult, 
                result);
        
        expResult = new HashMap<String, Integer>();
        expResult.put("C", 4);
        result = instance.next();
        assertEquals(expResult, result);
        
        assertFalse(instance.hasNext());
    }
    
}
