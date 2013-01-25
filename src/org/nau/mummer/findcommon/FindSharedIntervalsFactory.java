/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.utils.CoordsUtils;

/**
 *
 * @author jbeckstrom
 */
public class FindSharedIntervalsFactory {
    
    public static FindSharedIntervals createFindSharedIntervals(Collection<Interval> i1, Collection<Interval> i2){
        return new FindSharedIntervals(i1, IntervalUtil.createOverlapDetector(i2));
    }
    
    public static FindSharedIntervals createFindSharedIntervals(File coordsFile1, File coordsFile2, boolean skipFirst){
        Collection<Interval> intervals1 = coordsToIntervals(coordsFile1, 0, skipFirst);
        Collection<Interval> intervals2 = coordsToIntervals(coordsFile2, 1, skipFirst);
        return createFindSharedIntervals(intervals1, intervals2);
    }
    
    public static FindSharedIntervals createFindSharedIntervals(File coordsFile, IntervalList intervalList, boolean skipFirst){
        Collection<Interval> intervals = coordsToIntervals(coordsFile, 0, skipFirst);
        return createFindSharedIntervals(intervals, intervalList.getIntervals());
    }
    
    public static FindSharedIntervals createFindSharedIntervals(File coordsFile, Collection<Interval> intervals2, boolean skipFirst){
        Collection<Interval> intervals1 = coordsToIntervals(coordsFile, 0, skipFirst);
        return createFindSharedIntervals(intervals1, intervals2);
    }
    
    private static Collection<Interval> coordsToIntervals(File coordsFile, int index, boolean skipFirst){
        List<CoordsRecord> coords = CoordsUtils.readCoords(coordsFile, skipFirst);
        Collection<Interval> intervals = coordsToIntervals(coords, index);
        return intervals;
    }
    
    private static Collection<Interval> coordsToIntervals(Collection<CoordsRecord> coords, int index){
        Collection<Interval> ret = new ArrayList<Interval>();
        for (CoordsRecord coord : coords) {
            Coord c1 = coord.getCoord(index);
            Interval i = new Interval(c1.getChr(), c1.getStart(), c1.getEnd());
            ret.add(i);
        }
        return ret;
    }
    
}
