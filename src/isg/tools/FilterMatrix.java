 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.HeaderAttribute;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import isg.util.CompositeFilter;
import isg.util.Filter;
import java.io.File;
import java.io.IOException;
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
    @Option(doc = "File to write inclusive records", optional = false)
    public File INCLUSIVE_OUT;
    @Option(doc = "File to write exclusive records", optional = false)
    public File EXCLUSIVE_OUT;
    @Option(doc = "Minimum allowable mismatch distance. Matrix records with a mismatch "
            + "distance >= MINIMUM_MISMATCH will be written to the INCLUSIVE_OUT file all "
            + "other records (including records without a mismatch attribute) will be written"
            + "to the EXCLUSIVE_OUT file.", optional = true)
    public Integer MINIMUM_MISMATCH;
    @Option(doc = "Reference sequence file", optional = true)
    public File REFERENCE_SEQUENCE;
    @Option(doc = "Interval file to filter by. Matrix records that overlap an interval "
            + "from one or more FILTER files will be written to the INCLUSIVE_OUT file "
            + "all other records will be written to the EXCLUSIVE_OUT file.", optional = true)
    public List<File> FILTER;
    @Option(doc = "0-based index of chr column.", optional = true)
    public Integer CHR_COL = 0;
    @Option(doc = "0-based index of start column", optional = true)
    public Integer START_COL = 1;
    @Option(doc = "0-based index of end column", optional = true)
    public Integer END_COL = 2;
    @Option(doc = "Ignore lines that start with this", optional = true)
    public String COMMENT = "@";

    @Override
    protected int doWork() {

        VariantContextTabWriter includeWriter = openFileForWriting(INCLUSIVE_OUT);
        VariantContextTabWriter excludeWriter = openFileForWriting(EXCLUSIVE_OUT);

        try {

            VariantContextTabReader reader = new VariantContextTabReader(INPUT);

            includeWriter.writeHeader(reader.getHeader());
            excludeWriter.writeHeader(reader.getHeader());

            final Filter<VariantContext> vcFilter = createFilter();
            
            VariantContext record;
            while ((record = reader.nextRecord()) != null) {
                VariantContextTabWriter writer = vcFilter.pass(record) ? includeWriter : excludeWriter;
                writer.add(record);
            }

        } catch (Exception ex) {
            Logger.getLogger(FilterMatrix.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        } finally {
            includeWriter.close();
            excludeWriter.close();
        }
        return 0;
    }
    
    private Filter<VariantContext> createFilter() throws IOException{
        List<Filter<VariantContext>> filtersToAdd = new ArrayList<Filter<VariantContext>>();
        if(MINIMUM_MISMATCH!=null){
            filtersToAdd.add(new MismatchFilter(MINIMUM_MISMATCH));
        }
        if(FILTER!=null && !FILTER.isEmpty()){
            filtersToAdd.add(new OverlapFilter(createOverlapDetector()));
        }
        return new CompositeFilter(filtersToAdd);
    }

    private OverlapDetector<Interval> createOverlapDetector() throws IOException {
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        for (File f : FILTER) {
            CloseableTribbleIterator<TableFeature> iter = openTabularFileForReading(f).iterator();
            TableFeature currentLocus = null;
            while ((currentLocus = iter.next()) != null) {
                Interval i = new Interval(currentLocus.getChr(), currentLocus.getStart(), currentLocus.getEnd());
                overlapDetector.addLhs(i, i);
            }
            iter.close();
        }
        return overlapDetector;
    }

    private VariantContextTabWriter openFileForWriting(File f) {
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new IllegalStateException("An error occured opening file: " + f, ex);
        }

    }

    private AbstractFeatureReader<TableFeature> openTabularFileForReading(File f) {
        ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
        GenomeLocParser genomeLocParser = new GenomeLocParser(refSeqFile);
        TabularTableCodec codec = new TabularTableCodec(CHR_COL, START_COL, END_COL, COMMENT);
        codec.setGenomeLocParser(genomeLocParser);
        return AbstractFeatureReader.getFeatureReader(f.getAbsolutePath(), codec, false);
    }
    
    private static final class MismatchFilter implements Filter<VariantContext>{

        private final int min;
        
        public MismatchFilter(int min){
            this.min = min;
        }
        
        @Override
        public boolean pass(VariantContext vc) {
            if(vc.hasAttribute(HeaderAttribute.MISMATCH.getName())){
                Object value = vc.getAttribute(HeaderAttribute.MISMATCH.getName());
                if(Integer.valueOf((String)value) >= min){
                    return true;
                }
            }
            return false;
        }
        
    }
    
    private static final class OverlapFilter implements Filter<VariantContext>{

        private final OverlapDetector<Interval> overlapDetector;
        
        public OverlapFilter(OverlapDetector<Interval> overlapDetector){
            this.overlapDetector = overlapDetector;
        }
        
        @Override
        public boolean pass(VariantContext vc) {
            Interval i = new Interval(vc.getChr(), vc.getStart(), vc.getEnd());
            return !overlapDetector.getOverlaps(i).isEmpty();
        }
        
    }

    public static void main(String[] args) {
        System.exit(new FilterMatrix().instanceMain(args));
    }
}
