/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.vcf;

import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class VCFUtils {

    public static Double getGenotypeQuality(VariantContext vc) {
        try {
            if (vc.getSampleNames().size() > 0) {
                return getGenotypeQuality(vc, vc.getSampleNames().iterator().next());
            }
        } catch (Exception e) {
        }
        return null;
    }

    public static Double getGenotypeQuality(VariantContext vc, String sampleName) {
        Double ret = null;
        Genotype g = vc.getGenotype(sampleName);
        if (g != null) {
            ret = getGenotypeQuality(g);
        }
        return ret;
    }

    public static Double getGenotypeQuality(Genotype g) {
        Double ret = null;
        if(g.hasGQ()){
            ret = new Double(g.getGQ());
        }
        return ret;
    }
}
