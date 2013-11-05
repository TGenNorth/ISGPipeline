package isg.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;

/**
 * Wrapper around an iterator that enables filtering of elements that would
 * be returned by next()
 */
public class FilteringIterator<T> extends AbstractIterator<T> {
    final Filter<T> filter;
    final Iterator<T> underlyingIterator;

    public FilteringIterator(final Iterator<T> underlyingIterator, final Filter<T> filter) {
        this.underlyingIterator = underlyingIterator;
        this.filter = filter;
    }
    
    /**
     * @return the iterator wrapped by this object.
     */
    public Iterator<T> getUnderlyingIterator() {
        return underlyingIterator;
    }

    @Override
    protected T computeNext() {
        while(underlyingIterator.hasNext()){
            final T t = underlyingIterator.next();
            if(filter.pass(t)){
                return t;
            }
        }
        return endOfData();
    }

    
}
