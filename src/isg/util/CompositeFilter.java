/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import isg.util.Filter;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class CompositeFilter<T> implements Filter<T> {
    private List<Filter<T>> filters;

    public CompositeFilter(Filter<T>... filters) {
        this.filters = Arrays.asList(filters);
    }

    @Override
    public boolean pass(T t) {
        for (Filter<T> f : filters) {
            if (!f.pass(t)) {
                return false;
            }
        }
        return true;
    }
    
}
