 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2;

import org.nau.isg.tools.snpclassifier.GenBankAnnotator;
import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;
import org.nau.isg2.util.Filter;
import org.nau.isg2.util.SkimmingIterator;
import org.nau.isg2.util.FilteringIterator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceDictionary;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.FeatureReader;
import org.broadinstitute.sting.utils.codecs.vcf.VCFCodec;
import org.broadinstitute.sting.utils.codecs.vcf.VCFConstants;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.broad.tribble.bed.BEDCodec;
import org.broad.tribble.bed.BEDFeature;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.nau.isg.matrix.VariantContextTabHeader;
import org.nau.isg.matrix.VariantContextTabWriter;
import org.nau.util.FileUtils;

/**
 *
 * @author jbeckstrom
 * ISG Process:
 * 1. Build Matrix of SNPs from input files
 * 2. Mark areas of no coverage
 * 3. Mark areas of ambiguous calls (if ambiguous calls file is supplied)
 * 4. Assume reference base at remaining uncalled position
 * 4. Print matrix
 */
public class ISG2 extends CommandLineProgram {

    @Usage(programVersion = "0.6")
    public String USAGE = "";
    @Option(doc = "Name of sample to include in analysis.", optional = false)
    public List<String> SAMPLE;
    @Option(doc = "Output matrix file.", optional = false)
    public File OUTPUT;
    @Option(doc = "Directory containing vcf files.", optional = false)
    public File VCF_DIR;
    @Option(doc = "Directory containing alignment coverage files.", optional = false)
    public File COV_DIR;
    @Option(doc = "Directory containing genbank files.", optional = false)
    public File GBK_DIR;
    @Option(doc = "Reference sequence.", optional = false)
    public File REF;
    @Option(doc = "The minimum allele frequency of an alternative base needed to call a SNP.", optional = false)
    public float MIN_AF = 0.5F;
    @Option(doc = "The minimum Phred scaled probability needed to call a SNP", optional = false)
    public float MIN_QUAL = 30;
    @Option(doc = "The minimum genotype quality needed to call a SNP.", optional = false)
    public float MIN_GQ = 4;
    @Option(doc = "The minimum depth of reads needed to call a SNP.", optional = false)
    public int MIN_DP = 3;
    private static final String AMBIGUOUS_CALL_STRING = "N";
    private static final Allele AMBIGUOUS_CALL = Allele.create(AMBIGUOUS_CALL_STRING);
    private static final Filter<VariantContext> SNP_INDEL_FILTER = new Filter<VariantContext>() {

        @Override
        public boolean pass(VariantContext t) {
            return t.getStart() == t.getEnd() && (t.isSNP() || t.isIndel());
        }
    };

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.home"));
//        File wd = new File("/Users/jbeckstrom/isgpipeline/geomyces");
//        ISG2 isg = new ISG2();
//        isg.SAMPLE = Arrays.asList("Gd000002", "Gd000003a", "Gd000079");
//        isg.COV_DIR = new File(wd, "bams");
//        isg.GBK_DIR = new File(wd, "genbank");
//        isg.REF = new File(wd, "ref.fasta");
//        isg.VCF_DIR = new File(wd, "vcf");
//        isg.OUTPUT = new File(wd, "isg_out.tab");
//        isg.doWork();
//        System.exit(new ISG2().instanceMain(args));
    }

    @Override
    protected int doWork() {

        ReferenceSequenceFile refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(REF);
        final Comparator<VariantContext> vcComparator = new VariantContextComparator(refSeq.getSequenceDictionary());

        final Set<String> keys = new HashSet<String>(SAMPLE);
        final Map<String, Iterator<VariantContext>> vcfIters = getVariantContextIters(VCF_DIR, keys);
        final SkimmingIterator<VariantContext> masterVcfIter = new SkimmingIterator(vcfIters, vcComparator);
        final Map<String, LociStateCaller> lociCallers = getCoverageFinders(COV_DIR, keys);
        final GenBankAnnotator snpClassifier = new GenBankAnnotator(GBK_DIR, REF);

        VariantContextTabHeader vcHeader = new VariantContextTabHeader(Collections.EMPTY_LIST, keys);
        vcHeader = snpClassifier.annotate(vcHeader);
        VariantContextTabWriter vcWriter = openFileForWriting(OUTPUT);
        vcWriter.writeHeader(vcHeader);

        long written = 0;
        System.out.println("processing...");
        while (masterVcfIter.hasNext()) {

            Map<String, VariantContext> vcMap = masterVcfIter.next();

            VariantContext vcTop = vcMap.values().iterator().next();

            final Allele ref = vcTop.getReference();
            //make calls
            List<Genotype> genotypes = new ArrayList<Genotype>();

            for (String key : keys) {
                VariantContext vc = vcMap.get(key);
                if (vc == null) { //no snp called so check for coverage
                    LociStateCaller lociCaller = lociCallers.get(key);
                    if (lociCaller == null) {
                        genotypes.add(GenotypeBuilder.create(key, Arrays.asList(Allele.NO_CALL)));
                    } else {
                        final CalledState calledState = lociCaller.call(vcTop.getChr(), vcTop.getStart());
                        switch (calledState) {
                            case CALLABLE:
                                genotypes.add(GenotypeBuilder.create(key, Arrays.asList(ref)));
                                break;
                            case NO_COVERAGE:
                                genotypes.add(GenotypeBuilder.create(key, Arrays.asList(Allele.NO_CALL)));
                                break;
                            case EXCESSIVE_COVERAGE:
                            case LOW_COVERAGE:
                            case POOR_MAPPING_QUALITY:
                            case REF_N:
                                genotypes.add(GenotypeBuilder.create(key, Arrays.asList(AMBIGUOUS_CALL)));
                                break;
                            default:
                                throw new IllegalStateException("Unknown called state: " + calledState);
                        }
                    }
                } else {
                    if (!vc.getReference().equals(ref)) {
                        throw new IllegalStateException("ref alleles don't match: " + vc.getReference() + " != " + ref + " " + vc);
                    }
                    //make call: ambiguous or snp
                    Allele a = call(vc);
                    if (a.basesMatch(ref)) {
                        a = ref;
                    }
                    genotypes.add(GenotypeBuilder.create(key, Arrays.asList(a)));
                }
            }

            final Set<Allele> alleles = new HashSet<Allele>();
            //populate alleles from genotypes
            alleles.add(ref);
            for (Genotype g : genotypes) {
                for (Allele a : g.getAlleles()) {
                    if (!a.basesMatch(Allele.NO_CALL)) {
                        alleles.add(a);
                    }
                }
            }

            VariantContextBuilder builder = new VariantContextBuilder("source", vcTop.getChr(), vcTop.getStart(), vcTop.getEnd(), alleles);
            builder.genotypes(genotypes);

            VariantContext vc = builder.make();

            //print snps to file
            if (isSNP(vc)) {
                vc = snpClassifier.annotate(vc);
                vcWriter.add(vc);
                if (++written % 100000 == 0) {
                    System.out.println("Written " + written + " records.");
                }
            }

        }

        vcWriter.close();

        return 0;
    }

    private VariantContextTabWriter openFileForWriting(File f) {
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new IllegalStateException("An error occurred opening file: " + f.getAbsolutePath(), ex);
        }
    }

    public static boolean isSNP(VariantContext vc) {
        for (Allele a : vc.getAlternateAlleles()) {
            if (!a.basesMatch(AMBIGUOUS_CALL_STRING) && !a.basesMatch(Allele.NO_CALL_STRING)) {
                return true;
            }
        }
        return false;
    }

    private Map<String, LociStateCaller> getCoverageFinders(File dir, Set<String> samplesToInclude) {
        final Map<String, LociStateCaller> cvgFinders = new HashMap<String, LociStateCaller>();
        final String extensions[] = {".bed", ".interval_list"};
        for (final String sample : samplesToInclude) {
            File f = FileUtils.findFileUsingExtensions(dir, sample, extensions);
            if (f == null) {
                System.out.println("WARNING: Could not find coverage file for sample: " + sample);
                continue;
            }
            try {
                LociStateCaller lociStateCaller = LociStateCallerFactory.createFromFile(f);
                cvgFinders.put(sample, lociStateCaller);
            } catch (IOException ex) {
                Logger.getLogger(ISG2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return cvgFinders;
    }

    private Map<String, Iterator<VariantContext>> getVariantContextIters(File dir, Set<String> samplesToInclude) {
        final Map<String, Iterator<VariantContext>> ret = new HashMap<String, Iterator<VariantContext>>();
        for (final String sample : samplesToInclude) {

            File f = new File(dir, sample + ".vcf");
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
            Iterator<VariantContext> iter = new FilteringIterator<VariantContext>(getIteratorQuietly(vcfReader), SNP_INDEL_FILTER);
            ret.put(sample, iter);

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

    /* call: ambiguous or snp */
    private Allele call(VariantContext vc) {
        if (vc.getAlternateAlleles().size() > 1
                || vc.getAlternateAllele(0).basesMatch("N")
                || (vc.hasLog10PError() && vc.getPhredScaledQual() < MIN_QUAL)
                || (vc.getGenotype(0).hasGQ() && vc.getGenotype(0).getGQ() < MIN_GQ)
                || (vc.hasAttribute(VCFConstants.DEPTH_KEY) && vc.getAttributeAsInt(VCFConstants.DEPTH_KEY, -1) < MIN_DP)
                || (vc.hasAttribute(VCFConstants.ALLELE_FREQUENCY_KEY) && vc.getAttributeAsDouble(VCFConstants.ALLELE_FREQUENCY_KEY, -1) <= MIN_AF)) {
            return AMBIGUOUS_CALL;
        }
        return vc.getAlternateAllele(0);
    }

    private static class VariantContextComparator implements Comparator<VariantContext> {

        private final SAMSequenceDictionary seqDict;

        /** Constructs a comparator using the supplied sequence header. */
        public VariantContextComparator(final SAMFileHeader header) {
            this(header.getSequenceDictionary());
        }

        public VariantContextComparator(final SAMSequenceDictionary seqDict) {
            this.seqDict = seqDict;
        }

        @Override
        public int compare(VariantContext lhs, VariantContext rhs) {
            final int lhsIndex = this.seqDict.getSequenceIndex(lhs.getChr());
            final int rhsIndex = this.seqDict.getSequenceIndex(rhs.getChr());
            int retval = lhsIndex - rhsIndex;

            if (retval == 0) {
                retval = lhs.getStart() - rhs.getStart();
            }
            if (retval == 0) {
                retval = lhs.getEnd() - rhs.getEnd();
            }
            return retval;
        }
    }

    private static class IntervalOverlapComparator implements Comparator<Interval> {

        private final SAMFileHeader header;

        /** Constructs a comparator using the supplied sequence header. */
        public IntervalOverlapComparator(final SAMFileHeader header) {
            this.header = header;
        }

        @Override
        public int compare(Interval lhs, Interval rhs) {
            final int lhsIndex = this.header.getSequenceIndex(lhs.getSequence());
            final int rhsIndex = this.header.getSequenceIndex(rhs.getSequence());
            int retval = lhsIndex - rhsIndex;

            if (retval == 0) {
                if (lhs.getEnd() < rhs.getStart()) {
                    return -1;
                } else if (lhs.getStart() > rhs.getEnd()) {
                    return 1;
                } else {
                    return 0;
                }
            }
            return retval;
        }
    }
}
