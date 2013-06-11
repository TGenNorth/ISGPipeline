/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import isg.util.ComparableIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Merges multiple iterators.
 * 
 * @author jbeckstrom
 */
public abstract class AbstractMergingIterator<T, K> implements Iterator<K> {

    private final PriorityQueue<ComparableIterator<T>> pq;
    private final Comparator<T> cmp;

    public AbstractMergingIterator(final List<Iterator<T>> iters, final Comparator<T> cmp) {
        this.cmp = cmp;
        this.pq = new PriorityQueue<ComparableIterator<T>>();
        for (int i=0; i<iters.size(); i++) {
            addIfNotEmpty(new ComparableIterator<T>(iters.get(i), cmp, "", true));
        }
    }

    public static <K, V> Map<K, V> mapping(List<K> keys, List<V> values) {
        if (keys.size() != values.size()) {
            throw new IllegalArgumentException("There must be a key for each value.");
        }
        Map<K, V> ret = new HashMap<K, V>();
        for (int i = 0; i < keys.size(); i++) {
            ret.put(keys.get(i), values.get(i));
        }
        return ret;
    }

    /**
     * Adds iterator to priority queue. If the iterator has more records it is added
     * otherwise it is closed and not added.
     */
    private void addIfNotEmpty(final ComparableIterator<T> iterator) {
        if (iterator.hasNext()) {
            pq.offer(iterator);
        } else {
            iterator.close();
        }
    }

    /**
     * @return true if the iteration has more elements. (In other words, returns true if next would return an element 
     * rather than throwing an exception.)
     */
    @Override
    public boolean hasNext() {
        return !this.pq.isEmpty();
    }

    /**
     * @return the next element in the iteration. Calling this method repeatedly until the hasNext() method returns
     * false will return each element in the underlying collection exactly once.
     */
    @Override
    public K next() {
        if (!hasNext()) {
            throw new IllegalStateException("cannot call next() on exhausted iterator");
        }
        final List<T> recordsToMerge = new ArrayList<T>();

        ComparableIterator<T> iterator = null;
        T record = null;
        do {
            iterator = this.pq.poll();
            record = iterator.next();
            recordsToMerge.add(record);
            addIfNotEmpty(iterator);
        } while (((iterator = pq.peek()) != null)
                && (cmp.compare(record, iterator.peek()) == 0));

        return merge(recordsToMerge);
    }
    
    public abstract K merge(List<T> recordsToMerge);

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
