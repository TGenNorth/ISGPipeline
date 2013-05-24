/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.detector.FilePairDetectionAlgorithm;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class FilePairDetectionAlgorithmTest {
    
    public FilePairDetectionAlgorithmTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of apply method, of class FilePairDetectionAlgorithm.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        File f = new File("test_1.fastq");
        FilePairDetectionAlgorithm instance = new FilePairDetectionAlgorithm(".*_([12])\\..*");
        File expResult = new File("test_2.fastq");
        File result = instance.apply(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of detectable method, of class FilePairDetectionAlgorithm.
     */
    @Test
    public void testDetectable() {
        System.out.println("detectable");
        File f = new File("test.fastq");
        FilePairDetectionAlgorithm instance = new FilePairDetectionAlgorithm(".*_([12])\\..*");
        assertFalse(instance.detectable(f));
    }
}
