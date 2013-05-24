/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.detector.RegexSampleNameDetectionAlgorithm;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class RegexSampleNameDetectionAlgorithmTest {
    
    public RegexSampleNameDetectionAlgorithmTest() {
    }

    /**
     * Test of detectable method, of class RegexSampleNameDetectionAlgorithm.
     */
    @Test
    public void testDetectable() {
        System.out.println("detectable");
        File f = new File("sample_1.fastq");
        RegexSampleNameDetectionAlgorithm instance = new RegexSampleNameDetectionAlgorithm("(.*)_1\\.fastq", 1);
        boolean result = instance.detectable(f);
        assertTrue(result);
    }
    
    /**
     * Test of detectable method, of class RegexSampleNameDetectionAlgorithm.
     */
    @Test
    public void testNotDetectable() {
        System.out.println("notDetectable");
        File f = new File("sample_1.fastq");
        RegexSampleNameDetectionAlgorithm instance = new RegexSampleNameDetectionAlgorithm("(.*)_1\\.txt", 1);
        boolean result = instance.detectable(f);
        assertFalse(result);
    }

    /**
     * Test of apply method, of class RegexSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        File f = new File("sample_1.fastq");
        RegexSampleNameDetectionAlgorithm instance = new RegexSampleNameDetectionAlgorithm("(.*)_1\\.fastq", 1);
        String expResult = "sample";
        String result = instance.apply(f);
        assertEquals(expResult, result);
    }
}
