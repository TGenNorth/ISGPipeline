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
    
    public static double[] calculateAlleleFrequency(Genotype g){
        if(!g.hasAD()){
            return null;
        }
        int[] ad = g.getAD();
        double[] af = new double[ad.length];
        int dp = sum(ad);
        for(int i=0; i<ad.length; i++){
            af[i] = (double) ad[i]/dp;
        }
        return af;
    }
    
    private static int sum(int[] nums){
        int sum = 0;
        for(int num: nums){
            sum += num;
        }
        return sum;
    }
    
}
