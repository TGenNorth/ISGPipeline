/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.detector.PairDetector;
import isg.detector.FilePairDetectionAlgorithm;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class PairDetectorTest {
    
    public PairDetectorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of detectAllPairs method, of class PairDetector.
     */
    @Test
    public void testDetectAllPairs() {
        System.out.println("detectAllPairs");
        Collection<File> c = Arrays.asList(new File("test_1.fastq"), new File("test_2.fastq"), new File("test.fastq"));
        PairDetector instance = new PairDetector();
        instance.add(new FilePairDetectionAlgorithm(".*_([12])\\..*"));
        List<List<File>> expResult = Arrays.asList(
                Arrays.asList(new File("test_1.fastq"), new File("test_2.fastq")),
                Arrays.asList(new File("test.fastq")));
        List result = instance.detectAllPairs(c);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of detectAllPairs method, of class PairDetector.
     */
    @Test
    public void testDetectAllPairsNoPairs() {
        System.out.println("detectAllPairsNoPairs");
        Collection<String> c = Arrays.asList("abc", "def", "ghi");
        PairDetector instance = new PairDetector();
        List<List<String>> expResult = Arrays.asList(
                Arrays.asList("abc"),
                Arrays.asList("def"),
                Arrays.asList("ghi"));
        List result = instance.detectAllPairs(c);
        assertEquals(expResult, result);
    }
}
