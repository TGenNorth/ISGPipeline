/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import java.io.File;
import java.util.List;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMReadGroupRecord;

/**
 * Determine sample name from read group in bam file.
 * 
 * @author jbeckstrom
 */
public class BAMSampleNameDetectionAlgorithm implements SampleNameDetectionAlgorithm {

    @Override
    public boolean detectable(File f) {
        return f.getName().endsWith(".bam");
    }

    @Override
    public String apply(File f) {
        if (!detectable(f)) {
            throw new IllegalArgumentException("Cannot detect sample name for file: " + f.getPath());
        }
        final SAMFileReader in = new SAMFileReader(f);
        try {
            final SAMFileHeader header = in.getFileHeader();
            final List<SAMReadGroupRecord> rgs = header.getReadGroups();
            assertFalse(rgs.isEmpty(), "Cannot determine sample name: missing read group information for file: " + f.getPath());
            assertFalse(rgs.size() > 1, "Cannot determine sample name: more than one read group detected for file: " + f.getPath());
            final String sample = rgs.get(0).getSample();
            assertFalse(sample == null, "Cannot determine sample name: missing sample name in read group for file: " + f.getPath());
            return sample;
        } finally {
            in.close();
        }
    }

    private void assertFalse(boolean b, String msg) {
        if (b) {
            throw new IllegalArgumentException(msg);
        }
    }
}
