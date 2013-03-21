/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.vcf;

import org.broadinstitute.sting.utils.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class AmbiguousSNPCaller {
    
    public static final String AMBIGUOUS_CALL_STRING = "N";
    
    private double minQual;
    
    public AmbiguousSNPCaller(double minQual){
        this.minQual = minQual;
    }
    
    public boolean isAmbiguous(VariantContext vc){
        if(vc.getAlternateAlleles().size()==0 && vc.getAlternateAllele(0).basesMatch(AMBIGUOUS_CALL_STRING)){
            return true;
        }
        if(vc.hasLog10PError() && vc.getPhredScaledQual()<minQual){
            return true;
        }
        return false;
    }
    
}
