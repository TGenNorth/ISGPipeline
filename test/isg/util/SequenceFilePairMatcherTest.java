/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePairMatcherTest {
    
    public SequenceFilePairMatcherTest() {
    }

    /**
     * Test of process method, of class SequenceFilePairMatcher.
     */
    @Test
    public void testProcessMix() {
        System.out.println("processMix");
        Collection<File> files = Arrays.asList(new File("sampleA_1.fastq"),
                new File("sampleA_2.fastq"),
                new File("sampleB.fastq"));
        SequenceFilePairMatcher instance = new SequenceFilePairMatcher();
        Collection<SequenceFilePair> expResult = Arrays.asList(
                createPair("sampleA", new File("sampleA_1.fastq"), new File("sampleA_2.fastq")),
                createPair("sampleB", new File("sampleB.fastq"), null));
        Collection<SequenceFilePair> result = instance.process(files);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of process method, of class SequenceFilePairMatcher.
     */
    @Test
    public void testProcessMultiplePaired() {
        System.out.println("processMultiplePairedEnd");
        Collection<File> files = Arrays.asList(new File("sampleA_1.fastq"),
                new File("sampleA_2.fastq"),
                new File("sampleB_1.fastq"),
                new File("sampleB_2.fastq"));
        SequenceFilePairMatcher instance = new SequenceFilePairMatcher();
        Collection<SequenceFilePair> expResult = Arrays.asList(
                createPair("sampleA", new File("sampleA_1.fastq"), new File("sampleA_2.fastq")),
                createPair("sampleB", new File("sampleB_1.fastq"), new File("sampleB_2.fastq")));
        Collection<SequenceFilePair> result = instance.process(files);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of process method, of class SequenceFilePairMatcher.
     */
    @Test
    public void testProcessMultipleSingleEnd() {
        System.out.println("processMultipleSingleEnd");
        Collection<File> files = Arrays.asList(new File("sampleA.fastq"),
                new File("sampleB.fastq"));
        SequenceFilePairMatcher instance = new SequenceFilePairMatcher();
        Collection<SequenceFilePair> expResult = Arrays.asList(
                createPair("sampleA", new File("sampleA.fastq"), null),
                createPair("sampleB", new File("sampleB.fastq"), null));
        Collection<SequenceFilePair> result = instance.process(files);
        assertEquals(expResult, result);
        
    }
    
    /**
     * Test of process method, of class SequenceFilePairMatcher.
     */
    @Test
    public void testProcessSampleNameDuplicate() {
        System.out.println("processSampleNameDuplicate");
        Collection<File> files = Arrays.asList(new File("sampleA.fastq"),
                new File("sampleA_1.fastq"),
                new File("sampleA_2.fastq"));
        SequenceFilePairMatcher instance = new SequenceFilePairMatcher();
        Collection<SequenceFilePair> expResult = Arrays.asList(
                createPair("sampleA", new File("sampleA.fastq"), null),
                createPair("sampleA-1", new File("sampleA_1.fastq"), new File("sampleA_2.fastq")));
        Collection<SequenceFilePair> result = instance.process(files);
        assertEquals(expResult, result);
        
    }
    
    private SequenceFilePair createPair(String sample, File f1, File f2){
        SequenceFilePair ret = new SequenceFilePair(sample);
        ret.setSeq1(f1);
        ret.setSeq2(f2);
        return ret;
    }
}
