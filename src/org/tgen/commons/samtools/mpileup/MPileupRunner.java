/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.samtools.mpileup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tgen.commons.samtools.SamTools;

/**
 *
 * @author jbeckstrom
 */
public class MPileupRunner implements Runnable{

    private static final String version = "0.1.12a";

    private SamTools samtools;
    private File bam;

    private InputStream is;

    public MPileupRunner(SamTools samtools, File bam){
        if(!samtools.getVersion().equals(version))
            throw new IllegalArgumentException("Invalid version of samtools: "+samtools.getVersion());
        this.samtools = samtools;
        this.bam = bam;
    }

    public InputStream getInputStream(){
        return is;
    }

    public void run() {
        ProcessBuilder builder = new ProcessBuilder(samtools.getPath(),
                                                    "mpileup",
                                                    "-BQ0",
                                                    "-d10000000",
                                                    bam.getAbsolutePath());
//        builder.redirectErrorStream(true);
        Process process;
        try {
            process = builder.start();
            is = process.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(MPileupRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
