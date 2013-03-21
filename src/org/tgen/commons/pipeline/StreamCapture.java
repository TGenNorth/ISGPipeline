/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbeckstrom
 */
class StreamCapture implements Runnable {
    private final StringBuilder buffer;
    private final InputStream is;

    public StreamCapture(StringBuilder buffer, InputStream is) {
        this.buffer = buffer;
        this.is = is;
    }

    public void run() {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(StreamCapture.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(StreamCapture.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
