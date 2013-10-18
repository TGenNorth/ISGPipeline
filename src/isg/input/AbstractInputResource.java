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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractInputResource<T> other = (AbstractInputResource<T>) obj;
        if ((this.sampleName == null) ? (other.sampleName != null) : !this.sampleName.equals(other.sampleName)) {
            return false;
        }
        if (this.resource != other.resource && (this.resource == null || !this.resource.equals(other.resource))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.sampleName != null ? this.sampleName.hashCode() : 0);
        hash = 97 * hash + (this.resource != null ? this.resource.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "InputResource{" + "sampleName=" + sampleName + ", resource=" + resource + '}';
    }
    
    
}
