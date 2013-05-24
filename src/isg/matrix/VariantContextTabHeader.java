/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextTabHeader {
    
    public static final String CHROM = "Chrom";
    public static final String POS = "Pos";
    public static final String REF = "Ref";
    public static final String PATTERN = "Pattern";
    public static final String PATTERN_NUM = "PatternNum"; 
    public static final String NUM_SAMPLES = "numSamples";
    
    private Set<String> attributeKeys = new LinkedHashSet<String>();
    private Set<String> genotypes = new LinkedHashSet<String>();
    
    public VariantContextTabHeader(){}
    
    public VariantContextTabHeader(List<String> attributeKeys, List<String> genotypeNames){
        this.attributeKeys.addAll(attributeKeys);
        this.genotypes.addAll(genotypeNames);
    }
    
    public VariantContextTabHeader(List<String> attributeKeys, Set<String> genotypeNames){
        this.attributeKeys.addAll(attributeKeys);
        this.genotypes.addAll(genotypeNames);
    }

    public Set<String> getAttributeKeys() {
        return Collections.unmodifiableSet(attributeKeys);
    }

    public Set<String> getGenotypeNames() {
        return Collections.unmodifiableSet(genotypes);
    }
    
    public int numSamples(){
        return genotypes.size();
    }
    
    public VariantContextTabHeader addAttribute(final String attr){
        List<String> attributes = new ArrayList<String>(attributeKeys);
        attributes.add(attr);
        return new VariantContextTabHeader(attributes, genotypes);
    }

    public VariantContextTabHeader removeSamples(Collection<String> samplesToRemove) {
        List<String> genotypes = new ArrayList<String>(this.genotypes);
        List<String> attributes = new ArrayList<String>(attributeKeys);
        genotypes.removeAll(samplesToRemove);
        for(String sample: samplesToRemove){
            attributes.removeAll(findAllStartsWith(attributes, sample));
        }
        return new VariantContextTabHeader(attributes, genotypes); 
    }
    
    private List<String> findAllStartsWith(List<String> aList, String prefix){
        List<String> ret = new ArrayList<String>();
        for(String str: aList){
            if(str.startsWith(prefix)){
                ret.add(str);
            }
        }
        return ret;
    }
    
}
