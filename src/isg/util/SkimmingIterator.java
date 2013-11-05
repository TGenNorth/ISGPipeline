package isg.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

/**
 * An iterator that "skims" the top of one or more iterators combining
 * elements that are equal as defined by the provided comparator implmenetaion. 
 * It is assumed that all input iterators are sorted based on the provided Comparator. 
 * In other words, calling next() on the iterator must return an element that is 
 * less than or equal to the previous element.
 * 
 */
public class SkimmingIterator<T> implements Iterator<Map<String, T>> {

    private final PriorityQueue<ComparableIterator<T>> pq;
    private final Map<String, Iterator<T>> iterMap = new HashMap<String, Iterator<T>>();
    private final Comparator<T> cmp;

    public SkimmingIterator(final Map<String, Iterator<T>> iterMap, final Comparator<T> cmp) {
        this.iterMap.putAll(iterMap);
        this.cmp = cmp;
        this.pq = new PriorityQueue<ComparableIterator<T>>(iterMap.size());
        init();
    }

    public SkimmingIterator(final List<String> keys, final List<Iterator<T>> iters, final Comparator<T> cmp) {
        if (keys.size() != iters.size()) {
            throw new IllegalArgumentException("There must be a key for each iterator.");
        }
        this.cmp = cmp;
        this.pq = new PriorityQueue<ComparableIterator<T>>(iters.size());
        for (int i = 0; i < keys.size(); i++) {
            if (iterMap.containsKey(keys.get(i))) {
                throw new IllegalArgumentException("Duplicate keys found in list.");
            }
            iterMap.put(keys.get(i), iters.get(i));
        }
        init();
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

    private void init() {
        for (Entry<String, Iterator<T>> entry : iterMap.entrySet()) {
            addIfNotEmpty(new ComparableIterator<T>(entry.getValue(), cmp, entry.getKey()));
        }
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
    public boolean hasNext() {
        return !this.pq.isEmpty();
    }

    /**
     * @return the next element in the iteration. Calling this method repeatedly until the hasNext() method returns
     * false will return each element in the underlying collection exactly once.
     */
    public Map<String, T> next() {
        if (!hasNext()) {
            throw new IllegalStateException("cannot call next() on exhausted iterator");
        }
        final Map<String, T> ret = new HashMap<String, T>();

        ComparableIterator<T> iterator = null;
        T record = null;
        do {
            iterator = this.pq.poll();
            record = iterator.next();
            ret.put(iterator.getId(), record);
            addIfNotEmpty(iterator);
        } while (((iterator = pq.peek()) != null)
                && (cmp.compare(record, iterator.peek()) == 0));

        return ret;
    }

    /**
     * Unsupported
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) {
        Comparator<Integer> cmp = new Comparator<Integer>() {

            public int compare(Integer t, Integer t1) {
                return t.compareTo(t1);
            }
        };
        List<String> names = new ArrayList<String>();
        List<Iterator<Integer>> iters = new ArrayList<Iterator<Integer>>();

        names.add("test1");
        iters.add(Arrays.asList(1, 2, 3).iterator());
        names.add("test2");
        iters.add(Arrays.asList(2, 3).iterator());
        names.add("test3");
        iters.add(Arrays.asList(3, 4).iterator());

        SkimmingIterator<Integer> iter = new SkimmingIterator(names, iters, cmp);
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }
}
