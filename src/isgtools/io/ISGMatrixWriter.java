/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools.io;

import isgtools.model.ISGMatrixHeader;
import isgtools.model.ISGMatrixRecord;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixWriter {

    private PrintWriter pw;
    private ISGMatrixHeader header;

    public ISGMatrixWriter(File file) throws IOException {
        pw = new PrintWriter(new FileWriter(file));
    }

    public void writerHeader(ISGMatrixHeader header) {
        this.header = header;
        pw.println(String.format("#%s=%d", ISGMatrixHeader.NUM_SAMPLES, header.getNSamples()));
        pw.print(ISGMatrixHeader.CHROM);
        pw.print("\t");
        pw.print(ISGMatrixHeader.POS);
        pw.print("\t");
        pw.print(ISGMatrixHeader.REF);

        for (String str : header.getSampleNames()) {
            pw.print("\t");
            pw.print(str);
        }

        for (String additionalInfo : header.getAdditionalInfo()) {
            pw.print("\t");
            pw.print(additionalInfo);
        }

        pw.println();
    }

    public void addRecord(ISGMatrixRecord r) {
        if (header == null) {
            throw new IllegalStateException("Header must be written before records.");
        }
        pw.print(r.getChrom());
        pw.print("\t");
        pw.print(r.getPos());
        pw.print("\t");
        pw.print(r.getRef());
        
        for (int i = 0; i < r.getNStates(); i++) {
            pw.print("\t");
            pw.print(r.getState(i));
        }
        
        for (String additionalInfo : header.getAdditionalInfo()) {
            pw.print("\t");
            pw.print(r.getAdditionalInfo(additionalInfo));
        }
        
        pw.println();
    }

    public void close() {
        pw.close();
    }
}
