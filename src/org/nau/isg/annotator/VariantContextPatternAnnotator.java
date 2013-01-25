/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.annotator;

import java.util.HashMap;
import java.util.Map;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;
/**
 *
 * @author jbeckstrom
 */
public class VariantContextPatternAnnotator {
    
    private Map<String, Integer> patternNumMap = new HashMap<String, Integer>();

    public VariantContextPatternAnnotator(){}
    
    public VariantContext annotate(VariantContext vc){
        String pattern = createPattern(vc);
        int patternNum = getPatternNum(pattern);
        Map<String, Object> attribs = new HashMap(vc.getAttributes());
        attribs.put("pattern", pattern);
        attribs.put("patternNum", patternNum);
        
        VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
        vcBuilder.attributes(attribs);
        return vcBuilder.make();
    }
    
    private int getPatternNum(String pattern){
        Integer ret = patternNumMap.get(pattern);
        if(ret==null){
            ret = new Integer(patternNumMap.size()+1);
            patternNumMap.put(pattern, ret);
        } 
        return ret.intValue();
    }
    
    private String createPattern(VariantContext vc) {
        PatternBuilder builder = new PatternBuilder();
        builder.addAllele(vc.getReference());
        
        for (Genotype g : vc.getGenotypesOrderedByName()) {
            char base = getGenotypeAllele(g);
            builder.addAllele(base);
        }
        return builder.build();
    }

    private char getGenotypeAllele(Genotype g) {
//        if (g.getAlleles().size() > 1) {
//            throw new IllegalArgumentException("Genotype has more than one allele: " + g);
//        }
        return getState(g.getAllele(0));
    }
    
    private char getState(Allele allele){
        String baseStr = allele.getBaseString();
        if (baseStr.length() > 1) {
            throw new IllegalArgumentException("Allele has more than one base: " + allele);
        }
        if(baseStr.length()==0){
            baseStr = Allele.NO_CALL_STRING;
        }
        return baseStr.charAt(0);
    }
    
}
