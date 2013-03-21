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
import org.tgen.commons.feature.PeakFeature;

/**
 *
 * @author jbeckstrom
 */
public class PeaksFileReader {

    private BufferedReader reader;
    private String header;

    public PeaksFileReader(File file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
        readHeader();
    }

    private void readHeader() {
        header = advanceLine();
    }

    public String advanceLine() {
        String ret = null;
        try {
            ret = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(PeaksFileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public PeakFeature nextPeak() {
        PeakFeature ret = null;
        String line = advanceLine();
        if (line != null && line.length()>0) {
            String[] split = line.split("\t");
            String chr = split[0];
            int start = Integer.parseInt(split[1]);
            int end = Integer.parseInt(split[2]);
            int length = Integer.parseInt(split[3]);
            int median = Integer.parseInt(split[4]);
            int maxCov = Integer.parseInt(split[5]);
            ret = new PeakFeature(chr, start, end, maxCov, median);
        }
        
        return ret;
    }
}
