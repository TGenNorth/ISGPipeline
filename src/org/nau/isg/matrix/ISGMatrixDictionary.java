/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nau.isg.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixDictionary {

    private Map<String, Map<Integer, VariantContext>> matrixDict =
            new HashMap<String, Map<Integer, VariantContext>>();

    public ISGMatrixDictionary(){

    }

    public List<VariantContext> getVariants(){
        List<VariantContext> ret = new ArrayList<VariantContext>();
        Collection<Map<Integer, VariantContext>> maps = matrixDict.values();
        for(Map<Integer, VariantContext> map: maps){
            ret.addAll(map.values());
        }
        return ret;
    }

    public boolean containsVariantContext(VariantContext vc){
        return containsVariantContext(vc.getChr(), vc.getStart());
    }

    public boolean containsVariantContext(String chr, int pos){
        Map<Integer, VariantContext> map = matrixDict.get(chr);
        if(map!=null){
            return map.containsKey(pos);
        }
        return false;
    }

    public VariantContext getVariantContext(String chr, int pos){
        Map<Integer, VariantContext> map = matrixDict.get(chr);
        if(map!=null){
            return map.get(pos);
        }
        return null;
    }

    public void putVariantContext(String chr, int pos, VariantContext vc){
        Map<Integer, VariantContext> map = matrixDict.get(chr);
        if(map==null){
            map = new HashMap<Integer, VariantContext>();
            matrixDict.put(chr, map);
        }
        map.put(pos, vc);
    }

}
