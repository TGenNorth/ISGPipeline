/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.ISGMatrixReader;
import org.nau.isg.matrix.ISGMatrixWriter;
import org.nau.isg.matrix.ISGMatrixHeader;
import org.nau.isg.matrix.ISGMatrixRecord;
import isgtools.util.ISGMatrixRecordUtils;
import isgtools.util.PatternBuilder;
import isgtools.util.PatternNumGenerator;
import isgtools.util.TabularTableCodec;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.StandardOptionDefinitions;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.OverlapDetector;
import org.apache.commons.io.FileUtils;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.tgen.commons.feature.Locus;
import org.tgen.commons.feature.TableFeature;
import org.tgen.commons.gff.GffReader;
import org.tgen.commons.gff.GffRecord;
import org.tgen.commons.io.BedFileReader;
import org.tgen.commons.io.TabularReader;
import org.tgen.commons.vcf.VCFReader;
/**
 *
 * @author jbeckstrom
 */
public class AppendMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Append table to ISG Matrix file.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;
    @Option(doc = "Tabular file to filter by", optional = false)
    public File TAB;
    @Option(shortName = StandardOptionDefinitions.REFERENCE_SHORT_NAME, doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE;
    @Option(doc = "Directory containing bam coverage files. Bam coverage files must follow "
    + "naming convention: [sample_name].gff", optional = false)
    public File BAM_COV_DIRECTORY;
    @Option(doc = "Directory containing ambiguous calls. Files must follow "
    + "naming convention: [sample_name].vcf", optional = true)
    public File AMBIGUOUS_DIRECTORY;
    @Option(doc = "0-based index of chr column.", optional = false)
    public Integer CHR_COL = 0;
    @Option(doc = "0-based index of start column", optional = false)
    public Integer START_COL = 1;
    @Option(doc = "0-based index of end column", optional = false)
    public Integer END_COL = 2;
    @Option(doc = "Append input table to ISG Matrix file", optional = false)
    public Boolean APPEND = true;
    @Option(doc = "Output prefix", optional = false)
    public String OUTPUT_PREFIX;
    private ReferenceSequenceFile ref;
    private Map<String, OverlapDetector<Object>> noCoverageMap = new HashMap<String, OverlapDetector<Object>>();
    private Map<String, OverlapDetector<Object>> ambiguousMap = new HashMap<String, OverlapDetector<Object>>();

    @Override
    protected int doWork() {



        try {

            ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
            populateNoCoverageMap();
            populateAmbiguousMap();

            ISGMatrixReader reader = new ISGMatrixReader(INPUT);
            ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
            GenomeLocParser genomeLocParser = new GenomeLocParser(refSeqFile);
            TabularTableCodec codec = new TabularTableCodec(CHR_COL, START_COL, END_COL);
            codec.setGenomeLocParser(genomeLocParser);
            AbstractFeatureReader<TableFeature> tabularReader = AbstractFeatureReader.getFeatureReader(TAB.getAbsolutePath(), codec, false);
            
            ISGMatrixWriter filteredWriter = new ISGMatrixWriter(new File(OUTPUT_PREFIX + ".filtered.tab"));
            ISGMatrixWriter excludeWriter = new ISGMatrixWriter(new File(OUTPUT_PREFIX + ".tab"));

            filteredWriter.writerHeader(ISGMatrixHeader.addAddtionalInfo(reader.getHeader(), (List<String>)tabularReader.getHeader()));
            excludeWriter.writerHeader(ISGMatrixHeader.addAddtionalInfo(reader.getHeader(), (List<String>)tabularReader.getHeader()));

            ISGMatrixRecord record = null;
            CloseableTribbleIterator<TableFeature> iter = tabularReader.iterator();
            TableFeature currentLocus = iter.next();
            TableFeature overlappingLocus = null;
            int count = 0;

            while ((record = reader.nextRecord()) != null) {

                boolean overlapFound = false;

                do {

                    if (currentLocus == null) {
                        //do nothing
                    } else if (!currentLocus.getChr().equalsIgnoreCase(record.getChrom())) {
                        break;
                    } else if (record.getPos() < currentLocus.getStart()) {
                        break;
                    } else if (record.getPos() > currentLocus.getEnd()) {
                        //do nothing
                    } else {
                        overlapFound = true;
                        count++;
                        if (APPEND) {
                            record = ISGMatrixRecordUtils.addAdditionalInfo((List<String>)tabularReader.getHeader(), currentLocus.getAllValues(), record);
                        }
                        overlappingLocus = currentLocus;
                        break;
                    }

                    if (overlappingLocus == null) {
                        ISGMatrixRecord r = createMatrixRecord(currentLocus, (List<String>)tabularReader.getHeader(), reader.getHeader().getSampleNames());
                        writeRecord(r, excludeWriter);

                    }
                    overlappingLocus = null;
                } while ((currentLocus = iter.next()) != null);

                if (!overlapFound) {
                    writeRecord(record, filteredWriter);
                } else {
                    writeRecord(record, excludeWriter);
                }

            }

            System.out.println(count + " rows removed!");

            filteredWriter.close();
            excludeWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            return 1;
        }
        return 0;
    }
    
    private void writeRecord(ISGMatrixRecord record, ISGMatrixWriter writer){
        ISGMatrixRecord ret = new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), record.getStates(), record.getAdditionalInfo());
        writer.addRecord(ret);
    }

    private ISGMatrixRecord createMatrixRecord(TableFeature locus, List<String> header, List<String> samples) {
        ISGMatrixRecord ret = createMatrixRecord(locus.getChr(), locus.getStart(), samples);
        return ISGMatrixRecordUtils.addAdditionalInfo(header, locus.getAllValues(), ret);
    }

    private ISGMatrixRecord createMatrixRecord(String chr, int pos, List<String> samples) {
        Interval interval = new Interval(chr, pos, pos);
        char ref = (char) this.ref.getSequence(chr).getBases()[pos - 1];
        List<Character> alleles = new ArrayList<Character>();
        for (String sample : samples) {
            if (sample.contains(".")) {
                sample = sample.substring(0, sample.lastIndexOf("."));
            }
            alleles.add(getBase(interval, sample, ref));
        }
        Map<String, String> addInfo = new HashMap<String, String>();
        return new ISGMatrixRecord(chr, pos, ref, alleles, addInfo);
    }

    private char getBase(Interval interval, String sample, char ref) {
        if (!isCovered(interval, sample)) {
            return '.';
        }
        if (isAmbiguous(interval, sample)) {
            return 'N';
        }
        return ref;
    }

    private boolean isCovered(Interval interval, String sample) {
        OverlapDetector<Object> overlapDetector = noCoverageMap.get(sample);
        if (overlapDetector == null) {
            throw new NullPointerException("Could not find coverage file for sample: " + sample);
        }
        return !overlapDetector.getOverlaps(interval).isEmpty();
    }

    private boolean isAmbiguous(Interval interval, String sample) {
        OverlapDetector<Object> overlapDetector = ambiguousMap.get(sample);
        if (overlapDetector == null) {
            throw new NullPointerException("Could not find ambiguous file for sample: " + sample);
        }
        return !overlapDetector.getOverlaps(interval).isEmpty();
    }

    private void populateNoCoverageMap() throws IOException, Exception {
        Collection<File> files = FileUtils.listFiles(BAM_COV_DIRECTORY, new String[]{"gff"}, false);
        for (File file : files) {
            String sample = org.tgen.commons.utils.FileUtils.getFilenameWithoutExtension(file);
            List<Interval> intervals = parseGff(file);
            List<Object> objects = new ArrayList<Object>(createEmptyList(intervals.size()));
            OverlapDetector<Object> overlapDetector = new OverlapDetector<Object>(0, 0);
            overlapDetector.addAll(objects, intervals);
            noCoverageMap.put(sample, overlapDetector);
        }
    }

    private void populateAmbiguousMap() throws IOException, Exception {
        Collection<File> files = FileUtils.listFiles(AMBIGUOUS_DIRECTORY, new String[]{"vcf"}, false);
        for (File file : files) {
            String sample = org.tgen.commons.utils.FileUtils.getFilenameWithoutExtension(file);
            List<Interval> intervals = parseVCF(file);
            List<Object> objects = new ArrayList<Object>(createEmptyList(intervals.size()));
            OverlapDetector<Object> overlapDetector = new OverlapDetector<Object>(0, 0);
            overlapDetector.addAll(objects, intervals);
            ambiguousMap.put(sample, overlapDetector);
        }
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
        System.exit(new AppendMatrix().instanceMain(args));
    }
    
}
