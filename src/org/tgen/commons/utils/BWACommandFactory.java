/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class BWACommandFactory {
    
    public static List<String> createAlignCommand(String bwa, String refPrefix, File readFile, File saiFile, boolean illumina){
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("aln");
        ret.add(refPrefix);
        ret.add(readFile.getAbsolutePath());
        ret.add("-t");
        ret.add("1");
        if(illumina)
            ret.add("-I");
        ret.add("-f");
        ret.add(saiFile.getAbsolutePath());
        return ret;
    }
    
    public static List<String> createSampeCommand(String bwa, String refPrefix, File read1, File read2, File sai1, File sai2, File sam){
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("sampe");
        ret.add("-f");
        ret.add(sam.getAbsolutePath());
        ret.add(refPrefix);
        ret.add(sai1.getAbsolutePath());
        ret.add(sai2.getAbsolutePath());
        ret.add(read1.getAbsolutePath());
        ret.add(read2.getAbsolutePath());
        return ret;
    }
    
    //Usage: bwa samse [-n max_occ] [-f out.sam] [-r RG_line] <prefix> <in.sai> <in.fq>
    public static List<String> createSamseCommand(String bwa, String refPrefix, File fq, File sai, File sam){
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("samse");
        ret.add("-f");
        ret.add(sam.getAbsolutePath());
        ret.add(refPrefix);
        ret.add(sai.getAbsolutePath());
        ret.add(fq.getAbsolutePath());
        return ret;
    }
}
