/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 *
 * @author jbeckstrom
 */
public class DefaultMumSNPAlleleCaller implements SNPAlleleCaller<MumSNPFeature> {

    @Override
    public Allele callAlternate(MumSNPFeature snp) {
        return call(snp.getqBase());
    }

    @Override
    public Allele callReference(MumSNPFeature snp) {
        return Allele.create(snp.getrBase(), true);
    }
    
    private Allele call(String base){
        if (!base.equals(".")) {
            return Allele.create(base);
        } else {
            return Allele.NO_CALL;
        }
    }

}
