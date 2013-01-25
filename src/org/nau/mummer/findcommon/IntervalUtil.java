/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.util.Collection;
import net.sf.picard.util.Interval;
import net.sf.picard.util.OverlapDetector;

/**
 *
 * @author jbeckstrom
 */
public class IntervalUtil {
    
    public static OverlapDetector<Interval> createOverlapDetector(Collection<Interval> intervals){
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        for(Interval i: intervals){
            overlapDetector.addLhs(i, i);
        }
        return overlapDetector;
    }
    
    
}
