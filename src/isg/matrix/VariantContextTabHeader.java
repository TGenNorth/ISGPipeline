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
    public static final String NUM_SAMPLES = "numSamples";
    
    private Set<HeaderAttribute> attributeKeys = new LinkedHashSet<HeaderAttribute>();
    private Set<String> genotypes = new LinkedHashSet<String>();
    
    public VariantContextTabHeader(){}
    
    public VariantContextTabHeader(List<HeaderAttribute> attributeKeys, List<String> genotypeNames){
        this.attributeKeys.addAll(attributeKeys);
        this.genotypes.addAll(genotypeNames);
    }
    
    public VariantContextTabHeader(List<HeaderAttribute> attributeKeys, Set<String> genotypeNames){
        this.attributeKeys.addAll(attributeKeys);
        this.genotypes.addAll(genotypeNames);
    }

    public Set<HeaderAttribute> getAttributeKeys() {
        return Collections.unmodifiableSet(attributeKeys);
    }

    public Set<String> getGenotypeNames() {
        return Collections.unmodifiableSet(genotypes);
    }
    
    public int numSamples(){
        return genotypes.size();
    }
    
    public VariantContextTabHeader addAttribute(final HeaderAttribute attr){
        List<HeaderAttribute> attributes = new ArrayList<HeaderAttribute>(attributeKeys);
        attributes.add(attr);
        return new VariantContextTabHeader(attributes, genotypes);
    }

    public VariantContextTabHeader removeSamples(Collection<String> samplesToRemove) {
        List<String> genotypes = new ArrayList<String>(this.genotypes);
        List<HeaderAttribute> attributes = new ArrayList<HeaderAttribute>(attributeKeys);
        genotypes.removeAll(samplesToRemove);
        for(String sample: samplesToRemove){
            attributes.removeAll(findAttributesForSample(attributes, sample));
        }
        return new VariantContextTabHeader(attributes, genotypes); 
    }
    
    private List<HeaderAttribute> findAttributesForSample(List<HeaderAttribute> aList, String sampleName){
        List<HeaderAttribute> ret = new ArrayList<HeaderAttribute>();
        for(HeaderAttribute attr: aList){
            if(attr instanceof HeaderSampleAttribute && ((HeaderSampleAttribute)attr).getSampleName().equals(sampleName)){
                ret.add(attr);
            }
        }
        return ret;
    }
    
}
