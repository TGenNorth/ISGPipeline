/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.samtools.tabix;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
/**
 *
 * @author jbeckstrom
 */
public class TabixRunner implements Runnable{

    private File inFile;
    private Tabix tabix;
    
    private String preset = "vcf";
    
    public TabixRunner(Tabix tabix, File inFile){
        this.tabix = tabix;
        this.inFile = inFile;
    }
    
    private List<String> getCommands(){
        return Arrays.asList(tabix.getTabix().getAbsolutePath(), "-p", preset, inFile.getAbsolutePath());
    }

    public void run() {
        try {
            List<String> commands = getCommands();
            System.out.println(commands);
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = null;
            while((line = reader.readLine())!=null){
                System.out.println(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(TabixRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
