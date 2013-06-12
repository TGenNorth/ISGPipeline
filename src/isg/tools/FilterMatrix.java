 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.OverlapDetector;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.codecs.table.TableFeature;
import org.broadinstitute.variant.variantcontext.VariantContext;
import util.TabularTableCodec;

/**
 *
 * @author jbeckstrom
 */
public class FilterMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Filter ISG Matrix file.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;
    @Option(doc = "Tabular file to filter by", optional = false)
    public File FILTER;
    @Option(doc = "File to write inclusive records", optional = false)
    public File INCLUSIVE_OUT;
    @Option(doc = "File to write exclusive records", optional = false)
    public File EXCLUSIVE_OUT;
    @Option(doc = "Reference sequence file", optional = false)
    public File REFERENCE_SEQUENCE;// = new File("test/data/ref.fasta");
    @Option(doc = "0-based index of chr column.", optional = false)
    public Integer CHR_COL = 0;
    @Option(doc = "0-based index of start column", optional = false)
    public Integer START_COL = 1;
    @Option(doc = "0-based index of end column", optional = false)
    public Integer END_COL = 2;
    @Option(doc = "Ignore lines that start with this", optional = false)
    public String COMMENT = "@";

    @Override
    protected int doWork() {

        VariantContextTabWriter includeWriter = openFileForWriting(INCLUSIVE_OUT);
        VariantContextTabWriter excludeWriter = openFileForWriting(EXCLUSIVE_OUT);

        try {

            VariantContextTabReader reader = new VariantContextTabReader(INPUT);

            includeWriter.writeHeader(reader.getHeader());
            excludeWriter.writeHeader(reader.getHeader());

            final OverlapDetector<Interval> overlapDetector = createOverlapDetector();
            VariantContext record;
            while ((record = reader.nextRecord()) != null) {
                Interval i = new Interval(record.getChr(), record.getStart(), record.getEnd());
                boolean overlapFound = !overlapDetector.getOverlaps(i).isEmpty();
                VariantContextTabWriter writer = overlapFound ? includeWriter : excludeWriter;
                writer.add(record);
            }

        } catch (Exception ex) {
            Logger.getLogger(FilterMatrix.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }finally{
            includeWriter.close();
            excludeWriter.close();
        }
        return 0;
    }

    private OverlapDetector<Interval> createOverlapDetector() throws IOException {
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        CloseableTribbleIterator<TableFeature> iter = openTabularFileForReading().iterator();
        TableFeature currentLocus = null;
        while ((currentLocus = iter.next()) != null) {
            Interval i = new Interval(currentLocus.getChr(), currentLocus.getStart(), currentLocus.getEnd());
            overlapDetector.addLhs(i, i);
        }
        iter.close();
        return overlapDetector;
    }
    
    private VariantContextTabWriter openFileForWriting(File f){
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new IllegalStateException("An error occured opening file: "+f, ex);
        }

    }

    private AbstractFeatureReader<TableFeature> openTabularFileForReading() {
        ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
        GenomeLocParser genomeLocParser = new GenomeLocParser(refSeqFile);
        TabularTableCodec codec = new TabularTableCodec(CHR_COL, START_COL, END_COL, COMMENT);
        codec.setGenomeLocParser(genomeLocParser);
        return AbstractFeatureReader.getFeatureReader(FILTER.getAbsolutePath(), codec, false);
    }

    public static void main(String[] args) {
        System.exit(new FilterMatrix().instanceMain(args));
    }
}
