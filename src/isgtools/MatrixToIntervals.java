/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.ISGMatrixReader;
import org.nau.isg.matrix.ISGMatrixRecord;
import java.io.File;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMSequenceRecord;

/**
 *
 * @author jbeckstrom
 */
public class MatrixToIntervals extends CommandLineProgram {

    @Usage
    public final String USAGE = "Converts a snp matrix to intervals.";
    @Option(doc = "Input matrix.")
    public File INPUT;
    @Option(doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE;
    @Option(doc = "Output interval file.")
    public File OUTPUT;
    @Option(doc = "Padding of flanking sequence.")
    public int PADDING = 100;

    @Override
    protected int doWork() {
        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsReadable(REFERENCE_SEQUENCE);
        IoUtil.assertFileIsWritable(OUTPUT);

        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
        assertDictionaryExists(ref);
        assertFastaIsIndexed(ref);
        
        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(ref.getSequenceDictionary());
        final IntervalList intervalList = new IntervalList(header);
        final ISGMatrixReader reader = openMatrixForReading(INPUT);
        ISGMatrixRecord record = null;

        while ((record = reader.nextRecord()) != null) {

            final SAMSequenceRecord seqRec = ref.getSequenceDictionary().getSequence(record.getChrom());
            final int start = getPaddedStart(record.getPos(), PADDING);
            final int end = getPaddedEnd(record.getPos(), PADDING, seqRec.getSequenceLength());

            final Interval interval = new Interval(record.getChrom(), start, end, false, record.getChrom()+"_"+record.getPos());
            intervalList.add(interval);
        }

        intervalList.sort();
        intervalList.write(OUTPUT);

        return 0;
    }
    
    

    private void assertDictionaryExists(ReferenceSequenceFile ref) {
        if (ref.getSequenceDictionary()==null) {
            throw new PicardException("Fasta file must have a dictionary");
        }
    }
    
    private void assertFastaIsIndexed(ReferenceSequenceFile ref) {
        if (!ref.isIndexed()) {
            throw new PicardException("Fasta file must be indexed");
        }
    }

    private ISGMatrixReader openMatrixForReading(File file) {
        try {
            return new ISGMatrixReader(file);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private int getPaddedStart(int start, int padding) {
        int ret = start - padding;
        if (ret < 1) {
            ret = 1;
        }
        return ret;
    }

    private int getPaddedEnd(int end, int padding, int length) {
        int ret = end + padding;
        if (ret > length) {
            ret = length;
        }
        return ret;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new MatrixToIntervals().instanceMainWithExit(args);
    }
}
