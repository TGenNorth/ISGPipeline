/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import isg.util.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.variant.vcf.VCFConstants;
import util.VariantContextUtils;

/**
 * Determines if VariantContext object is "ambiguous" or not. If ambiguous, then
 * SNP is modified to have an alternate allele of 'N'.
 * 
 * @author jbeckstrom
 */
public class MarkAmbiguous implements Algorithm<VariantContext, VariantContext> {

    public static final Allele AMBIGUOUS_CALL = Allele.create("N");
    private final MarkAmbiguousInfo info;

    public MarkAmbiguous(MarkAmbiguousInfo info){
        this.info = info;
    }
    
    @Override
    public VariantContext apply(VariantContext vc) {
        if (VariantContextUtils.countUniqueAlleles(vc.getGenotype(0)) > info.maxNumAlt
                || (vc.hasLog10PError() && vc.getPhredScaledQual() < info.minQual)
                || (vc.getGenotype(0).hasGQ() && vc.getGenotype(0).getGQ() < info.minGQ)
                || (vc.hasAttribute(VCFConstants.DEPTH_KEY) && vc.getAttributeAsInt(VCFConstants.DEPTH_KEY, -1) < info.minDP)
                || (vc.hasAttribute(VCFConstants.ALLELE_FREQUENCY_KEY) && vc.getAttributeAsDouble(VCFConstants.ALLELE_FREQUENCY_KEY, -1) < info.minAF)) {
            return makeAmbiguous(vc);
        }
        return vc;
    }
    
    public VariantContext makeAmbiguous(VariantContext vc){
        List<Genotype> genotypes = new ArrayList<Genotype>();
        Set<Allele> alleles = new HashSet<Allele>();
        
        alleles.add(vc.getReference());
        alleles.add(AMBIGUOUS_CALL);
        for(Genotype g: vc.getGenotypes()){
            genotypes.add(GenotypeBuilder.create(g.getSampleName(), Arrays.asList(AMBIGUOUS_CALL)));
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
