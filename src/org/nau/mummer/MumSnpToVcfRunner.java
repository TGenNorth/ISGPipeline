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
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
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

        Iterator<MumSNPFeature> iter = new MumSnpIterator(snps);
        while (iter.hasNext()) {
            MumSNPFeature snp = iter.next();

            VariantContextBuilder vcBldr = new VariantContextBuilder();
            vcBldr.chr(snp.getChr());
            vcBldr.start(snp.getStart());
            vcBldr.stop(snp.getEnd());

            Allele refAllele = Allele.create(snp.getrBase(), true);
            Allele altAllele = Allele.create(snp.getqBase());
            if (snp.getrNumRepeat() > 0 || snp.getqNumRepeat() > 0) {
                altAllele = Allele.create("N");
            }
            List<Allele> alleles = Arrays.asList(refAllele, altAllele);

            Genotype g = GenotypeBuilder.create(sampleName, alleles);
            vcBldr.genotypes(g);
            vcBldr.alleles(alleles);

            vcList.add(vcBldr.make());
        }

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
