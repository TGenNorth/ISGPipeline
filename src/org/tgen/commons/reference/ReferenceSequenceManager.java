/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.reference;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author jbeckstrom
 */
public class ReferenceSequenceManager {
    
    private Map<String, byte[]> sequences = new TreeMap<String, byte[]>();
    
    public ReferenceSequenceManager(){
        
    }
    
    public void addSequence(String name, byte[] sequence){
        sequences.put(name, sequence);
    }
    
    public byte getBase(String name, int pos){
        byte[] bases = getSequence(name);
        return bases[pos-1];
    }
    
    public byte[] getSequence(String name){
        return sequences.get(name);
    }
    
}
