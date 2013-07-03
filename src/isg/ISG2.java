 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabWriter;
import isg.util.Algorithm;
import isg.util.AlgorithmApplyingIterator;
import isg.util.Filter;
import isg.util.FilteringIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.SAMSequenceDictionary;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;
import util.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class ISG2 extends CommandLineProgram {

    @Usage(programVersion = "0.6")
    public String USAGE = "";
    @Option(doc = "Name of sample to include in analysis.", optional = false)
    public List<String> SAMPLE;
    @Option(doc = "Output directory.", optional = false)
    public File OUT_DIR;
    @Option(doc = "Directory containing vcf files.", optional = false)
    public File VCF_DIR;
    @Option(doc = "Directory containing alignment coverage files.", optional = false)
    public File COV_DIR;
    @Option(doc = "Reference sequence.", optional = false)
    public File REF;
    @Option(doc = "The ploidy of the genome", optional = false)
    public int PLOIDY = 1;
    @Option(doc = "The minimum allele frequency of an alternative base needed to call a SNP.", optional = false)
    public float MIN_AF = 1.0F;
    @Option(doc = "The minimum Phred scaled probability needed to call a SNP", optional = false)
    public int MIN_QUAL = 30;
    @Option(doc = "The minimum genotype quality needed to call a SNP.", optional = false)
    public int MIN_GQ = 4;
    @Option(doc = "The minimum depth of reads needed to call a SNP.", optional = false)
    public int MIN_DP = 3;
    @Option(doc = "Process indels.", optional = false)
    public boolean INDEL = false;
    public static final String ALL_VARIANTS_FILENAME = "all.variants.txt";
    private SAMSequenceDictionary dict;
    private static final String AMBIGUOUS_CALL_STRING = "N";
    private static final Allele AMBIGUOUS_CALL = Allele.create(AMBIGUOUS_CALL_STRING);
    private static final Filter<VariantContext> SNP_INDEL_FILTER = new Filter<VariantContext>() {

        @Override
        public boolean pass(VariantContext t) {
            return t.isSNP() || t.isIndel();
        }
    };
    private static final Filter<VariantContext> SNP_FILTER = new Filter<VariantContext>() {

        @Override
        public boolean pass(VariantContext t) {
            return t.isSNP();
        }
    };
    private static final Filter<VariantContext> UNAMBIGUOUS_FILTER = new Filter<VariantContext>() {

        @Override
        public boolean pass(VariantContext vc) {
            for (Allele a : vc.getAlternateAlleles()) {
                if (!a.basesMatch(AMBIGUOUS_CALL_STRING) && !a.basesMatch(Allele.NO_CALL_STRING)) {
                    return true;
                }
            }
            return false;
        }
    };

    public static void main(String[] args) {
        System.exit(new ISG2().instanceMain(args));
    }

    @Override
    protected int doWork() {

        ReferenceSequenceFile refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(REF);
        dict = refSeq.getSequenceDictionary();
        final Set<String> keys = new HashSet<String>(SAMPLE);

        writeToFile(
                filterUnambiguousSNPs(
                genotypeNoCalls(
                mergeSNPs(
                fixPloidy(
                markAmbiguous(
                filterSNPs(
                createVCFIters(keys))))))));

        return 0;
    }

    private List<Iterator<VariantContext>> filterSNPs(List<Iterator<VariantContext>> iters) {
        return applyFilter(iters, INDEL ? SNP_INDEL_FILTER : SNP_FILTER);
    }

    private List<Iterator<VariantContext>> markAmbiguous(List<Iterator<VariantContext>> iters) {
        return applyAlgorithm(iters, new MarkAmbiguous(createMarkAmbiguousInfo()));
    }

    private List<Iterator<VariantContext>> fixPloidy(List<Iterator<VariantContext>> iters) {
        return applyAlgorithm(iters, new FixPloidy(PLOIDY));
    }

    private Iterator<VariantContext> mergeSNPs(List<Iterator<VariantContext>> iters) {
        return new MergingVariantContextIterator(iters, dict);
    }

    private Iterator<VariantContext> genotypeNoCalls(Iterator<VariantContext> iter) {
        return new AlgorithmApplyingIterator<VariantContext, VariantContext>(iter, new GenotypeNoCalls(createGenotypers(new HashSet<String>(SAMPLE))));
    }

    private Iterator<VariantContext> filterUnambiguousSNPs(Iterator<VariantContext> iter) {
        return new FilteringIterator<VariantContext>(iter, UNAMBIGUOUS_FILTER);
    }

    private void writeToFile(Iterator<VariantContext> iter) {
        final VariantContextTabHeader vcHeader = new VariantContextTabHeader(Collections.EMPTY_LIST, new HashSet<String>(SAMPLE));
        final VariantContextTabWriter allWriter = openFileForWriting(new File(OUT_DIR, ALL_VARIANTS_FILENAME));

        //write header
        allWriter.writeHeader(vcHeader);

        long written = 0;
        System.out.println("processing...");
        while (iter.hasNext()) {
            VariantContext vc = iter.next();
            allWriter.add(vc);
            if (++written % 100000 == 0) {
                System.out.println("Written " + written + " records.");
            }
        }
        
        //close writers
        allWriter.close();
    }

    /**
     * A utility method that wraps each iterator inside a filtering iterator 
     * which applies a filter to each element returned by calling next().
     * 
     * @param iters to apply filter to
     * @param filter to apply to iterators
     * @return List of iterators with filtering applied.
     */
    private List<Iterator<VariantContext>> applyFilter(List<Iterator<VariantContext>> iters, Filter filter) {
        List<Iterator<VariantContext>> ret = new ArrayList<Iterator<VariantContext>>();
        for (final Iterator<VariantContext> iter : iters) {
            ret.add(new FilteringIterator<VariantContext>(iter, filter));
        }
        return ret;
    }

    /**
     * A utility method that wraps each iterator inside an algorithm iterator 
     * that applies the algorithm to each element returned by calling next().
     * 
     * @param iters to apply algorithm to
     * @param algo to apply to iterators
     * @return List of iterators with algorithm applied.
     */
    private List<Iterator<VariantContext>> applyAlgorithm(List<Iterator<VariantContext>> iters, Algorithm algo) {
        List<Iterator<VariantContext>> ret = new ArrayList<Iterator<VariantContext>>();
        for (final Iterator<VariantContext> iter : iters) {
            ret.add(new AlgorithmApplyingIterator<VariantContext, VariantContext>(iter, algo));
        }
        return ret;
    }

    private MarkAmbiguousInfo createMarkAmbiguousInfo() {
        return new MarkAmbiguousInfo.Builder().maxNumAlt(PLOIDY).minAF(MIN_AF).minDP(MIN_DP).minGQ(MIN_GQ).minQual(MIN_QUAL).build();
    }

    private VariantContextTabWriter openFileForWriting(File f) {
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new IllegalStateException("An error occurred opening file: " + f.getAbsolutePath(), ex);
        }
    }

    private List<SingleSampleGenotyper> createGenotypers(Set<String> samplesToInclude) {
        final List<SingleSampleGenotyper> ret = new ArrayList<SingleSampleGenotyper>();
        final String extensions[] = {".bed", ".interval_list"};
        for (final String sample : samplesToInclude) {
            File f = FileUtils.findFileUsingExtensions(COV_DIR, sample, extensions);
            if(f==null){
                Logger.getLogger(ISG2.class.getName()).log(Level.WARNING, "Could not find coverage file for sample ''{0}''", sample);
            }
            try {
                LociStateCaller lociStateCaller = (f==null ? LociStateCallerFactory.createEmptyStateCaller() : LociStateCallerFactory.createFromFile(f));
                SingleSampleGenotyper ssg = new SingleSampleGenotyperImpl(sample, lociStateCaller);
                ret.add(ssg);
            } catch (IOException ex) {
                Logger.getLogger(ISG2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    private List<Iterator<VariantContext>> createVCFIters(Set<String> samplesToInclude) {
        final List<Iterator<VariantContext>> ret = new ArrayList<Iterator<VariantContext>>();
        for (final String sample : samplesToInclude) {

            File f = new File(VCF_DIR, sample + ".vcf");
            if (!f.exists()) {
                throw new IllegalStateException("Could not find file: " + f.getAbsolutePath());
            }

            final FeatureReader<VariantContext> vcfReader = createVCFReader(f);
            final VCFHeader header = (VCFHeader) vcfReader.getHeader();

            List<String> samples = header.getGenotypeSamples();
            if (samples.size() > 1) {
                throw new IllegalStateException("multiple genotype samples per vcf file is not supported: " + f.getAbsolutePath());
            } else if (samples.isEmpty()) {
                throw new IllegalStateException("vcf file doesn't have any genotype samples: " + f.getAbsolutePath());
            }
            Iterator<VariantContext> iter = getIteratorQuietly(vcfReader);
            ret.add(iter);

        }
        return ret;
    }

    private Iterator<VariantContext> getIteratorQuietly(FeatureReader<VariantContext> vcfReader) {
        try {
            return vcfReader.iterator();
        } catch (IOException ex) {
            throw new PicardException("An error occured trying to create iterator", ex);
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
