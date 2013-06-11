/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import com.google.common.collect.AbstractIterator;
import isg.LociStateCaller;
import isg.util.ComparableIterator;
import isg.util.VariantContextComparator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.sting.utils.variant.GATKVariantContextUtils;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 * Merges multiple VariantContext iterators.
 * @author jbeckstrom
 */
public class MergingVariantContextIterator implements Iterator<VariantContext> {

    private final PriorityQueue<ComparableIterator<VariantContext>> pq;
    private final Comparator<VariantContext> cmp;

    public MergingVariantContextIterator(final List<Iterator<VariantContext>> iters, final SAMSequenceDictionary dict) {
        this.cmp = new VariantContextComparator(dict);
        this.pq = new PriorityQueue<ComparableIterator<VariantContext>>(iters.size());
        for (int i=0; i<iters.size(); i++) {
            addIfNotEmpty(new ComparableIterator<VariantContext>(iters.get(i), cmp, "",true));
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
    private void addIfNotEmpty(final ComparableIterator<VariantContext> iterator) {
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
    public VariantContext next() {
        if (!hasNext()) {
            throw new IllegalStateException("cannot call next() on exhausted iterator");
        }
        final List<VariantContext> recordsToMerge = new ArrayList<VariantContext>();

        ComparableIterator<VariantContext> iterator = null;
        VariantContext record = null;
        do {
            iterator = this.pq.poll();
            record = iterator.next();
            recordsToMerge.add(record);
            addIfNotEmpty(iterator);
        } while (((iterator = pq.peek()) != null)
                && (cmp.compare(record, iterator.peek()) == 0));

        return merge(recordsToMerge);
    }
    
    private VariantContext merge(List<VariantContext> recordsToMerge){
        return GATKVariantContextUtils.simpleMerge(
                recordsToMerge, //unsortedVCs
                null,  //priorityListOfVCs
                GATKVariantContextUtils.FilteredRecordMergeType.KEEP_UNCONDITIONAL, 
                GATKVariantContextUtils.GenotypeMergeType.UNSORTED, 
                false,  //annotate origin 
                false,  //print messages
                null,   //setKey 
                false,  //filteredAreUncalled 
                false); //mergeInfoWithMaxAC
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
