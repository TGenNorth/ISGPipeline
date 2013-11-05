/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.tools.FindParalogs.IntervalOverlapInfo;
import mummer.coords.Coord;
import mummer.coords.CoordsRecord;
import net.sf.picard.util.Interval;
import net.sf.samtools.SAMSequenceDictionary;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class FindParalogsTest {
    
    public FindParalogsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testCalculateOverlap() {
        System.out.println("calculateOverlap");
        Interval i1 = new Interval("S1", 10, 20);
        Interval i2 = new Interval("S1", 15, 30);
        FindParalogs instance = new FindParalogs();
        IntervalOverlapInfo expResult = new IntervalOverlapInfo(0, 5, false);
        IntervalOverlapInfo result = instance.calculateOverlap(i1, i2);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of translate method, of class FindParalogs.
     */
    @Test
    public void testTranslate() {
        System.out.println("translate");
        Interval i1 = new Interval("S1", 10, 20);
        Interval i2 = new Interval("S1", 15, 30);
        Interval i3 = new Interval("S2", 1, 20);
        FindParalogs instance = new FindParalogs();
        Interval expResult = new Interval("S2", 1, 6);
        Interval result = instance.translate(i1, i2, i3);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testTranslateReverseStrand() {
        System.out.println("translateReverseStrand");
        Interval i1 = new Interval("S1", 10, 20);
        Interval i2 = new Interval("S1", 15, 30);
        Interval i3 = new Interval("S2", 1, 20, true, ".");
        FindParalogs instance = new FindParalogs();
        Interval expResult = new Interval("S2", 15, 20);
        Interval result = instance.translate(i1, i2, i3);
        assertEquals(expResult, result);
    }
}
