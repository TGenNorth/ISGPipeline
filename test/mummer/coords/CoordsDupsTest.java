/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.coords;

import java.util.List;
import java.util.ArrayList;
import net.sf.picard.util.Interval;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class CoordsDupsTest {
    
    public CoordsDupsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of computeNext method, of class CoordsDups.
     */
    @Test
    public void testComputeNext() {
        System.out.println("computeNext");
        
        List<CoordsRecord> list = new ArrayList<CoordsRecord>();
        list.add(new CoordsRecord(createCoord("a1", 1, 10), createCoord("a1", 1, 10), 0));
        list.add(new CoordsRecord(createCoord("a1", 20, 30), createCoord("a2", 20, 30), 0));
        list.add(new CoordsRecord(createCoord("a2", 1, 10), createCoord("a1", 1, 10), 0));
        
        
        CoordsDups instance = new CoordsDups(list.iterator());
        Interval expResult = new Interval("a1", 20, 30, false, ".");
        Interval result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = new Interval("a2", 1, 10, false, ".");
        result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = null;
        result = instance.computeNext();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testComputeNext2() {
        System.out.println("computeNext2");
        
        List<CoordsRecord> list = new ArrayList<CoordsRecord>();
        list.add(new CoordsRecord(createCoord("a1", 1, 10), createCoord("a1", 1, 10), 0));
        list.add(new CoordsRecord(createCoord("a1", 20, 30), createCoord("a2", 20, 30), 0));
        list.add(new CoordsRecord(createCoord("a2", 1, 10), createCoord("a2", 1, 10), 0));
        list.add(new CoordsRecord(createCoord("a2", 20, 30), createCoord("a1", 1, 10), 0));
        
        CoordsDups instance = new CoordsDups(list.iterator());
        Interval expResult = new Interval("a1", 20, 30, false, ".");
        Interval result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = new Interval("a2", 20, 30, false, ".");
        result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = null;
        result = instance.computeNext();
        assertEquals(expResult, result);
    }
    
    private Coord createCoord(String name, int start, int end){
        return new Coord(name, start, end, end-start, false);
    }

}
