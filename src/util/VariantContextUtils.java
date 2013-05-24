/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.HashSet;
import java.util.Set;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextUtils {
    
    public static int countUniqueAlleles(Genotype g){
        Set<Allele> uniqueAlleles = new HashSet<Allele>();
        uniqueAlleles.addAll(g.getAlleles());
        return uniqueAlleles.size();
    }
    
}
