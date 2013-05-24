/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.picard.PicardException;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.util.SequenceUtil;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broadinstitute.sting.utils.codecs.vcf.VCFConstants;
import org.broadinstitute.sting.utils.codecs.vcf.VCFFormatHeaderLine;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeaderLine;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeaderLineType;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;
import org.broadinstitute.sting.utils.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.sting.utils.variantcontext.writer.VariantContextWriterFactory;
import org.nau.isg2.util.Filter;
import org.nau.isg2.util.FilteringIterator;
import org.tgen.commons.mummer.snp.MumSNPCodec;
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 *
 * Find the common snps between two mummer snp files
 * @author jbeckstrom
 */
public class MumSnpToVcf2 implements Runnable {

    private File refSnps;
    private File querySnps;
    private File output;
    private final String sampleName;
    private SAMSequenceDictionary seqDict;
    private static final Filter<MumSNPFeature> REF_FILTER = new Filter<MumSNPFeature>() {

        @Override
        public boolean pass(MumSNPFeature snp) {
            return !snp.getrBase().equals(".")
                    && Allele.acceptableAlleleBases(snp.getrBase())
                    && Allele.acceptableAlleleBases(snp.getqBase());
        }
    };
    private static final Filter<MumSNPFeature> QRY_FILTER = new Filter<MumSNPFeature>() {

        @Override
        public boolean pass(MumSNPFeature snp) {
            return !snp.getqBase().equals(".")
                    && Allele.acceptableAlleleBases(snp.getrBase())
                    && Allele.acceptableAlleleBases(snp.getqBase());
        }
    };
    

    public MumSnpToVcf2(File refSnps, File querySnps, File output, SAMSequenceDictionary seqDict, String sampleName) {
        this.refSnps = refSnps;
        this.querySnps = querySnps;
        this.output = output;
        this.sampleName = sampleName;
        this.seqDict = seqDict;
    }

    private boolean exists() {
        return (output.exists() && output.length() > 0);
    }

    @Override
    public void run() {
        if (exists()) {
            System.out.println(output.getAbsolutePath() + " already exists");
            return;
        }

        final FilteringIterator<MumSNPFeature> refIter = new FilteringIterator<MumSNPFeature>(
                createMumSnpIter(refSnps), new CompositeFilter(new UniqueFilter(), REF_FILTER));
        final FilteringIterator<MumSNPFeature> qryIter = new FilteringIterator<MumSNPFeature>(
                createMumSnpIter(querySnps), new CompositeFilter(new UniqueFilter(), QRY_FILTER));
        final List<VariantContext> snps = new ArrayList<VariantContext>();
        final MergingIterator iter = new MergingIterator(refIter, qryIter, new MumSNPComparator());
        while (iter.hasNext()) {
            MergedRecord<MumSNPFeature> rec = iter.next();
            if (rec.lhs != null && rec.rhs != null) {
                snps.add(parseSNPByRef(rec.lhs));
            } else if (rec.lhs != null) {
                VariantContext vc = parseSNPByRef(rec.lhs);
                snps.add(makeAmbiguous(vc));
            } else if (rec.rhs != null) {
                VariantContext vc = parseSNPByQuery(rec.rhs);
                snps.add(makeAmbiguous(vc));
            }
        }

        Collections.sort(snps, new VariantContextComparator(seqDict));
        writeToFile(snps);
    }

    public static CloseableTribbleIterator<MumSNPFeature> createMumSnpIter(File snps) {
        try {
            AbstractFeatureReader<MumSNPFeature> reader = AbstractFeatureReader.getFeatureReader(snps.getAbsolutePath(), new MumSNPCodec(), false);
            return reader.iterator();
        } catch (IOException ex) {
            throw new PicardException("An error occured trying to read file", ex);
        }
    }

    private void writeToFile(List<VariantContext> mumSnps) {

        VariantContextWriter writer = VariantContextWriterFactory.create(output, seqDict);
        Set<VCFHeaderLine> metadata = new HashSet<VCFHeaderLine>();
        metadata.add(new VCFFormatHeaderLine(VCFConstants.GENOTYPE_KEY, 1, VCFHeaderLineType.String, "Genotype"));
        VCFHeader header = new VCFHeader(metadata, new HashSet(Arrays.asList(sampleName)));
        writer.writeHeader(header);

        for (VariantContext vc : mumSnps) {
            writer.add(vc);
        }

        writer.close();

    }

    private VariantContext parseSNPByRef(MumSNPFeature mumSnp) {
        String chr = mumSnp.getChr();
        int start = mumSnp.getStart();
        int end = mumSnp.getEnd();

        Allele refAllele = Allele.create(mumSnp.getrBase(), true);
        Allele altAllele = Allele.create(mumSnp.getqBase());

        List<Allele> alleles = new ArrayList<Allele>();
        alleles.add(refAllele);
        if (!altAllele.basesMatch(Allele.NO_CALL)) {
            alleles.add(altAllele);
        }

        Genotype g = new GenotypeBuilder(sampleName, Arrays.asList(altAllele)).make();
        VariantContextBuilder vcBuilder = new VariantContextBuilder("source", chr, start, end, alleles);
        vcBuilder.genotypes(g);
        return vcBuilder.make();
    }

    private VariantContext parseSNPByQuery(MumSNPFeature mumSnp) {
        String chr = mumSnp.getqFastaID();
        int start = mumSnp.getqPos();
        int end = mumSnp.getqPos();

        Allele refAllele = null;
        Allele altAllele = null;
        if (mumSnp.getqDir() == -1) {
            refAllele = Allele.create(SequenceUtil.complement((byte) mumSnp.getqBase().charAt(0)), true);
            altAllele = Allele.create(SequenceUtil.complement((byte) mumSnp.getrBase().charAt(0)));
        } else {
            refAllele = Allele.create(mumSnp.getqBase(), true);
            altAllele = Allele.create(mumSnp.getrBase());
        }

        List<Allele> alleles = new ArrayList<Allele>();
        alleles.add(refAllele);
        if (!altAllele.basesMatch(Allele.NO_CALL)) {
            alleles.add(altAllele);
        }

        Genotype g = new GenotypeBuilder(sampleName, Arrays.asList(altAllele)).make();
        VariantContextBuilder vcBuilder = new VariantContextBuilder("source", chr, start, end, alleles);
        vcBuilder.genotypes(g);
        return vcBuilder.make();
    }

    private static VariantContext makeAmbiguous(VariantContext vc) {
        List<Allele> alleles = Arrays.asList(vc.getReference(), Allele.create("N"));
        VariantContextBuilder vcBuilder = new VariantContextBuilder(vc.getSource(), vc.getChr(), vc.getStart(), vc.getEnd(), alleles);
        List<Genotype> genotypes = new ArrayList<Genotype>();
        for (Genotype g : vc.getGenotypesOrderedByName()) {
            Genotype gt = new GenotypeBuilder(g.getSampleName(), Arrays.asList(Allele.create("N"))).make();
            genotypes.add(gt);
        }
        vcBuilder.genotypes(genotypes);
        return vcBuilder.make();
    }

    public static void main(String[] args) throws IOException {
        File dir = new File("/Users/jbeckstrom/isgpipeline/test");
        File ref = new File(dir, "/mummer/ref_MSHR1043.snps");
        File qry = new File(dir, "/mummer/MSHR1043_ref.snps");
        final ReferenceSequenceFile refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(new File(dir, "ref.fasta"));
        final File out = new File(dir, "test.vcf");
        out.delete();
        MumSnpToVcf2 mumSnpToVcf2 = new MumSnpToVcf2(ref, qry, out, refSeq.getSequenceDictionary(), "MSHR1043");
        mumSnpToVcf2.run();
    }

    private static class MergedRecord<T> {

        public final T lhs;
        public final T rhs;

        public MergedRecord(T lhs, T rhs) {
            this.lhs = lhs;
            this.rhs = rhs;
        }
    }

    private static class MergingIterator<T> implements Iterator<MergedRecord<T>> {

        private final Comparator<T> comparator;
        private final Iterator<T> lhsIter;
        private final Iterator<T> rhsIter;
        private T lhs, rhs;

        public MergingIterator(Iterator<T> lhsIter, Iterator<T> rhsIter, Comparator<T> comparator) {
            this.lhsIter = lhsIter;
            this.rhsIter = rhsIter;
            this.comparator = comparator;

            lhs = advance(lhsIter);
            rhs = advance(rhsIter);
        }

        @Override
        public boolean hasNext() {
            return (lhs != null || rhs != null);
        }

        @Override
        public MergedRecord<T> next() {
            if (!hasNext()) {
                throw new IllegalStateException("cannot call next() on exhausted iterator");
            }
            final MergedRecord<T> ret;
            final int cmp = compare();
            if (cmp == 0) {
                //lhs = rhs, advance both
                ret = new MergedRecord(lhs, rhs);
                lhs = advance(lhsIter);
                rhs = advance(rhsIter);
            } else if (cmp > 0) {
                //lhs > rhs, advance rhs
                ret = new MergedRecord(null, rhs);
                rhs = advance(rhsIter);
            } else {
                //rhs > lhs, advance lhs
                ret = new MergedRecord(lhs, null);
                lhs = advance(lhsIter);
            }
            return ret;
        }

        private T advance(Iterator<T> iter) {
            if (iter.hasNext()) {
                return iter.next();
            }
            return null;
        }

        private int compare() {
            if (lhs == null && rhs == null) {
                throw new IllegalArgumentException("lhs and rhs cannot be null");
            } else if (lhs == null) {
                return 1;
            } else if (rhs == null) {
                return -1;
            }
            return comparator.compare(lhs, rhs);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class MumSNPComparator implements Comparator<MumSNPFeature> {

        @Override
        public int compare(MumSNPFeature lhs, MumSNPFeature rhs) {
            int retval = lhs.getChr().compareTo(rhs.getqFastaID());
            if (retval == 0) {
                retval = lhs.getStart() - rhs.getqPos();
            }
            return retval;
        }
    }

    private static class CompositeFilter implements Filter<MumSNPFeature> {

        private List<Filter<MumSNPFeature>> filters;

        public CompositeFilter(Filter<MumSNPFeature>... filters) {
            this.filters = Arrays.asList(filters);
        }

        @Override
        public boolean pass(MumSNPFeature t) {
            for (Filter<MumSNPFeature> f : filters) {
                if (!f.pass(t)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    private static class UniqueFilter implements Filter<MumSNPFeature> {

        private MumSNPFeature old = null;

        @Override
        public boolean pass(MumSNPFeature snp) {
            boolean ret = true;
            if (old != null) {
                ret = snp.getStart() != old.getStart()
                        || snp.getqPos() != old.getqPos()
                        || !snp.getChr().equals(old.getChr())
                        || !snp.getqFastaID().equals(old.getqFastaID());
            }
            old = snp;
            return ret;
        }
    };

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
}
