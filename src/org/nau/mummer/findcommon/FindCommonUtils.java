/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.io.File;
import java.util.List;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.picard.util.OverlapDetector;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.utils.CoordsUtils;

/**
 *
 * @author jbeckstrom
 */
public class FindCommonUtils {
    
    public static OverlapDetector<Interval> createOverlapDetectorFromCoords(File coordsFile, boolean skipFirst){
        List<CoordsRecord> coords = CoordsUtils.readCoords(coordsFile, skipFirst);
        return createOverlapDetectorFromCoords(coords);
    }
    
    public static OverlapDetector<Interval> createOverlapDetectorFromCoords(List<CoordsRecord> coords){
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        for (CoordsRecord coord : coords) {
            Coord c = coord.getCoord(1);
            Interval i = new Interval(c.getChr(), c.getStart(), c.getEnd());
            overlapDetector.addLhs(i, i);
        }
        return overlapDetector;
    }
    
    public static OverlapDetector<Interval> createOverlapDetectorFromIntervalFile(File f){
        IntervalList intervalList = IntervalList.fromFile(f);
        return createOverlapDetectorFromIntervalList(intervalList);
    }
    
    public static OverlapDetector<Interval> createOverlapDetectorFromIntervalList(IntervalList intervalList){
        
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        for(Interval i: intervalList.getIntervals()){
            overlapDetector.addLhs(i, i);
        }
        return overlapDetector;
    }
    
}
