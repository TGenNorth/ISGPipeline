/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.snps;

import isg.util.AbstractMergingIterator;
import isg.util.VariantContextComparator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.util.SequenceUtil;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
class MumSNPMarkAmbiguous extends AbstractMergingIterator<VariantContext, VariantContext> {

    public MumSNPMarkAmbiguous(List<Iterator<VariantContext>> iters, SAMSequenceDictionary seqDict) {
        super(iters, new VariantContextComparator(seqDict));
    }

    @Override
    public VariantContext merge(List<VariantContext> recordsToMerge) {
        switch (recordsToMerge.size()) {
            case 0:
                throw new IllegalStateException("Unexpected number of records to merge: " + recordsToMerge.size());
            case 1:
                //ambiguous
                return makeAmbiguous(recordsToMerge.get(0));
            default:
                return recordsToMerge.get(0);
        }
    }
    
    public VariantContext makeAmbiguous(VariantContext vc){
        List<Genotype> genotypes = new ArrayList<Genotype>();
        Set<Allele> alleles = new HashSet<Allele>();
        
        alleles.add(vc.getReference());
        alleles.add(Allele.create("N"));
        for(Genotype g: vc.getGenotypes()){
            genotypes.add(GenotypeBuilder.create(g.getSampleName(), Arrays.asList(Allele.create("N"))));
        }
        
        return new VariantContextBuilder()
                .alleles(alleles)
                .genotypes(genotypes)
                .chr(vc.getChr())
                .start(vc.getStart())
                .log10PError(vc.getLog10PError())
                .attributes(vc.getAttributes())
                .stop(vc.getEnd()).make();
    }

}
