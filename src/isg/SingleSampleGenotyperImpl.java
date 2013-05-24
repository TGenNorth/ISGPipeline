/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.util.Arrays;
import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;

/**
 * An implementation of SingleSampleGenotyper that uses a LociStateCaller to 
 * perform genotyping.
 * 
 * @author jbeckstrom
 */
public class SingleSampleGenotyperImpl implements SingleSampleGenotyper{

    private final String sample; 
    private final LociStateCaller caller;
    
    public SingleSampleGenotyperImpl(final String sample, final LociStateCaller caller){
        this.sample = sample;
        this.caller = caller;
    }
    
    @Override
    public String sample() {
        return sample;
    }

    @Override
    public Genotype genotype(Allele ref, String chr, int pos) {
        if(ref.length()>1){
            throw new IllegalArgumentException("cannot genotype a locus length > 1 : "+ref.length());
        }
        if(ref.isNonReference()){
            throw new IllegalArgumentException("ref Allele must be flagged as reference.");
        }
        final CalledState calledState = caller.call(chr, pos);
        switch (calledState) {
            case REF_N:
                if(!ref.basesMatch("N")){
                    throw new IllegalStateException("Expected a REF_N but found: "+ref);
                }
            case CALLABLE:
                return new GenotypeBuilder(sample, Arrays.asList(ref)).make();
            case NO_COVERAGE:
                return new GenotypeBuilder(sample, Arrays.asList(Allele.NO_CALL)).make();
            case EXCESSIVE_COVERAGE:
            case LOW_COVERAGE:
            case POOR_MAPPING_QUALITY:
                return new GenotypeBuilder(sample, Arrays.asList(MarkAmbiguous.AMBIGUOUS_CALL)).make();
            default:
                throw new IllegalStateException("Unknown called state: " + calledState);
        }
    }
    
    
}
