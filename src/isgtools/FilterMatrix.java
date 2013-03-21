 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.ISGMatrixReader;
import org.nau.isg.matrix.ISGMatrixWriter;
import org.nau.isg.matrix.ISGMatrixRecord;
import isgtools.util.TabularTableCodec;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.tgen.commons.gff.GffReader;
import org.tgen.commons.gff.GffRecord;
import org.tgen.commons.vcf.VCFReader;

/**
 *
 * @author jbeckstrom
 */
public class FilterMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Filter ISG Matrix file.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/data/isg_out.tab");
    @Option(doc = "Tabular file to filter by", optional = false)
    public File TAB;// = new File("test/data/ref.interval_list");
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
    @Option(doc = "Output prefix", optional = false)
    public String OUTPUT_PREFIX;// = "test/data/isg_out.dups";
    @Option(doc = "Type of filter to apply", optional = false)
    public FilterType FILTER_TYPE = FilterType.INCLUSIVE;

    public enum FilterType {

        INCLUSIVE, EXCLUSIVE;
    }

    @Override
    protected int doWork() {



        try {

            ISGMatrixReader reader = new ISGMatrixReader(INPUT);
            ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
            GenomeLocParser genomeLocParser = new GenomeLocParser(refSeqFile);
            TabularTableCodec codec = new TabularTableCodec(CHR_COL, START_COL, END_COL, COMMENT);
            codec.setGenomeLocParser(genomeLocParser);
            AbstractFeatureReader<TableFeature> tabularReader = AbstractFeatureReader.getFeatureReader(TAB.getAbsolutePath(), codec, false);
            
            ISGMatrixWriter includeWriter = new ISGMatrixWriter(getIncludeFile());
            ISGMatrixWriter excludeWriter = new ISGMatrixWriter(getExcludeFile());

            includeWriter.writerHeader(reader.getHeader());
            excludeWriter.writerHeader(reader.getHeader());

            OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
            ISGMatrixRecord record = null;
            CloseableTribbleIterator<TableFeature> iter = tabularReader.iterator();
            TableFeature currentLocus = null;
            while ((currentLocus = iter.next()) != null) {
                Interval i = new Interval(currentLocus.getChr(), currentLocus.getStart(), currentLocus.getEnd());
                overlapDetector.addLhs(i, i);
            }

            int count = 0;

            while ((record = reader.nextRecord()) != null) {

                Interval i = new Interval(record.getChrom(), record.getPos(), record.getPos());
                boolean overlapFound = !overlapDetector.getOverlaps(i).isEmpty();

                if (overlapFound) {
                    writeRecord(record, includeWriter);
                } else {
                    writeRecord(record, excludeWriter);
                }

            }

            includeWriter.close();
            excludeWriter.close();

        } catch (Exception ex) {
            Logger.getLogger(FilterMatrix.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }

    private File getIncludeFile() {
        switch (FILTER_TYPE) {
            case INCLUSIVE:
                return new File(OUTPUT_PREFIX + ".filt.tab");
            default:
                return new File(OUTPUT_PREFIX + ".unfilt.tab");

        }

    }
    
    private File getExcludeFile() {
        switch (FILTER_TYPE) {
            case EXCLUSIVE:
                return new File(OUTPUT_PREFIX + ".filt.tab");
            default:
                return new File(OUTPUT_PREFIX + ".unfilt.tab");

        }

    }

    private void writeRecord(ISGMatrixRecord record, ISGMatrixWriter writer) {
        ISGMatrixRecord ret = new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), record.getStates(), record.getAdditionalInfo());
        writer.addRecord(ret);
    }

    private List<Interval> parseGff(File file) throws Exception {
        List<Interval> ret = new ArrayList<Interval>();
        GffReader reader = new GffReader(file);
        GffRecord record = null;
        while ((record = reader.nextRecord()) != null) {
            String chr = record.getAttribute("i");
            int start = record.getStart();
            int end = record.getEnd();
            ret.add(new Interval(chr, start, end));
        }
        return ret;
    }

    private List<Interval> parseVCF(File file) throws Exception {
        List<Interval> ret = new ArrayList<Interval>();
        VCFReader reader = new VCFReader(file);
        VariantContext vc = null;
        while ((vc = reader.next()) != null) {
            ret.add(new Interval(vc.getChr(), vc.getStart(), vc.getEnd()));
        }
        return ret;
    }

    private List<Object> createEmptyList(int size) {
        List<Object> ret = new ArrayList<Object>();
        for (int i = 0; i < size; i++) {
            ret.add(null);
        }
        return ret;
    }

    public static void main(String[] args) {
        System.exit(new FilterMatrix().instanceMain(args));
    }
}
