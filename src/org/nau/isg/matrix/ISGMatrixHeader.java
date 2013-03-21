/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixHeader {

    private final List<String> sampleNames;
    private final Set<String> additionalInfo = new LinkedHashSet<String>();
    
    public static final String CHROM = "Chrom";
    public static final String POS = "Pos";
    public static final String REF = "Ref";
    public static final String PATTERN = "Pattern";
    public static final String PATTERN_NUM = "PatternNum"; 
    public static final String NUM_SAMPLES = "numSamples";
    
    public ISGMatrixHeader(List<String> sampleNames){
        this(sampleNames, new LinkedHashSet<String>());
    }
    
    public ISGMatrixHeader(List<String> sampleNames, Collection<String> additionalInfo){
        this.sampleNames = sampleNames;
        this.additionalInfo.addAll(additionalInfo);
    }

    public List<String> getSampleNames() {
        return Collections.unmodifiableList(sampleNames);
    }
    
    public Set<String> getAdditionalInfo(){
        return additionalInfo;
    }
    
    public int indexOfSample(String sampleName){
        return sampleNames.indexOf(sampleName);
    }
    
    public static ISGMatrixHeader addAddtionalInfo(ISGMatrixHeader header, Collection<String> additionalInfoToAdd){
        Set<String> additionalInfo = new LinkedHashSet<String>();
        additionalInfo.addAll(header.getAdditionalInfo());
        additionalInfo.addAll(additionalInfoToAdd);
        return new ISGMatrixHeader(header.getSampleNames(), additionalInfo);
    }
    
    public static ISGMatrixHeader removeSampleFromHeader(ISGMatrixHeader header, String sampleNameToRemove){
        List<String> newSampleNames = new ArrayList<String>();
        for(String sampleName: header.getSampleNames()){
            if(!sampleName.equalsIgnoreCase(sampleNameToRemove)){
                newSampleNames.add(sampleName);
            }
        }
        return new ISGMatrixHeader(newSampleNames, header.getAdditionalInfo());
    }
    
    public static ISGMatrixHeader removeSamplesFromHeader(ISGMatrixHeader header, List<String> sampleNamesToRemove) {
        List<String> newSampleNames = new ArrayList<String>();
        for(String sampleName: header.getSampleNames()){
            if(!sampleNamesToRemove.contains(sampleName)){
                newSampleNames.add(sampleName);
            }
        }
        return new ISGMatrixHeader(newSampleNames, header.getAdditionalInfo());
    }
    
    public static ISGMatrixHeader removeSampleIndicesFromHeader(ISGMatrixHeader header, List<Integer> indices) {
        List<String> newSampleNames = new ArrayList<String>();
        for(int i=0; i<header.getNSamples(); i++){
            if(!indices.contains(i)){
                newSampleNames.add(header.getSampleAtIndex(i));
            }
        }
        return new ISGMatrixHeader(newSampleNames, header.getAdditionalInfo());
    }
    
    public static ISGMatrixHeader includeSampleIndicesFromHeader(ISGMatrixHeader header, List<Integer> indices) {
        List<String> newSampleNames = new ArrayList<String>();
        for(int i=0; i<header.getNSamples(); i++){
            if(indices.contains(i)){
                newSampleNames.add(header.getSampleAtIndex(i));
            }
        }
        return new ISGMatrixHeader(newSampleNames, header.getAdditionalInfo());
    }

    public int getNSamples() {
        return sampleNames.size();
    }
    
    public List<Integer> getIndicesOfSamples(List<String> sampleNamesToFind){
        List<Integer> ret = new ArrayList<Integer>();
        for(String sampleName: sampleNamesToFind){
            int index = sampleNames.indexOf(sampleName);
            if(index==-1){
                throw new IllegalArgumentException("Could not find sample with name: "+sampleName);
            }else{
                ret.add(index);
            }
        }
        return ret;
    }
    
    public List<Integer> getIndicesOfSamples(String regex){
        List<Integer> ret = new ArrayList<Integer>();
        for(int i=0; i<sampleNames.size(); i++){
            String sampleName = sampleNames.get(i);
            if(sampleName.matches(regex)){
                ret.add(i);
            }
        }
        return ret;
    }

    private String getSampleAtIndex(int i) {
        return sampleNames.get(i);
    }
    
}
