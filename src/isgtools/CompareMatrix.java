 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.VariantContextTabReader;
import org.nau.isg.matrix.VariantContextTabWriter;
import isgtools.util.SkimmingIterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.SAMFileHeader;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

/**
 * compare two matrix files to find what is similar and what is different.
 * @author jbeckstrom
 */
public class CompareMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Compare two matrix files for similarities and differences.";
    @Option(doc = "First input ISG Matrix file", optional = false)
    public File MATRIX1;
    @Option(doc = "Second input ISG Matrix file", optional = false)
    public File MATRIX2;
    @Option(doc = "Output ISG Matrix file of MATRIX1 differences", optional = false)
    public File MATRIX1_DIFF;
    @Option(doc = "Output ISG Matrix file of MATRIX2 differences", optional = false)
    public File MATRIX2_DIFF;
    @Option(doc = "Output ISG Matrix file of similarities between MATRIX1 and MATRIX2", optional = false)
    public File MATRIX_SHARED;
    @Option(doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE;

    @Override
    protected int doWork() {


        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(ref.getSequenceDictionary());

        VariantContextTabReader reader1 = openFileForReading(MATRIX1);
        VariantContextTabReader reader2 = openFileForReading(MATRIX2);
        
        VariantContextTabWriter writer1 = createWriter(MATRIX1_DIFF);
        VariantContextTabWriter writer2 = createWriter(MATRIX2_DIFF);
        VariantContextTabWriter sharedWriter = createWriter(MATRIX_SHARED);
        
        writer1.writeHeader(reader1.getHeader());
        writer2.writeHeader(reader2.getHeader());
        sharedWriter.writeHeader(reader1.getHeader());
        
        List<Integer> keys = Arrays.asList(0, 1);
        List<Iterator<VariantContext>> iters = new ArrayList<Iterator<VariantContext>>();
        iters.add(new ISGMatrixIterator(reader1));
        iters.add(new ISGMatrixIterator(reader2));
        List<VariantContextTabWriter> writers = Arrays.asList(writer1, writer2);

        SkimmingIterator<Integer, VariantContext> iter = new SkimmingIterator<Integer, VariantContext>(keys, iters, new ISGMatrixRecordComparator(header));

        while (iter.hasNext()) {
            Map<Integer, VariantContext> map = iter.next();
            if (map.size() == 2) {
                //shared
                sharedWriter.add(map.values().iterator().next());
            } else if (map.size() == 1) {
                Entry<Integer, VariantContext> entry = map.entrySet().iterator().next();
                writers.get(entry.getKey()).add(entry.getValue());
            } else {
                //should never get here
                throw new IllegalStateException("Unexpected result: " + map.keySet());
            }
        }

        for (VariantContextTabWriter writer : writers) {
            writer.close();
        }
        sharedWriter.close();

        return 0;
    }

    private VariantContextTabReader openFileForReading(File f) {
        try {
            return new VariantContextTabReader(f);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("Could not find file: " + f.getAbsolutePath(), ex);
        } catch (IOException ex) {
            throw new IllegalStateException("Error reading file: " + f.getAbsolutePath(), ex);
        }
    }

    private VariantContextTabWriter createWriter(File f) {
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new PicardException("Error writting matrix file: " + f.getAbsolutePath(), ex);
        }
    }

    private static class ISGMatrixIterator implements Iterator<VariantContext> {

        private final VariantContextTabReader reader;
        private VariantContext nextElement;

        public ISGMatrixIterator(VariantContextTabReader reader) {
            this.reader = reader;
            nextElement = reader.nextRecord();
        }

        @Override
        public boolean hasNext() {
            return (nextElement != null);
        }

        @Override
        public VariantContext next() {
            VariantContext ret = nextElement;
            nextElement = reader.nextRecord();
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    private static class ISGMatrixRecordComparator implements Comparator<VariantContext> {

        private final SAMFileHeader header;

        /** Constructs a comparator using the supplied sequence header. */
        public ISGMatrixRecordComparator(final SAMFileHeader header) {
            this.header = header;
        }

        @Override
        public int compare(VariantContext lhs, VariantContext rhs) {
            final int lhsIndex = this.header.getSequenceIndex(lhs.getChr());
            final int rhsIndex = this.header.getSequenceIndex(rhs.getChr());
            int retval = lhsIndex - rhsIndex;

            if (retval == 0) {
                retval = lhs.getStart() - rhs.getStart();
            }

            return retval;
        }
    }

    public static void main(String[] args) {
        System.exit(new CompareMatrix().instanceMain(args));
    }
}
