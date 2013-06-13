/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.matrix;

/**
 *
 * @author jbeckstrom
 */
public class HeaderAttributeImpl implements HeaderAttribute{
    
    private final String name;
    
    public HeaderAttributeImpl(final String name){
        this.name = name;
    }
    
    @Override
    public String getName(){
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}
