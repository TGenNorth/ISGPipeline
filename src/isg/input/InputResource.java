/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

/**
 *
 * @author jbeckstrom
 */
public interface InputResource <T> {
    
    public String sampleName();
    
    public T resource();
    
    public void apply(InputResourceVisitor visitor);
    
    public InputResourceType type();
    
}
