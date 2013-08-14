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
        this.resources = resources;
    }
    
    public Set<String> samples(){
        return resources.keySet();
    }
    
    public <T extends InputResource> List<T> getResources(Class<T> clazz){
        List<T> ret = new ArrayList<T>();
        for(InputResource resource: resources.values()){
            if(clazz.isInstance(resource)){
                ret.add((T)resource);
            }
        }
        return ret;
    }
}
