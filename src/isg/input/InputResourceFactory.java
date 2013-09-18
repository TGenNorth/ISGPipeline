/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public interface InputResourceFactory <T> {
    
    public boolean isResourceType(File f);
    
    public InputResource<T> create(File f);
    
}
