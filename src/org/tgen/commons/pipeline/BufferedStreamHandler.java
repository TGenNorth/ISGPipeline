/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author jbeckstrom
 */
public class BufferedStreamHandler extends StreamHandler {

    private final StringBuilder buffer;
    
    public BufferedStreamHandler(StringBuilder buffer){
        this.buffer = buffer;
    }
    
    public BufferedStreamHandler(){
        this(new StringBuilder());
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
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public String getBuffer(){
        return buffer.toString();
    }
    
}
