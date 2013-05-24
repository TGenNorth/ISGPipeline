/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

/**
 *
 * @author jbeckstrom
 */
public interface Filter<T> {
    
    public boolean pass(T t);
    
}
