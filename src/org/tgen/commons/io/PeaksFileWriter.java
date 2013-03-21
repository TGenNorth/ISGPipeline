/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.tgen.commons.feature.PeakFeature;

/**
 *
 * @author jbeckstrom
 */
public class PeaksFileWriter {

    private PrintWriter pw;

    public PeaksFileWriter(File f) throws IOException {
        pw = new PrintWriter(new FileWriter(f));
        writeHeader();
    }

    private void writeHeader() {
        pw.print("chr");
        pw.print("\t");
        pw.print("start");
        pw.print("\t");
        pw.print("end");
        pw.print("\t");
        pw.print("length");
        pw.print("\t");
        pw.print("median");
        pw.print("\t");
        pw.print("maxCov");
        pw.println();
    }

    public void writePeak(PeakFeature peak) {
        pw.print(peak.getChr());
        pw.print("\t");
        pw.print(peak.getStart());
        pw.print("\t");
        pw.print(peak.getEnd());
        pw.print("\t");
        pw.print(peak.getLength());
        pw.print("\t");
        pw.print(peak.getMedian());
        pw.print("\t");
        pw.print(peak.getMaxCov());
        pw.println();
    }
    
    public void close(){
        pw.close();
    }
}
