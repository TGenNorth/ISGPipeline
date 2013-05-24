/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.detector.BAMSampleNameDetectionAlgorithm;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import java.io.IOException;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMReadGroupRecord;
import java.util.List;
import net.sf.samtools.SAMFileHeader;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class BAMSampleNameDetectionAlgorithmTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public BAMSampleNameDetectionAlgorithmTest() {
    }

    /**
     * Test of detectable method, of class BAMSampleNameDetectionAlgorithm.
     */
    @Test
    public void testDetectable() {
        System.out.println("detectable");
        File f = new File("test.bam");
        BAMSampleNameDetectionAlgorithm instance = new BAMSampleNameDetectionAlgorithm();
        assertTrue(instance.detectable(f));
    }

    /**
     * Test of detectable method, of class BAMSampleNameDetectionAlgorithm.
     */
    @Test
    public void testNotDetectable() {
        System.out.println("notDetectable");
        File f = new File("test.txt");
        BAMSampleNameDetectionAlgorithm instance = new BAMSampleNameDetectionAlgorithm();
        assertFalse(instance.detectable(f));
    }

    /**
     * Test of apply method, of class BAMSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApplySingleSample() {
        System.out.println("applySingleSample");
        File f = createTempQuietly("abc", ".bam");
        createBAM(f, Arrays.asList(createRG("1", "abc")));
        BAMSampleNameDetectionAlgorithm instance = new BAMSampleNameDetectionAlgorithm();
        String expResult = "abc";
        String result = instance.apply(f);
        assertEquals(expResult, result);
    }

    /**
     * Test of apply method, of class BAMSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApplyMultipleSamples() {
        System.out.println("applyMulitpleSamples");
        File f = createTempQuietly("abc", ".bam");
        createBAM(f, Arrays.asList(createRG("1", "abc"), createRG("2", "123")));
        BAMSampleNameDetectionAlgorithm instance = new BAMSampleNameDetectionAlgorithm();
        exception.expect(IllegalArgumentException.class);
        instance.apply(f);
    }

    /**
     * Test of apply method, of class BAMSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApplyNoSample() {
        System.out.println("applyNoSamples");
        File f = createTempQuietly("abc", ".bam");
        createBAM(f, Collections.EMPTY_LIST);
        BAMSampleNameDetectionAlgorithm instance = new BAMSampleNameDetectionAlgorithm();
        exception.expect(IllegalArgumentException.class);
        instance.apply(f);
    }

    private SAMReadGroupRecord createRG(String id, String sample) {
        SAMReadGroupRecord ret = new SAMReadGroupRecord(id);
        if (sample != null) {
            ret.setSample(sample);
        }
        return ret;
    }

    private void createBAM(File f, List<SAMReadGroupRecord> rgs) {
        final SAMFileHeader samHeader = new SAMFileHeader();
        samHeader.setReadGroups(rgs);
        final SAMFileWriter samWriter = new SAMFileWriterFactory().makeSAMWriter(samHeader, false, f);
        samWriter.close();
    }

    private File createTempQuietly(String prefix, String suffix) {
        try {
            return File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
