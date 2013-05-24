/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.coords;

import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.picard.reference.ReferenceSequence;
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
public class CoordsCoverageTest {
    
    public CoordsCoverageTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of computeNext method, of class CoordsCoverage.
     */
    @Test
    public void testComputeNext() {
        System.out.println("computeNext");
        
        List<CoordsRecord> list = new ArrayList<CoordsRecord>();
        list.add(new CoordsRecord(createCoord("a1", 1, 10), createCoord("b1", 1, 10), 0));
        list.add(new CoordsRecord(createCoord("a1", 20, 30), createCoord("b1", 20, 30), 0));
        list.add(new CoordsRecord(createCoord("a2", 1, 10), createCoord("b2", 1, 10), 0));
        
        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("a1", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("a2", 1, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        CoordsCoverage instance = new CoordsCoverage(list.iterator(), dict);
        Interval expResult = new Interval("a1", 1, 10, false, ".");
        Interval result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = new Interval("a1", 20, 30, false, ".");
        result = instance.computeNext();
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
        list.add(new CoordsRecord(createCoord("a1", 1, 10), createCoord("b1", 1, 10), 0));
        list.add(new CoordsRecord(createCoord("a1", 20, 30), createCoord("b1", 20, 30), 0));
        list.add(new CoordsRecord(createCoord("a2", 1, 10), createCoord("b2", 1, 10), 0));
        
        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("b1", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("b2", 1, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        CoordsCoverage instance = new CoordsCoverage(list.iterator(), dict);
        Interval expResult = new Interval("b1", 1, 10, false, ".");
        Interval result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = new Interval("b1", 20, 30, false, ".");
        result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = new Interval("b2", 1, 10, false, ".");
        result = instance.computeNext();
        assertEquals(expResult, result);
        
        expResult = null;
        result = instance.computeNext();
        assertEquals(expResult, result);
    }

    private Coord createCoord(String name, int start, int end){
        return new Coord(name, start, end, end-start, false);
    }
    
    private SAMSequenceDictionary createDict(List<ReferenceSequence> refSeqList) {
        final List<SAMSequenceRecord> r = new ArrayList<SAMSequenceRecord>();
        for (ReferenceSequence refSeq : refSeqList) {
            r.add(new SAMSequenceRecord(refSeq.getName()));
        }
        return new SAMSequenceDictionary(r);
    }
}
