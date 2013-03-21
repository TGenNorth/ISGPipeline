 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.VariantContextTabHeader;
import org.nau.isg.matrix.VariantContextTabReader;
import org.nau.isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.util.Interval;
import net.sf.picard.util.OverlapDetector;
import net.sf.samtools.util.SequenceUtil;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;
import org.tgen.commons.mummer.delta.DeltaAlignment;
import org.tgen.commons.mummer.delta.DeltaFileReader;

/**
 * Translates a matrix from one reference to another.
 * @author jbeckstrom
 */
public class TranslateMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Translates a matrix from one reference to another.";
    @Option(doc = "ISG Matrix file to translate", optional = false)
    public File INPUT;// = new File("test/data/isg_out.tab");
    @Option(doc = "Output translated ISG Matrix file", optional = false)
    public File OUTPUT;// = new File("test/data/isg_out.translated.tab");
    @Option(doc = "delta file in terms of input matrix file", optional = false)
    public File DELTA;// = new File("test/data/ref_MSHR1043.filter");
//    @Option(doc = "query sequence fasta file.")
//    public File QUERY_SEQUENCE;// = new File("test/data/ref.fasta");
    @Option(doc = "name of sample used as input", optional = false)
    public String SAMPLE_NAME;// = "1043";

    @Override
    protected int doWork() {


//        final ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(QUERY_SEQUENCE);
//        final SAMFileHeader header = new SAMFileHeader();
//        header.setSequenceDictionary(refSeqFile.getSequenceDictionary());

        OverlapDetector<DeltaAlignment> overlapDetector = createOverlapDetector();

//        ReferenceSequenceHelper refSeqHelper = new ReferenceSequenceHelper(refSeqFile);
        VariantContextTabReader reader = createReader(INPUT);
        VariantContextTabWriter writer = createWriter(OUTPUT);

        VariantContextTabHeader vcHeader = reader.getHeader();
        vcHeader = vcHeader.addAttribute(String.format("%s_chr", SAMPLE_NAME));
        vcHeader = vcHeader.addAttribute(String.format("%s_pos", SAMPLE_NAME));
        writer.writeHeader(vcHeader);

        VariantContext record = null;
        long count = 0;
        while ((record = reader.nextRecord()) != null) {
            Interval rhs = new Interval(record.getChr(), record.getStart(), record.getEnd());
            Collection<DeltaAlignment> overlaps = overlapDetector.getOverlaps(rhs);
            if (!overlaps.isEmpty()) {
                Iterator<DeltaAlignment> iter = overlaps.iterator();
                DeltaAlignment deltaAlgnmt = iter.next();
                final int pos = deltaAlgnmt.translate(record.getStart());
                final String qry = deltaAlgnmt.getQryName();
                boolean paralog = false;

                while (iter.hasNext()) {
                    deltaAlgnmt = iter.next();
                    int p = deltaAlgnmt.translate(record.getStart());
                    String q = deltaAlgnmt.getQryName();
                    if (p != pos || !qry.equals(q)) {
                        //paralog
                        paralog = true;
                        break;
                    }
                }

                if (paralog) {
                    //paralog
                } else if (pos == -1) {
                    //indel
                } else {
                    VariantContextBuilder vcBldr = new VariantContextBuilder(record);
                    vcBldr.attribute(SAMPLE_NAME + "_chr", qry);
                    vcBldr.attribute(SAMPLE_NAME + "_pos", pos);
                    record = vcBldr.make();
                }

//                if (pos == -1) {
//                    //indel, cannot translate
//                    continue;
//                }
//                byte[] ref = refSeqHelper.query(deltaAlgnmt.getQryName(), pos-1); //{seqBases[pos - 1]};
//                if (!Allele.acceptableAlleleBases(ref)) {
//                    System.out.printf("Illegal base [%c] seen in reference\n", ref[0]);
//                    continue;
//                }
//
//                VariantContext vc = translate(record, deltaAlgnmt.getQryName(), ref, pos, deltaAlgnmt.isReverse());
//                writer.add(vc);
//                count++;
//                if (count % 10000 == 0) {
//                    System.out.println(count + " records written");
//                }
            }
            writer.add(record);
            count++;
            if (count % 100000 == 0) {
                System.out.println(count + " records written");
            }
        }
        writer.close();

        return 0;
    }

    private VariantContext translate(VariantContext vc, String chr, byte[] ref, int pos, boolean reverse) {
        if (!Allele.acceptableAlleleBases(ref)) {
            throw new IllegalArgumentException(
                    String.format("Illegal base [%c] seen in reference\n", ref[0]));
        }
        Allele refAllele = Allele.create(ref, true);

        VariantContextBuilder vcBldr = new VariantContextBuilder();
        List<Allele> alleles;
        List<Genotype> genotypes;
        if (reverse) {
            alleles = complimentAlleles(vc.getAlleles(), refAllele);
            genotypes = complimentGenotypes(vc.getGenotypes(), refAllele);
        } else {
            alleles = fixAlleles(vc.getAlleles(), refAllele);
            genotypes = fixGenotypes(vc.getGenotypes(), refAllele);
        }

        if (!alleles.contains(refAllele)) {
            alleles.add(refAllele);
        }

        vcBldr.chr(chr);
        vcBldr.start(pos);
        vcBldr.stop(pos);
        vcBldr.attributes(vc.getAttributes());
        vcBldr.alleles(alleles);
        vcBldr.genotypes(genotypes);

//        Genotype g = vc.getGenotype("MSHR1655");
//        if (!refAllele.equals(g.getAlleles().get(0), false)) {
//            if (!record.getReference().basesMatch(g.getAlleles().get(0))
//                    && !g.getAlleles().get(0).basesMatch("N")) {
//                System.out.println(deltaAlgnmt.isReverse());
//                System.out.printf("pos: %d\n", record.getStart());
//                System.out.printf("ref: %s\n", record.getReference().getBaseString());
//                System.out.printf("%s:%d-%d  %s:%d-%d\n",
//                        deltaAlgnmt.getRefName(),
//                        deltaAlgnmt.getRefStart(),
//                        deltaAlgnmt.getRefEnd(),
//                        deltaAlgnmt.getQryName(),
//                        deltaAlgnmt.getQryStart(),
//                        deltaAlgnmt.getQryEnd());
//                System.out.println(deltaAlgnmt.getRefMatches());
//                System.out.println(deltaAlgnmt.getQryMatches());
//
//                System.out.printf("validation error: %s != %s\n", refAllele, g.getAlleles().get(0));
//            }
//        }

        return vcBldr.make();
    }

    private List<Genotype> fixGenotypes(List<Genotype> genotypes, Allele ref) {
        List<Genotype> ret = new ArrayList<Genotype>(genotypes.size());
        for (Genotype g : genotypes) {
            List<Allele> alleles = fixAlleles(g.getAlleles(), ref);
            ret.add(GenotypeBuilder.create(g.getSampleName(), alleles));
        }
        return ret;
    }

    private List<Allele> fixAlleles(List<Allele> alleles, Allele ref) {
        List<Allele> ret = new ArrayList<Allele>(alleles.size());
        for (Allele a : alleles) {
            if (a.basesMatch(ref)) {
                ret.add(ref);
            } else if (a.isNoCall()) {
                ret.add(a);
            } else {
                ret.add(Allele.create(a.getBases()));
            }
        }
        return ret;
    }

    private List<Genotype> complimentGenotypes(List<Genotype> genotypes, Allele ref) {
        List<Genotype> ret = new ArrayList<Genotype>(genotypes.size());
        for (Genotype g : genotypes) {
            List<Allele> alleles = complimentAlleles(g.getAlleles(), ref);
            ret.add(GenotypeBuilder.create(g.getSampleName(), alleles));
        }
        return ret;
    }

    private List<Allele> complimentAlleles(List<Allele> alleles, Allele ref) {
        List<Allele> ret = new ArrayList<Allele>(alleles.size());
        for (Allele a : alleles) {
            String baseStr = SequenceUtil.reverseComplement(a.getBaseString());
            if (ref.basesMatch(baseStr)) {
                ret.add(ref);
            } else {
                ret.add(Allele.create(baseStr));
            }
        }
        return ret;
    }

    private DeltaFileReader createDeltaFileReader() {
        try {
            return new DeltaFileReader(DELTA);
        } catch (FileNotFoundException ex) {
            throw new PicardException("An error occured trying to read file: " + DELTA.getAbsolutePath(), ex);
        }
    }

    private OverlapDetector<DeltaAlignment> createOverlapDetector() {
        OverlapDetector<DeltaAlignment> overlapDetector = new OverlapDetector<DeltaAlignment>(0, 0);
        DeltaFileReader reader = createDeltaFileReader();
        DeltaAlignment deltaAlgnmt = null;
        while ((deltaAlgnmt = reader.nextAlignment()) != null) {
            Interval intvl = new Interval(deltaAlgnmt.getRefName(), deltaAlgnmt.getRefStart(), deltaAlgnmt.getRefEnd());
            overlapDetector.addLhs(deltaAlgnmt, intvl);
        }
        return overlapDetector;
    }

    private VariantContextTabReader createReader(File f) {
        try {
            return new VariantContextTabReader(f);
        } catch (FileNotFoundException ex) {
            throw new PicardException("Could not find file: " + f.getAbsolutePath(), ex);
        } catch (IOException ex) {
            throw new PicardException("Error reading matrix file: " + f.getAbsolutePath(), ex);
        }
    }

    private VariantContextTabWriter createWriter(File f) {
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new PicardException("Error writting matrix file: " + f.getAbsolutePath(), ex);
        }
    }

    private static class ReferenceSequenceHelper {

        private final ReferenceSequenceFile refSeqFile;
        private final Map<String, byte[]> sequenceMap = new HashMap<String, byte[]>();

        public ReferenceSequenceHelper(ReferenceSequenceFile refSeqFile) {
            this.refSeqFile = refSeqFile;
        }

        public byte[] getBases(String chrom) {
            byte[] seqBases = sequenceMap.get(chrom);
            if (seqBases == null) {
                seqBases = refSeqFile.getSequence(chrom).getBases();
                sequenceMap.put(chrom, seqBases);
            }
            return seqBases;
        }

        public byte[] query(String chrom, int index) {
            byte[] bases = getBases(chrom);
            byte[] ret = {bases[index]};
            return ret;
        }
    }

    public static void main(String[] args) {
        System.exit(new TranslateMatrix().instanceMain(args));
    }
}
