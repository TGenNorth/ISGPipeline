/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabHeader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class PatternNumGenerator {
    
    private Map<String, Integer> patternNumMap = new HashMap<String, Integer>();
    private List<String> samples = new ArrayList<String>();
    private int count = 0;
    
    public PatternNumGenerator(Collection<String> samples){
        this.samples.add(VariantContextTabHeader.REF);
        this.samples.addAll(samples);
    }
    
    public int getPatternNum(Map<String, String> pattern){
        StringBuilder sb = new StringBuilder();
        for(String sample: samples){
            if(!pattern.containsKey(sample)){
                throw new IllegalArgumentException("could not find "+sample+" in pattern map");
            }
            sb.append(pattern.get(sample));
        }
        return getPatternNum(sb.toString());
    }
    
    public int getPatternNum(String pattern){
        if(!patternNumMap.containsKey(pattern)){
            patternNumMap.put(pattern, ++count);
        }
        return patternNumMap.get(pattern);
    }
    
}
