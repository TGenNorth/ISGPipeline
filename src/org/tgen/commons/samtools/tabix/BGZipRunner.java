/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.samtools.tabix;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class BGZipRunner implements Runnable{
    
    private File inFile;
    private File outFile;
    private Tabix tabix;
    
    public BGZipRunner(Tabix tabix, File inFile, File outFile){
        this.tabix = tabix;
        this.inFile = inFile;
        this.outFile = outFile;
    }
    
    public BGZipRunner(Tabix tabix, File inFile){
        this(tabix, inFile, null);
    }
    
    private List<String> getCommands(){
        List<String> ret = new ArrayList<String>();
        if(outFile==null){
            ret.add(tabix.getBGZip().getAbsolutePath()); 
            ret.add(inFile.getAbsolutePath());
        }else{
           ret.add(tabix.getBGZip().getAbsolutePath()); 
           ret.add("-c"); 
           ret.add(inFile.getAbsolutePath()); 
        }
        return ret;
    }

    public void run() {
        try {
            List<String> commands = getCommands();
            System.out.println(commands);
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            FileUtils.copyInputStreamToFile(p.getInputStream(), outFile);
        } catch (IOException ex) {
            Logger.getLogger(BGZipRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
