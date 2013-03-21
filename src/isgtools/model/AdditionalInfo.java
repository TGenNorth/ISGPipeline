/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools.model;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jbeckstrom
 */
public class AdditionalInfo {
    
    private final Map<String, String> infoMap = new HashMap<String, String>();
    
    public AdditionalInfo(Map<String, String> infoMap){
        this.infoMap.putAll(infoMap);
    }
    
    public String getValue(String key){
        return infoMap.get(key);
    }
    
}
