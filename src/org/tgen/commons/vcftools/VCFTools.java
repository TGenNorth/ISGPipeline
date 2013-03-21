/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.vcftools;

import org.tgen.commons.samtools.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.tgen.commons.samtools.tabix.Tabix;

/**
 *
 * @author jbeckstrom
 */
public class VCFTools {

    private File vcfTools;
    private Tabix tabix;
    
    private File perlDir;
    private File mergeVCF;
    

    public VCFTools(File vcfTools, Tabix tabix) throws IOException {
        this.vcfTools = vcfTools;
        this.tabix = tabix;
        init();
    }

    private void init() throws IOException {
        perlDir = new File(vcfTools.getAbsolutePath()+"/perl");
        if(!perlDir.isDirectory())
            throw new FileNotFoundException("Could not find perl directory: "+perlDir.getAbsolutePath());
        mergeVCF = new File(perlDir.getAbsolutePath()+"/vcf-merge");
        if(!mergeVCF.exists()){
            throw new FileNotFoundException("Could not find vcf-merge: "+mergeVCF.getAbsolutePath());
        }
            
    }

    public File getMergeVCF(){
        return mergeVCF;
    }
    
    public File getPerlDirectory(){
        return perlDir;
    }

    public Tabix getTabix() {
        return tabix;
    }
    
}
