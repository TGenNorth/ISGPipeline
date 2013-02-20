/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.sf.picard.PicardException;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
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
import org.tgen.commons.mummer.snp.MumSNPCodec;
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 * Convert mummer snps to vcf
 * 
 * @author jbeckstrom
 */
public class MumSnpToVcfRunner implements Runnable {

    private File snps;
    private File output;
    private String sampleName;
    private File ref;

    public MumSnpToVcfRunner(File snps, File output, File ref, String sampleName) {
        this.snps = snps;
        this.output = output;
        this.sampleName = sampleName;
        this.ref = ref;
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

        final ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(ref);
        final SAMSequenceDictionary seqDict = refSeqFile.getSequenceDictionary();

        List<VariantContext> vcList = new ArrayList<VariantContext>();

        final CloseableTribbleIterator<MumSNPFeature> iter = createMumSnpIter();

        while (iter.hasNext()) {
            MumSNPFeature snp = iter.next();

            VariantContextBuilder vcBldr = new VariantContextBuilder();
            vcBldr.chr(snp.getChr());
            vcBldr.start(snp.getStart());
            vcBldr.stop(snp.getEnd());

            if (snp.getrBase().equals(".")
                    || !Allele.acceptableAlleleBases(snp.getrBase())
                    || !Allele.acceptableAlleleBases(snp.getqBase())) {
                //unsupported base call
                continue;
            }

            Allele refAllele = Allele.create(snp.getrBase(), true);
            List<Allele> alleles;
            Allele altAllele;
            if (!snp.getqBase().equals(".")) {
                altAllele = Allele.create(snp.getqBase());
                alleles = Arrays.asList(refAllele, altAllele);
            }else{
                altAllele = Allele.NO_CALL;
                alleles = Arrays.asList(refAllele);
            }


            Genotype g = GenotypeBuilder.create(sampleName, Arrays.asList(altAllele));
            vcBldr.genotypes(g);
            vcBldr.alleles(alleles);

            vcList.add(vcBldr.make());
        }

        iter.close();
        Collections.sort(vcList, new VariantContextComparator(seqDict));

        writeToFile(vcList, sampleName, output, seqDict);
    }

    private void writeToFile(List<VariantContext> mumSnps, String sampleName, File file, SAMSequenceDictionary seqDict) {
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

    private CloseableTribbleIterator<MumSNPFeature> createMumSnpIter() {
        try {
            AbstractFeatureReader<MumSNPFeature> reader = AbstractFeatureReader.getFeatureReader(snps.getAbsolutePath(), new MumSNPCodec(), false);
            return reader.iterator();
        } catch (IOException ex) {
            throw new PicardException("An error occured trying to read file", ex);
        }
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

    public static void main(String[] args) {
        File wd = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test");
        File snps = new File(wd, "MSHR1043_MSHR1655.snps");
        File output = new File(wd, "out.vcf");
        File ref = new File(wd, "MSHR1043.fasta");
        String sampleName = "MSHR1043";
        output.delete();
        MumSnpToVcfRunner runner = new MumSnpToVcfRunner(snps, output, ref, sampleName);
        runner.run();
    }
}
