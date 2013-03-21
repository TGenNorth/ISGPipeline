/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools.model;

import isgtools.util.PatternBuilder;
import isgtools.util.PatternNumGenerator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixRecord {
    
    private final String chrom;
    private final int pos;
    private final char ref;
    private final List<Character> states;
    private final Map<String, String> additionalInfo;

    public ISGMatrixRecord(String chrom, int pos, char ref, List<Character> states, Map<String, String> additionalInfo) {
        this.chrom = chrom;
        this.pos = pos;
        this.ref = ref;
        this.states = states;
        this.additionalInfo = additionalInfo;
    }
    
    public ISGMatrixRecord(ISGMatrixRecord record, Map<String, String> additionalInfo){
        this.chrom = record.getChrom();
        this.pos = record.getPos();
        this.ref = record.getRef();
        this.states = record.getStates();
        this.additionalInfo = new HashMap<String, String>(additionalInfo);
    }

    public String getChrom() {
        return chrom;
    }

    public int getPos() {
        return pos;
    }

    public char getRef() {
        return ref;
    }

    public char getState(int index) {
        return states.get(index);
    }
    
    public int getNStates(){
        return states.size();
    }

    public List<Character> getStates() {
        return states;
    }
    
    public String getAdditionalInfo(String key){
        if(!additionalInfo.containsKey(key)){
            return "";
        }
        return additionalInfo.get(key);
    }
    
    public Map<String, String> getAdditionalInfo(){
        return Collections.unmodifiableMap(additionalInfo);
    }
    
    public boolean containsSnp(){
        for(char state: states){
            if( (state=='A' || state=='T' || state=='C' || state=='G') && state!=ref ){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ISGMatrixRecord other = (ISGMatrixRecord) obj;
        if ((this.chrom == null) ? (other.chrom != null) : !this.chrom.equals(other.chrom)) {
            return false;
        }
        if (this.pos != other.pos) {
            return false;
        }
        if (this.ref != other.ref) {
            return false;
        }
        if (this.states != other.states && (this.states == null || !this.states.equals(other.states))) {
            return false;
        }
        if (this.additionalInfo != other.additionalInfo && (this.additionalInfo == null || !this.additionalInfo.equals(other.additionalInfo))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (this.chrom != null ? this.chrom.hashCode() : 0);
        hash = 61 * hash + this.pos;
        hash = 61 * hash + this.ref;
        hash = 61 * hash + (this.states != null ? this.states.hashCode() : 0);
        hash = 61 * hash + (this.additionalInfo != null ? this.additionalInfo.hashCode() : 0);
        return hash;
    }
    
    
    
}
