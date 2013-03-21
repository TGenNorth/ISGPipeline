/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.tgen.commons.feature.Locus;

/**
 *
 * @author jbeckstrom
 */
public class BedFileWriter {
    
    private PrintWriter pw;

    public BedFileWriter(File f, boolean skipHeader) throws IOException {
        pw = new PrintWriter(new FileWriter(f));
        if(!skipHeader)
            writeHeader();
    }

    private void writeHeader() {
        pw.print("chrom");
        pw.print("\t");
        pw.print("chromStart");
        pw.print("\t");
        pw.print("chromEnd");
        pw.println();
    }

    public void write(Locus locus) {
        pw.print(locus.getChr());
        pw.print("\t");
        pw.print(locus.getStart());
        pw.print("\t");
        pw.print(locus.getEnd());
        pw.println();
    }
    
    public void close(){
        pw.close();
    }
    
}
