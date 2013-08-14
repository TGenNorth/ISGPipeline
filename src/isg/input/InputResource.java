/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

/**
 *
 * @author jbeckstrom
 */
public abstract class InputResource {
    
    protected final String sample;
    
    public InputResource(String sample){
        this.sample = sample;
    }

    public String getSample() {
        return sample;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final InputResource other = (InputResource) obj;
        if ((this.sample == null) ? (other.sample != null) : !this.sample.equals(other.sample)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.sample != null ? this.sample.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "InputResource{" + "sample=" + sample + '}';
    }
    
    
}
