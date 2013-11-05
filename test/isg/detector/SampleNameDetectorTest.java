/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.detector.SampleNameDetectionAlgorithm;
import isg.detector.SampleNameDetector;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class SampleNameDetectorTest {
    
    public SampleNameDetectorTest() {
    }

    /**
     * Test of detect method, of class SampleNameDetector.
     */
    @Test
    public void testDetectNull() {
        System.out.println("detectNull");
        File f = new File("");
        SampleNameDetector instance = new SampleNameDetector();
        instance.add(create(false, ""));
        String expResult = null;
        String result = instance.detect(f);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of detect method, of class SampleNameDetector.
     */
    @Test
    public void testDetect() {
        System.out.println("detect");
        File f = new File("");
        SampleNameDetector instance = new SampleNameDetector();
        instance.add(create(true, "abc"));
        String expResult = "abc";
        String result = instance.detect(f);
        assertEquals(expResult, result);
    }
    
    private SampleNameDetectionAlgorithm create(final boolean detectable, final String apply){
        return new SampleNameDetectionAlgorithm(){

            @Override
            public boolean detectable(File i) {
                return detectable;
            }

            @Override
            public String apply(File i) {
                return apply;
            }
        };
    }
}
