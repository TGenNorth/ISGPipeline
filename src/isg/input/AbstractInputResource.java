/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

/**
 *
 * @author jbeckstrom
 */
public abstract class AbstractInputResource <T> implements InputResource <T> {
    
    private final String sampleName;
    private final T resource;

    public AbstractInputResource(String sampleName, T resource) {
        this.sampleName = sampleName;
        this.resource = resource;
    }
    
    @Override
    public String sampleName(){
        return sampleName;
    }
    
    @Override
    public T resource(){
        return resource;
    }
    
}
