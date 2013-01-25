/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.coverage.coords;

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
public class CoordsCoverageRunner implements Runnable {

    private File coords;
    private File out;

    public CoordsCoverageRunner(File coords, File out) throws IOException {
        this.coords = coords;
        if(out.isDirectory()){
            String name = FileUtils.getFilenameWithoutExtension(coords);
            this.out = new File(out.getAbsolutePath()+"/"+name+".gff");
        }else{
            this.out = out;
        }
    }
    
    @Override
    public void run() {
        PrintWriter pw = null;
        if(out.exists() && out.length()>0) return;
        try {
            pw = new PrintWriter(new FileWriter(out));
            CoordsFileReader reader = new CoordsFileReader(coords);
            CoordsRecord cr = null;
            while ((cr = reader.next()) != null) {
                GffRecord gff = createGffRecord(cr);
                pw.println(gff.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(CoordsCoverageRunner.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }

    private GffRecord createGffRecord(CoordsRecord cr) {
        Coord c1 = cr.getCoord(0);
        Coord c2 = cr.getCoord(1);
        GffRecord gff = new GffRecord();
        gff.setStart(c1.getStart());
        gff.setEnd(c1.getEnd());
        gff.setAttribute("i", c1.getChr());
        return gff;
    }
}
