/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import net.sf.picard.util.Interval;
import net.sf.samtools.SAMSequenceDictionary;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.Feature;

/**
 * UNDER DEVELOPMENT
 * 
 * @author jbeckstrom
 */
public class OnePassOverlapDetector<T> {

    private final Iterator<T> iter;
    private final IntervalOverlapComparator comparator;
    private final List<T> curList = new ArrayList<T>();
    private T next = null;

    public OnePassOverlapDetector(final Iterator<T> iter, final SAMSequenceDictionary seqDict) {
        this.iter = iter;
        this.comparator = new IntervalOverlapComparator(seqDict);
//        advance();
    }

//    private void advance() {
//        curList.clear();
//        if (next != null) {
//            curList.add(next);
//        }
//        T prev = next;
//        while (iter.hasNext()) {
//            T cur = iter.next();
//            if (prev == null || comparator.compare(prev, cur) == 0) {
//                curList.add(cur);
//            } else {
//                next = cur;
//                break;
//            }
//            prev = cur;
//        }
//    }
//
//    private boolean canAdvance() {
//        return iter.hasNext() || !curList.contains(next);
//    }
//
//    private int compareCurrentTo(Interval interval) {
//        int cmp = 1;
//        for (T cur : curList) {
//            cmp = comparator.compare(cur, query);
//            if (cmp == 0) {
//                return 0;
//            }
//        }
//        return cmp;
//    }
//
//    private Collection<T> findOverlaps(Interval interval) {
//        final List<T> ret = new ArrayList<T>();
//        for (T cur : curList) {
//            if (comparator.compare(cur, query) == 0) {
//                ret.add(cur);
//            }
//        }
//        return ret;
//    }
//
//    public Collection<T> getOverlaps(Interval interval) {
//        int cmp = -1;
//        while ((cmp = compareCurrentTo(query)) < 0 && canAdvance()) {
//            advance();
//        }
//        return (cmp == 0) ? findOverlaps(query) : Collections.EMPTY_LIST;
//    }

    private static class IntervalOverlapComparator implements Comparator<Interval> {

        private final SAMSequenceDictionary seqDict;

        public IntervalOverlapComparator(final SAMSequenceDictionary seqDict) {
            this.seqDict = seqDict;
        }

        @Override
        public int compare(Interval lhs, Interval rhs) {
            final int lhsIndex = seqDict.getSequenceIndex(lhs.getSequence());
            final int rhsIndex = seqDict.getSequenceIndex(rhs.getSequence());
            int retval = lhsIndex - rhsIndex;

            if (retval == 0) {
                if (lhs.getEnd() < rhs.getStart()) {
                    return -1;
                } else if (lhs.getStart() > rhs.getEnd()) {
                    return 1;
                } else {
                    return 0;
                }
            }
            return retval;
        }
    }

}
