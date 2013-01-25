/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.finddups;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public class FindDupsRunner implements Runnable {

    private File in;
    private File out;
    private File ref;

    public FindDupsRunner(File in, File out, File ref) {
        this.in = in;
        this.out = out;
        this.ref = ref;
    }
    
    private FindDups getInstance(){
        FindDups ret = new FindDups();
        ret.COORDS = in;
        ret.REFERENCE_SEQUENCE = ref;
        ret.OUTPUT = out;
        return ret;
    }

    @Override
    public void run() {
        if (out.exists() && out.length() > 0) {
            System.out.println("File exists: " + out.getAbsolutePath());
            return;
        }

        getInstance().doWork();
    }
    
}
