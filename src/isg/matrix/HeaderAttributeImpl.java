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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HeaderAttributeImpl other = (HeaderAttributeImpl) obj;
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    
    
}
