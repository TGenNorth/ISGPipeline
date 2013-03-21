/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.io.InputStream;

/**
 *
 * @author jbeckstrom
 */
public abstract class StreamHandler implements Runnable {
    
    protected InputStream is;
    
    public StreamHandler(){
    }
    
    public void setInputStream(InputStream is){
        this.is = is;
    }
    
}
