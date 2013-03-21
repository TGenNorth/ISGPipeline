package isgtools.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Wrapper around an iterator that enables non-destructive peeking at the next element that would
 * be returned by next()
 */
public class SkimmingIterator<K, T> implements Iterator<Map<K, T>> {

    final Map<K, Iterator<T>> iterMap = new HashMap<K, Iterator<T>>();
    final Map<K, T> currentElements = new HashMap<K, T>();
    final Comparator<T> cmp;
    
    public SkimmingIterator(final Map<K, Iterator<T>> iterMap, final Comparator<T> cmp){
        this.iterMap.putAll(iterMap);
        this.cmp = cmp;
        advanceAll();
    }

    public SkimmingIterator(final List<K> keys, final List<Iterator<T>> iters, final Comparator<T> cmp) {
        if (keys.size() != iters.size()) {
            throw new IllegalArgumentException("There must be a key for each iterator.");
        }
        this.cmp = cmp;
        for (int i = 0; i < keys.size(); i++) {
            if (iterMap.containsKey(keys.get(i))) {
                throw new IllegalArgumentException("Duplicate keys found in list.");
            }
            iterMap.put(keys.get(i), iters.get(i));
        }
        advanceAll();
    }

    /**
     * @return true if the iteration has more elements. (In other words, returns true if next would return an element 
     * rather than throwing an exception.)
     */
    public boolean hasNext() {
        for(T t: currentElements.values()){
            if(t!=null){
                return true;
            }
        }
        return false;
    }

    /**
     * @return the next element in the iteration. Calling this method repeatedly until the hasNext() method returns
     * false will return each element in the underlying collection exactly once.
     */
    public Map<K, T> next() {
        if(!hasNext()){
            throw new IllegalStateException("cannot call next() on exhausted iterator");
        }
        T top = null;
        final Map<K, T> ret = new HashMap<K, T>();
        for (final K key : currentElements.keySet()) {
            T cur = currentElements.get(key);
            if (cur == null) {
                continue;
            } else if (top == null) {
                top = cur;
                ret.put(key, cur);
            } else {
                int c = cmp.compare(cur, top);
                if (c < 0) {
                    ret.clear();
                    ret.put(key, cur);
                    top = cur;
                } else if (c == 0) {
                    ret.put(key, cur);
                }
            }
        }
        advance(ret.keySet());
        return ret;
    }
    
    private void advanceAll() {
        for (final K key : iterMap.keySet()) {
            advance(key);
        }
    }

    private void advance(Collection<K> keys) {
        for (final K key : keys) {
            advance(key);
        }
    }
    
    private void advance(K key) {
        final Iterator<T> iter = iterMap.get(key);
        if (iter.hasNext()) {
            currentElements.put(key, iter.next());
        }else{
            currentElements.put(key, null);
        }
    }

    /**
     * Unsupported
     */
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
    public static void main(String[] args){
        Comparator<Integer> cmp = new Comparator<Integer>(){

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
        
        SkimmingIterator<String, Integer> iter = new SkimmingIterator(names, iters, cmp);
        while(iter.hasNext()){
            System.out.println(iter.next());
        }
    }
}
