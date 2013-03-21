/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.tgen.commons.coords.CoordsFileReader;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;

/**
 *
 * @author jbeckstrom
 */
public class CoordsUtils {
    
    public static List<CoordsRecord> readCoords(File coordsFile, boolean skipFirstLine){
        List<CoordsRecord> ret = new ArrayList<CoordsRecord>();
        CoordsFileReader reader = new CoordsFileReader(coordsFile);
        CoordsRecord record = null;
        String chr = null;
        while( (record = reader.next()) != null ){
            Coord c = record.getCoord(0);
            if(skipFirstLine && (chr==null || !c.getChr().equals(chr))){
                //skip this record
                chr = c.getChr();
            }else{
                ret.add(record);
            }
        }
        return ret;
    }
    
}
