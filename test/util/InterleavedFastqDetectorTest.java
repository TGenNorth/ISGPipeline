/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;
import java.util.List;
import java.io.File;
import java.util.Iterator;
import net.sf.picard.fastq.FastqReader;
import net.sf.picard.fastq.FastqRecord;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class InterleavedFastqDetectorTest {
    
    public InterleavedFastqDetectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of isInterleaved method, of class InterleavedFastqDetector.
     */
    @Test
    public void testNotInterleaved_Iterator() {
        System.out.println("notInterleaved");
        List<FastqRecord> records = Arrays.asList(
                new FastqRecord("ILLUMINA-5C547F_0007:4:1:1243:1022#GCCAAT/1","","",""),
                new FastqRecord("ILLUMINA-5C547F_0007:4:1:1243:1022#GCCAAT/1","","",""));
        Iterator<FastqRecord> iter = records.iterator();
        boolean expResult = false;
        boolean result = InterleavedFastqDetector.isInterleaved(iter);
        assertEquals(expResult, result);    
    }
    
    @Test
    public void testNotInterleaved2_Iterator() {
        System.out.println("notInterleaved");
        List<FastqRecord> records = Arrays.asList(
                new FastqRecord("@KO97-1026.2.SRR000342.1 D1JYA3U01CX2GO length=97","","",""),
                new FastqRecord("@KO97-1026.2.SRR000342.2 D1JYA3U01A2ML6 length=118","","",""));
        Iterator<FastqRecord> iter = records.iterator();
        boolean expResult = false;
        boolean result = InterleavedFastqDetector.isInterleaved(iter);
        assertEquals(expResult, result);    
    }
    
    @Test
    public void testIsInterleaved_Iterator() {
        System.out.println("isInterleaved");
        List<FastqRecord> records = Arrays.asList(
                new FastqRecord("ILLUMINA-5C547F_0007:4:1:1243:1022#GCCAAT/1","","",""),
                new FastqRecord("ILLUMINA-5C547F_0007:4:1:1243:1022#GCCAAT/2","","",""));
        Iterator<FastqRecord> iter = records.iterator();
        boolean expResult = true;
        boolean result = InterleavedFastqDetector.isInterleaved(iter);
        assertEquals(expResult, result);    
    }
}
