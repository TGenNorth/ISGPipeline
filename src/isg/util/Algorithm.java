/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

/**
 *
 * @author jbeckstrom
 */
public interface Algorithm<I, O> {
    public O apply(I i);
}
