/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

/**
 * An iterator that applies the specified algorithm to each element
 * 
 * @author jbeckstrom
 */
public class AlgorithmApplyingIterator<I, O> extends AbstractIterator<O> {

    private final Iterator<I> iter;
    private final Algorithm<I, O> apply;
    
    public AlgorithmApplyingIterator(Iterator<I> iter, Algorithm<I, O> apply){
        this.iter = iter;
        this.apply = apply;
    }
    
    @Override
    protected O computeNext() {
        return (iter.hasNext()) ? apply.apply(iter.next()) : endOfData();
    }
}
