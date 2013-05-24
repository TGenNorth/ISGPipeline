/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.detector.VCFSampleNameDetectionAlgorithm;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import java.util.List;
import java.util.HashSet;
import org.broadinstitute.variant.vcf.VCFHeader;
import java.util.Arrays;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriterFactory;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriter;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class VCFSampleNameDetectionAlgorithmTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    
    public VCFSampleNameDetectionAlgorithmTest() {
    }

    /**
     * Test of detectable method, of class VCFSampleNameDetectionAlgorithm.
     */
    @Test
    public void testDetectable() {
        System.out.println("detectable");
        File f = new File("test.vcf");
        VCFSampleNameDetectionAlgorithm instance = new VCFSampleNameDetectionAlgorithm();
        boolean result = instance.detectable(f);
        assertTrue(result);
    }

    /**
     * Test of detectable method, of class VCFSampleNameDetectionAlgorithm.
     */
    @Test
    public void testNotDetectable() {
        System.out.println("notDetectable");
        File f = new File("test.txt");
        VCFSampleNameDetectionAlgorithm instance = new VCFSampleNameDetectionAlgorithm();
        boolean result = instance.detectable(f);
        assertFalse(result);
    }

    /**
     * Test of apply method, of class VCFSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApplySingleSample() {
        System.out.println("applySingleSample");
        File f = createTempQuietly("abc", ".vcf");
        createVCF(f, Arrays.asList("abc"));
        VCFSampleNameDetectionAlgorithm instance = new VCFSampleNameDetectionAlgorithm();
        String expResult = "abc";
        String result = instance.apply(f);
        assertEquals(expResult, result);
        f.delete();
    }
    
    /**
     * Test of apply method, of class VCFSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApplyMulitpleSamples() {
        System.out.println("applyMulitpleSamples");
        File f = createTempQuietly("abc", ".vcf");
        createVCF(f, Arrays.asList("abc", "123"));
        VCFSampleNameDetectionAlgorithm instance = new VCFSampleNameDetectionAlgorithm();
        exception.expect(IllegalArgumentException.class);
        instance.apply(f);
    }
    
    /**
     * Test of apply method, of class VCFSampleNameDetectionAlgorithm.
     */
    @Test
    public void testApplyNoSamples() {
        System.out.println("applyNoSamples");
        File f = createTempQuietly("abc", ".vcf");
        createVCF(f, Collections.EMPTY_LIST);
        VCFSampleNameDetectionAlgorithm instance = new VCFSampleNameDetectionAlgorithm();
        exception.expect(IllegalArgumentException.class);
        instance.apply(f);
    }

    private void createVCF(final File f, final List<String> samples) {
        VariantContextWriter writer = VariantContextWriterFactory.create(f, new SAMSequenceDictionary());
        VCFHeader header = new VCFHeader(Collections.EMPTY_SET, new HashSet(samples));
        writer.writeHeader(header);
        writer.close();
    }

    private File createTempQuietly(String prefix, String suffix) {
        try {
            return File.createTempFile(prefix, suffix);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
