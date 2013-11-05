/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import isg.util.Algorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

/**
 * Genotype each sample that is undefined in a VariantContext object. 
 * Determines undefined samples based on samples that appear in the collection 
 * of expected samples that do not appear in the VaraintContext object in question.
 * 
 * @author jbeckstrom
 */
public class GenotypeNoCalls implements Algorithm<VariantContext, VariantContext> {

    private final Set<String> expectedSamples = new HashSet<String>();
    private final Map<String, SingleSampleGenotyper> genotypers = 
            new HashMap<String, SingleSampleGenotyper>();

    public GenotypeNoCalls(final List<SingleSampleGenotyper> agenotypers) {
        for(SingleSampleGenotyper ssg: agenotypers){
            expectedSamples.add(ssg.sample());
            genotypers.put(ssg.sample(), ssg);
        }
    }

    @Override
    public VariantContext apply(VariantContext vc) {
        if (!expectedSamples.containsAll(vc.getSampleNames())) {
            throw new IllegalArgumentException("Unexpected samples found in VariantContext: "+vc);
        }
        Set<String> noCallSamples = new HashSet<String>(expectedSamples);
        noCallSamples.removeAll(vc.getSampleNames());

        final List<Genotype> genotypes = new ArrayList<Genotype>(vc.getGenotypes());
        final Set<Allele> alleles = new HashSet<Allele>(vc.getAlleles());

        genotypes.addAll(genotype(noCallSamples, vc));
        addAllelesTo(genotypes, alleles);
        
        VariantContext ret = new VariantContextBuilder().alleles(alleles).genotypes(genotypes).chr(vc.getChr()).start(vc.getStart()).stop(vc.getEnd()).make();
        return ret;
    }
    
    public void addAllelesTo(List<Genotype> genotypes, Set<Allele> alleles){
        for(Genotype g: genotypes){
            if(g.isNoCall()) continue;
            alleles.addAll(g.getAlleles());
        }
    }

    public List<Genotype> genotype(Set<String> samples, final VariantContext vc) {
        final List<Genotype> genotypes = new ArrayList<Genotype>();
        for (final String sample : samples) {
            genotypes.add(genotype(sample, vc));
        }
        return genotypes;
    }

    public Genotype genotype(final String sample, final VariantContext vc) {
        if(!genotypers.containsKey(sample)){
            throw new IllegalArgumentException("Could not find LociStateCaller for sample: "+sample);
        }
        return genotypers.get(sample).genotype(vc.getReference(), vc.getChr(), vc.getStart());
    }

}
