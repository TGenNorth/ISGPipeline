/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tgen.commons.feature.Locus;

/**
 *
 * @author jbeckstrom
 */
public class BedFileReader {
    
    private BufferedReader reader;
    private String[] header;

    public BedFileReader(File file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
        readHeader();
    }

    private void readHeader() {
        header = advanceLine().split("\t");
        if(!header[0].equalsIgnoreCase("chrom") ||
                !header[1].equalsIgnoreCase("chromStart") ||
                !header[2].equalsIgnoreCase("chromEnd")){
            throw new IllegalStateException("Could not find valid BED header");
        }
    }

    public String advanceLine() {
        String ret = null;
        try {
            ret = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(BedFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public Locus nextLocus() {
        Locus ret = null;
        String line = advanceLine();
        if (line != null && line.length()>0) {
            String[] split = line.split("\t");
            String chr = split[0];
            int start = Integer.parseInt(split[1]);
            int end = Integer.parseInt(split[2]);
            ret = new Locus(chr, start, end);
        }
        
        return ret;
    }
    
}
