/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.util;

import java.io.InputStream;

/**
 *
 * @author jbeckstrom
 */
public interface ProcessOutputHandler extends Runnable {

    public void setInputStream(final InputStream is);
    
}
