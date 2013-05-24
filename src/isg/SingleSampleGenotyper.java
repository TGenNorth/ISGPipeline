/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;

/**
 *
 * @author jbeckstrom
 */
public interface SingleSampleGenotyper {
    
    public String sample();
    
    public Genotype genotype(Allele ref, String chr, int pos);
    
}
