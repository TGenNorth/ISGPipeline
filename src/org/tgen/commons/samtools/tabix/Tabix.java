/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.samtools.tabix;

import org.tgen.commons.vcftools.*;
import org.tgen.commons.samtools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jbeckstrom
 */
public class Tabix {

    private File tabixDir;

    private File tabix;
    private File bgzip;
    

    public Tabix(File tabixDir) throws IOException {
        this.tabixDir = tabixDir;
        init();
    }

    private void init() throws IOException {
        tabix = new File(tabixDir.getAbsolutePath()+"/tabix");
        if(!tabix.exists())
            throw new FileNotFoundException("Could not find tabix binary: "+tabix.getAbsolutePath());
        bgzip = new File(tabixDir.getAbsolutePath()+"/bgzip");
        if(!bgzip.exists())
            throw new FileNotFoundException("Could not find bgzip binary: "+bgzip.getAbsolutePath());
    }

    public File getBGZip() {
        return bgzip;
    }

    public File getTabix() {
        return tabix;
    }

    public File getTabixDir() {
        return tabixDir;
    }
    
}
