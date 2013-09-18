/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jbeckstrom
 */
public class InputResourceManager {
    
    private final Map<String, InputResource> resources;
    
    protected InputResourceManager(Map<String, InputResource> resources){
        this.resources = new HashMap<String, InputResource>(resources);
    }
    
    public Set<String> samples(){
        return resources.keySet();
    }
    
    public void applyAll(InputResourceVisitor visitor){
        for(InputResource inputResource: resources.values()){
            inputResource.apply(visitor);
        }
    }
}
