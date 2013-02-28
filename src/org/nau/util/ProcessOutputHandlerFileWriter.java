/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 *
 * @author jbeckstrom
 */
public class ProcessOutputHandlerFileWriter extends ProcessOutputHandlerLineReader {

    private final PrintWriter pw;

    public ProcessOutputHandlerFileWriter(final File f) throws IOException {
        pw = new PrintWriter(new FileWriter(f));
    }

    @Override
    protected void write(final String message) {
        pw.println(message);
    }

    @Override
    protected void close() {
        super.close();
        pw.close();
    }
}
