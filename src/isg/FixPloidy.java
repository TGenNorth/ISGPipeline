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

/**
 * "Fixes" the ploidy by reducing the number of alleles in each Genotype object
 * of a VariantContext to that of the specified ploidy. This is necessary because
 * the ploidy used by UG is set artificially high in order to genotype "ambiguous"
 * variants that would be assumed the reference if the ploidy was set correctly.
 * 
 * @author jbeckstrom
 */
public class FixPloidy implements Algorithm<VariantContext, VariantContext> {

    private final int targetPloidy;

    public FixPloidy(final int aPloidy) {
        this.targetPloidy = aPloidy;
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
            genotypes.add(fixPloidy(g));
        }
        
        final Set<Allele> alleles = new HashSet<Allele>();
        alleles.add(vc.getReference());
        alleles.addAll(getUniqueCallableAlleles(genotypes));

        return new VariantContextBuilder().alleles(alleles).genotypes(genotypes).chr(vc.getChr()).start(vc.getStart()).stop(vc.getEnd()).make();
    }
    
    public Genotype fixPloidy(final Genotype genotypeToFix){
        String sample = genotypeToFix.getSampleName();
        Set<Allele> alleles = new HashSet<Allele>(genotypeToFix.getAlleles());
        if(alleles.size()!=1){
            throw new IllegalStateException("Cannot fix ploidy of genotype with more than one unique allele: "+genotypeToFix);
        }
        return GenotypeBuilder.create(sample, new ArrayList<Allele>(alleles));
    }
    
    public Set<Allele> getUniqueCallableAlleles(List<Genotype> genotypes){
        Set<Allele> alleles = new HashSet<Allele>();
        for(Genotype g: genotypes){
            if(g.isNoCall()) continue;
            alleles.addAll(g.getAlleles());
        }
        return alleles;
    }
    
}
