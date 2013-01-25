/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.util.ArrayList;
import java.util.Collection;
import net.sf.picard.util.Interval;
import net.sf.picard.util.OverlapDetector;

/**
 * Finds shared intervals between two collections of intervals
 * @author jbeckstrom
 */
public class FindSharedIntervals implements Runnable {

    //collection to store shared intervals
    private final Collection<Interval> sharedIntervals = new ArrayList<Interval>();
    
    private final Collection<Interval> intervals;
    private final OverlapDetector<Interval> od;
    
    public FindSharedIntervals(Collection<Interval> intervals, OverlapDetector<Interval> od){
        this.intervals = intervals;
        this.od = od;
    }
    
    
    
    @Override
    public void run() {
        for (Interval i1: intervals) {
            Collection<Interval> overlapingIntervals = od.getOverlaps(i1);
            for (Interval i2 : overlapingIntervals) {
                sharedIntervals.add(intersect(i1, i2));
            }
        }
    }
    
    public Collection<Interval> get(){
        return sharedIntervals;
    }
    
    private Interval intersect(Interval i1, Interval i2) {
        if (!i1.intersects(i2)) {
            throw new IllegalArgumentException(i1 + " does not intersect " + i2);
        }
        return new Interval(i1.getSequence(),
                Math.max(i1.getStart(), i2.getStart()),
                Math.min(i1.getEnd(), i2.getEnd()),
                i1.isNegativeStrand(),
                ".");
    }
    
}
