/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.PicardException;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMReadGroupRecord;
import org.broad.tribble.FeatureReader;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;

/**
 * Determine sample name from header in vcf file.
 * 
 * @author jbeckstrom
 */
public class VCFSampleNameDetectionAlgorithm implements SampleNameDetectionAlgorithm {

    @Override
    public boolean detectable(File f) {
        return f.getName().endsWith(".vcf");
    }

    @Override
    public String apply(File f) {
        if (!detectable(f)) {
            throw new IllegalArgumentException("Cannot detect sample name for file: " + f.getPath());
        }
        final FeatureReader<VariantContext> reader = createVCFReader(f);
        try {
            final VCFHeader header = (VCFHeader) reader.getHeader();
            final List<String> samples = header.getGenotypeSamples();
            assertFalse(samples.isEmpty(), "Cannot determine sample name: missing genotype information for file: " + f.getPath());
            assertFalse(samples.size() > 1, "Cannot determine sample name: more than one sample detected for file: " + f.getPath());
            return samples.get(0);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(VCFSampleNameDetectionAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void assertFalse(boolean b, String msg) {
        if (b) {
            throw new IllegalArgumentException(msg);
        }
    }

    private FeatureReader<VariantContext> createVCFReader(File f) {
        try {
            return new TribbleIndexedFeatureReader<VariantContext>(f.getAbsolutePath(), new VCFCodec(), false);
        } catch (IOException ex) {
            throw new PicardException("An error occured trying to read file: " + f.getAbsolutePath(), ex);
        }
    }
}
