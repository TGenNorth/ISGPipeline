package org.nau.isg2.util;

import java.util.Iterator;

/**
 * Wrapper around an iterator that enables filtering of elements that would
 * be returned by next()
 */
public class FilteringIterator<T> implements CacheableIterator<T> {
    final Filter<T> filter;
    final Iterator<T> underlyingIterator;
    T currentElement = null;
    T nextElement = null;

    public FilteringIterator(final Iterator<T> underlyingIterator, final Filter<T> filter) {
        this.underlyingIterator = underlyingIterator;
        this.filter = filter;
        init();
    }
    
    private void init(){
        nextElement = advance();
    }

    /**
     * @return true if the iteration has more elements. (In other words, returns true if next would return an element 
     * rather than throwing an exception.)
     */
    public boolean hasNext() {
        return (nextElement != null);  
    }

    /**
     * @return the next element in the iteration. Calling this method repeatedly until the hasNext() method returns
     * false will return each element in the underlying collection exactly once.
     */
    public T next() {
        currentElement = nextElement;
        nextElement = advance();
        return currentElement;
    }
    
    /**
     * @return the next element that passes the filter or null.
     */
    private T advance() {
        while(underlyingIterator.hasNext()){
            T t = underlyingIterator.next();
            if(filter.pass(t)){
                return t;
            }
        }
        return null;
    }

    /**
     * @return the next element in the iteration, but without removing it, so the next call to next() or peek()
     * will return the same element as returned by the current call to peek().
     */
    public T current() {
        return currentElement;
    }
    
    public T current(boolean initialize) {
        if(initialize && currentElement==null && hasNext()){
            currentElement = next();
        }
        return currentElement;
    }

    /**
     * Unsupported
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return the iterator wrapped by this object.
     */
    public Iterator<T> getUnderlyingIterator() {
        return underlyingIterator;
    }

    
}
