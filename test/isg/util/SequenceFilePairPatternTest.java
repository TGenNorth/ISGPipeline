/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePairPatternTest {
    
    public SequenceFilePairPatternTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of sample method, of class SequenceFilePairPattern.
     */
    @Test
    public void testSample() {
        System.out.println("sample");
        File f = new File("sample_1.fastq");
        SequenceFilePairPattern instance = new SequenceFilePairPattern("(.*)_([12])\\..*");
        String expResult = "sample";
        String result = instance.sample(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of other method, of class SequenceFilePairPattern.
     */
    @Test
    public void testOther() {
        System.out.println("other");
        File f = new File("sample_1.fastq");
        SequenceFilePairPattern instance = new SequenceFilePairPattern("(.*)_([12])\\..*");
        File expResult = new File("sample_2.fastq");
        File result = instance.other(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of matches method, of class SequenceFilePairPattern.
     */
    @Test
    public void testMatches() {
        System.out.println("matches");
        File f = new File("sample_1.fastq");
        SequenceFilePairPattern instance = new SequenceFilePairPattern("(.*)_([12])\\..*");
        assertTrue(instance.matches(f));
    }
}
