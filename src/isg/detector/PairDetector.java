/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.broadinstitute.sting.utils.collections.Pair;

/**
 *
 * @author jbeckstrom
 */
public class PairDetector<T> {
    
    private List<DetectionAlgorithm<T, T>> detectors = new ArrayList<DetectionAlgorithm<T, T>>();
    
    public PairDetector(){}
    
    public void add(DetectionAlgorithm<T, T> pda){
        detectors.add(pda);
    }
    
    /**
     * Detects all pairs in a given collection. If no pair is detected for a given
     * element, then the element itself is considered a "pair" of one and appended
     * to the returned list.
     * 
     * @param c a collection of elements to detect pairs
     * @return a list of lists. 
     */
    public List<List<T>> detectAllPairs(Collection<T> c){
        List<List<T>> ret = new ArrayList<List<T>>();
        Set<T> ignore = new HashSet<T>();
        for(final T t: c){
            if(ignore.contains(t)){
                continue;
            }
            final DetectionAlgorithm<T, T> d = findDetector(t);
            if(d==null){
                ret.add(Arrays.asList(t));
            }else{
                final T t2 = d.apply(t);
                if(!c.contains(t2)){
                    throw new IllegalStateException("Could not find pair in collection: "+t2);
                }
                ret.add(Arrays.asList(t, t2));
                ignore.add(t2);
            }
        }
        return ret;
    }
    
    private DetectionAlgorithm<T, T> findDetector(T t){
        for(DetectionAlgorithm<T, T> d: detectors){
            if(d.detectable(t)){
                return d;
            }
        }
        return null;
    }
    
}
