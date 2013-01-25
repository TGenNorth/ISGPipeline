/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2.util;

/**
 *
 * @author jbeckstrom
 */
public interface Filter<T> {
    
    public boolean pass(T t);
    
}
