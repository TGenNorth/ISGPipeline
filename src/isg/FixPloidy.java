/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import isg.LociStateCaller;
import isg.util.Algorithm;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import util.VariantContextUtils;

/**
 * "Fixes" the ploidy by reducing the number of alleles in each Genotype object
 * of a VariantContext to that of the specified ploidy (currently only supports haploid). 
 * This is necessary because the ploidy used by UG is set artificially high in 
 * order to genotype "ambiguous" variants that would be assumed the reference if 
 * the ploidy was set correctly.
 * 
 * @author jbeckstrom
 */
public class FixPloidy implements Algorithm<VariantContext, VariantContext> {

    private final int targetPloidy = 1;
    private final double minAF;

    public FixPloidy(final double minAF) {
        this.minAF = minAF;
    }

    /*
     * This function has the side-effect of stripping out all attributes of the 
     * VariantContext and Genotypes therin. 
     */
    @Override
    public VariantContext apply(VariantContext vc) {
        if(vc.getGenotype(0).getPloidy()==targetPloidy){
            return vc;
        }

        final List<Genotype> genotypes = new ArrayList<Genotype>();
        for(Genotype g: vc.getGenotypes()){
            genotypes.add(fixPloidy(g, vc));
        }
        
        final Set<Allele> alleles = new HashSet<Allele>();
        alleles.add(vc.getReference());
        alleles.addAll(getUniqueCallableAlleles(genotypes));

        return new VariantContextBuilder().alleles(alleles).genotypes(genotypes).chr(vc.getChr()).start(vc.getStart()).stop(vc.getEnd()).make();
    }
    
    /*
     * "Fixes" the ploidy by comparing each unique allele's frequency against 
     * the minimum allele frequency (minAF) specified when constructing the class. 
     * Alleles that are at or above minAF are used in the new genotype returned 
     * by this function.
     */
    public Genotype fixPloidy(final Genotype genotypeToFix, final VariantContext vc){
        String sample = genotypeToFix.getSampleName();
        double[] af = VariantContextUtils.calculateAlleleFrequency(genotypeToFix);
        Set<Allele> alleles = new HashSet<Allele>();
        for(Allele allele: genotypeToFix.getAlleles()){
            int index = vc.getAlleleIndex(allele);
            if(af==null || af[index]>=minAF){
                alleles.add(allele);
            }
        }
        if(alleles.size()!=1){
            //should never get here because any genotype with more than one unique 
            //allele above the minAF threshold will be marked ambiguous.
            throw new IllegalStateException("Cannot fix ploidy of genotype with more than one unique allele: "+genotypeToFix);
        }
        return GenotypeBuilder.create(sample, new ArrayList<Allele>(alleles));
    }
    
    public Set<Allele> getUniqueCallableAlleles(List<Genotype> genotypes){
        Set<Allele> alleles = new HashSet<Allele>();
        for(Genotype g: genotypes){
            for(Allele a: g.getAlleles()){
                if(a.isCalled()){
                    alleles.add(a);
                }
            }
        }
        return alleles;
    }
    
}
