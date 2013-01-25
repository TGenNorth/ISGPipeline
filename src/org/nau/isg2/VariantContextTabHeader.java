/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextTabHeader {
    
    private List<String> attributeKeys = new ArrayList<String>();
    private List<String> genotypeNames = new ArrayList<String>();
    
    public VariantContextTabHeader(List<String> attributeKeys, List<String> genotypeNames){
        this.attributeKeys = attributeKeys;
        this.genotypeNames = genotypeNames;
    }
    
    public VariantContextTabHeader(List<String> attributeKeys, Set<String> genotypeNames){
        this.attributeKeys = attributeKeys;
        this.genotypeNames = new ArrayList<String>(genotypeNames);
        Collections.sort(this.genotypeNames);
    }

    public List<String> getAttributeKeys() {
        return attributeKeys;
    }

    public List<String> getGenotypeNames() {
        return genotypeNames;
    }
    
}
