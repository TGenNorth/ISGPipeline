/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.samtools.mpileup;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tgen.commons.samtools.SamTools;

/**
 *
 * @author jbeckstrom
 */
public class MPileupReader {

    private BufferedReader reader;
    private int lineCount;

    public MPileupReader(File bam, SamTools samtools) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(samtools.getPath(),
                "mpileup",
                "-BQ0",
                "-d10000000",
                bam.getAbsolutePath());
        reader = new BufferedReader(new InputStreamReader(builder.start().getInputStream()));
    }

    public MPileupReader(InputStream is) throws IOException {
        reader = new BufferedReader(new InputStreamReader(is));
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public String advanceLine(){
        String line = null;
        try {
            line = reader.readLine();
            lineCount++;
            if(lineCount%1000000==0){
                System.out.println(lineCount);
            }
        } catch (IOException ex) {
            Logger.getLogger(MPileupReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return line;
    }

    public MPileupRecord next() {
        MPileupRecord ret = null;
        String line = advanceLine();
        
        if (line != null) {

            String[] split = line.split("\t");
            String chr = split[0];
            int pos = Integer.parseInt(split[1]);
            int count = Integer.parseInt(split[3]);
            ret = new MPileupRecord(chr, pos, count);

        }


        return ret;
    }
}
