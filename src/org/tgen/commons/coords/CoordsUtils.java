/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.coords;

import net.sf.picard.util.Interval;
import org.tgen.commons.feature.Coord;

/**
 *
 * @author jbeckstrom
 */
public class CoordsUtils {
    
    public static Interval coordToInterval(Coord coord){
        return new Interval(coord.getChr(), coord.getStart(), coord.getEnd());
    }
    
}
