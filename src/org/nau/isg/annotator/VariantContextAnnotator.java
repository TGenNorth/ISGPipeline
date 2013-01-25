/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.annotator;

import java.util.Map.Entry;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.nau.snpclassifier.GenBankAnnotator;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextAnnotator {

    private GenBankAnnotator snpClassifier;
    private VariantContextPatternAnnotator patternAnnotator;

    public VariantContextAnnotator() {
        patternAnnotator = new VariantContextPatternAnnotator();
    }

    public void setSnpClassifier(GenBankAnnotator snpClassifier) {
        this.snpClassifier = snpClassifier;
    }
    
    public VariantContext annotate(VariantContext vc) {
        if(snpClassifier!=null)
            vc = snpClassifier.annotate(vc);
        vc = patternAnnotator.annotate(vc);
        return vc;
    }

    
}
