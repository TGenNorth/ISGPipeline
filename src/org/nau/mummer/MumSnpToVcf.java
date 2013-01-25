/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import org.nau.mummer.findcommon.*;
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
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.util.SequenceUtil;
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
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 *
 * Find the common snps between two mummer snp files
 * @author jbeckstrom
 */
public class MumSnpToVcf implements Runnable {

    private File refSnps;
    private File querySnps;
    private File output;
    private String sampleName;
    private SAMSequenceDictionary seqDict;

    public MumSnpToVcf(File refSnps, File querySnps, File output, SAMSequenceDictionary seqDict, String sampleName) {
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

        //read first snps file into map
        //iterate over second snps file. For each pass look for matching snp and remove from map
        List<VariantContext> real = new ArrayList<VariantContext>();

        Map<String, VariantContext> snpMap = new HashMap<String, VariantContext>();
        Iterator<MumSNPFeature> iter = new MumSnpIterator(refSnps);
        while (iter.hasNext()) {
            MumSNPFeature snp = iter.next();
            String key = snp.getChr() + "_" + snp.getStart() + "_" + snp.getqFastaID() + "_" + snp.getqPos();
            if (seqDict.getSequenceIndex(snp.getChr()) != -1) {
                snpMap.put(key, parseSNPByRef(snp, sampleName));
            } else {
                snpMap.put(key, parseSNPByQuery(snp, sampleName));
            }

        }


        Iterator<MumSNPFeature> iter2 = new MumSnpIterator(querySnps);
        while (iter2.hasNext()) {
            MumSNPFeature snp = iter2.next();
            String key = snp.getqFastaID() + "_" + snp.getqPos() + "_" + snp.getChr() + "_" + snp.getStart();
            VariantContext value = snpMap.remove(key);
            if (value != null) {
                real.add(value);
            } else if(seqDict.getSequenceIndex(snp.getChr()) != -1) {
                real.add(makeAmbiguous(parseSNPByRef(snp, sampleName)));
            }else{
                real.add(makeAmbiguous(parseSNPByQuery(snp, sampleName)));
            }
        }

        for (VariantContext vc : snpMap.values()) {
            real.add(makeAmbiguous(vc));
        }

        snpMap.clear();
        Runtime.getRuntime().gc();

        Collections.sort(real, new VariantContextComparator(seqDict));

        writeToFile(real, sampleName, output);
    }

    private void writeToFile(List<VariantContext> mumSnps, String sampleName, File file) {

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

    private static VariantContext parseSNPByRef(MumSNPFeature mumSnp, String sampleName) {
        String chr = mumSnp.getChr();
        int start = mumSnp.getStart();
        int end = mumSnp.getEnd();

        Allele refAllele = Allele.create(mumSnp.getrBase(), true);
        Allele altAllele = Allele.create(mumSnp.getqBase());

        Genotype g = new GenotypeBuilder(sampleName, Arrays.asList(altAllele)).make();
        VariantContextBuilder vcBuilder = new VariantContextBuilder("source", chr, start, end, Arrays.asList(refAllele, altAllele));
        vcBuilder.genotypes(g);
        return vcBuilder.make();
    }

    private static VariantContext parseSNPByQuery(MumSNPFeature mumSnp, String sampleName) {
        String chr = mumSnp.getqFastaID();
        int start = mumSnp.getqPos();
        int end = mumSnp.getqPos();

        Allele refAllele = null;
        Allele altAllele = null;
        if(mumSnp.getqDir()==-1){
            refAllele = Allele.create(SequenceUtil.complement((byte)mumSnp.getqBase().charAt(0)), true);
            altAllele = Allele.create(SequenceUtil.complement((byte)mumSnp.getrBase().charAt(0)));
        }else{
            refAllele = Allele.create(mumSnp.getqBase(), true);
            altAllele = Allele.create(mumSnp.getrBase());
        }

        Genotype g = new GenotypeBuilder(sampleName, Arrays.asList(altAllele)).make();
        VariantContextBuilder vcBuilder = new VariantContextBuilder("source", chr, start, end, Arrays.asList(refAllele, altAllele));
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
        File refSnps = new File("test/data/burk/mummer/ref_vs_MSHR1043.snps");
        File querySnps = new File("test/data/burk/mummer/MSHR1043_vs_ref.snps");
        File output = new File("test/data/burk/MSHR1043.vcf");
        if (output.exists()) {
            output.delete();
        }
        SAMFileReader reader = new SAMFileReader(new File("test/data/burk/ref.dict"));
        SAMSequenceDictionary seqDict = reader.getFileHeader().getSequenceDictionary();
        MumSnpToVcf mumSnpToVcf = new MumSnpToVcf(refSnps, querySnps, output, seqDict, "MSHR1043");
        mumSnpToVcf.run();
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
}
