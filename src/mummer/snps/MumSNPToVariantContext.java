/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.snps;

import isg.util.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.util.SequenceUtil;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
public class MumSNPToVariantContext implements Algorithm<MumSNPFeature, VariantContext> {

    private final SAMSequenceDictionary seqDict;
    private final String sample;

    public MumSNPToVariantContext(SAMSequenceDictionary seqDict, String sample) {
        this.seqDict = seqDict;
        this.sample = sample;
    }

    @Override
    public VariantContext apply(MumSNPFeature snp) {
        switch (determineReference(snp)) {
            case 0:
                return toVariantContext(snp.getChr(), snp.getStart(), snp.getrBase(), snp.getqBase(), snp.getrDir());
            case 1:
                return toVariantContext(snp.getqFastaID(), snp.getqPos(), snp.getqBase(), snp.getrBase(), snp.getqDir());
            default:
                throw new IllegalStateException("Could not determine reference for snp: "+snp);
        }
    }

    public int determineReference(MumSNPFeature snp) {
        if (seqDict.getSequence(snp.getChr()) != null) {
            return 0;
        } else if (seqDict.getSequence(snp.getqFastaID()) != null) {
            return 1;
        } else {
            throw new IllegalArgumentException("Could not find reference coord in sequence dictionary.");
        }
    }

    private VariantContext toVariantContext(String chr, int pos, String ref, String qry, int rDir) {
        final Allele refAllele = toAllele(ref, true, rDir);
        final Allele altAllele = toAllele(qry, false, rDir);
        return new VariantContextBuilder("", chr, pos, pos, Collections.EMPTY_LIST).alleles(asCallableList(refAllele, altAllele)).genotypes(new GenotypeBuilder(sample, Arrays.asList(altAllele)).make()).make();
    }

    private List<Allele> asCallableList(Allele... alleles) {
        List<Allele> ret = new ArrayList<Allele>();
        for (Allele a : alleles) {
            if (!a.basesMatch(Allele.NO_CALL)) {
                ret.add(a);
            }
        }
        return ret;
    }

    private Allele toAllele(String bases, boolean ref, int dir) {
        if (dir == -1) {
            return Allele.create(SequenceUtil.reverseComplement(bases), ref);
        } else {
            return Allele.create(bases, ref);
        }
    }
}
