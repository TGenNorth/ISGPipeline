/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.PicardException;

/**
 * Runnable that reads off the given stream and logs it somewhere.
 */
/**
 *
 * @author jbeckstrom
 */
abstract class ProcessOutputHandlerLineReader implements ProcessOutputHandler {
    private BufferedReader reader;

    @Override
    public void setInputStream(InputStream is) {
        reader = new BufferedReader(new InputStreamReader(is));
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                write(line);
            }
        } catch (IOException e) {
            throw new PicardException("Unexpected exception reading from process stream", e);
        } finally {
            close();
        }
    }

    protected abstract void write(String line);

    protected void close() {
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(ExternalProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
