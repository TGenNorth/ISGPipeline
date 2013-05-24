/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.snps;

import isg.util.Algorithm;
import isg.util.AlgorithmApplyingIterator;
import isg.util.CompositeFilter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.picard.PicardException;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMSequenceDictionary;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.variant.vcf.VCFConstants;
import org.broadinstitute.variant.vcf.VCFFormatHeaderLine;
import org.broadinstitute.variant.vcf.VCFHeader;
import org.broadinstitute.variant.vcf.VCFHeaderLine;
import org.broadinstitute.variant.vcf.VCFHeaderLineType;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.variant.variantcontext.writer.VariantContextWriterFactory;
import isg.util.Filter;
import isg.util.FilteringIterator;
import isg.util.VariantContextComparator;
import java.io.FileInputStream;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.SAMTextHeaderCodec;
import net.sf.samtools.util.BufferedLineReader;
import org.broadinstitute.sting.queue.function.AbstractInProcessFunction;

/**
 *
 * Find the common snps between two mummer snp files
 * @author jbeckstrom
 */
public class MumSnpToVcf extends AbstractInProcessFunction {

    @Input(doc = "input ref snps")
    public File refSnpsFile;
    @Input(doc = "input query snps")
    public File querySnpsFile;
    @Input(doc = "reference sequence file")
    public File referenceSequence;
    @Output(doc = "output vcf")
    public File output;
    @Argument(doc = "sample name")
    public String sampleName;
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

    private boolean exists() {
        return (output.exists() && output.length() > 0);
    }

    @Override
    public void run() {
        if (exists()) {
            System.out.println(output.getAbsolutePath() + " already exists");
            return;
        }

        seqDict = ReferenceSequenceFileFactory
                .getReferenceSequenceFile(referenceSequence)
                .getSequenceDictionary();

        final Algorithm<MumSNPFeature, VariantContext> converter = new MumSNPToVariantContext(seqDict, sampleName);
        final Iterator<MumSNPFeature> refIter = new FilteringIterator<MumSNPFeature>(
                createMumSnpIter(refSnpsFile), new CompositeFilter<MumSNPFeature>(new UniqueFilter(), REF_FILTER));
        final Iterator<MumSNPFeature> qryIter = new FilteringIterator<MumSNPFeature>(
                createMumSnpIter(querySnpsFile), new CompositeFilter<MumSNPFeature>(new UniqueFilter(), QRY_FILTER));
        final List<VariantContext> snps = new ArrayList<VariantContext>();
        
        final List<Iterator<VariantContext>> iters = new ArrayList<Iterator<VariantContext>>();
        iters.add(new AlgorithmApplyingIterator<MumSNPFeature, VariantContext>(refIter, converter));
        iters.add(new AlgorithmApplyingIterator<MumSNPFeature, VariantContext>(qryIter, converter));
        
        final MumSNPMarkAmbiguous iter = new MumSNPMarkAmbiguous(iters, seqDict);
        while (iter.hasNext()) {
            snps.add(iter.next());
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
}
