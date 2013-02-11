/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.tools;

import org.nau.coverage.coords.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tgen.commons.coords.CoordsFileReader;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.gff.GffRecord;
import org.tgen.commons.utils.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class FindParalogsRunner implements Runnable {

    private File selfCoord; 
    private File refCoords; 
    private File out; 
    private File ref;

    public FindParalogsRunner(File selfCoords, File refCoords, File out, File ref) {
        this.selfCoord = selfCoords;
        this.refCoords = refCoords;
        this.out = out;
        this.ref = ref;
    }
    
    @Override
    public void run() {
        FindParalogs findParalogs = new FindParalogs();
        findParalogs.SELF_COORDS = selfCoord;
        findParalogs.REF_COORDS = refCoords;
        findParalogs.OUTPUT = out;
        findParalogs.REFERENCE_SEQUENCE = ref;
        findParalogs.doWork();
    }
}
