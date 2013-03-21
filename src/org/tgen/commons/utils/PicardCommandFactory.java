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
public class PicardCommandFactory {
    
    public static List<String> createSortSamCommand(String java, File picard, File in, File out, File tmpDir){
        List<String> ret = new ArrayList<String>();
        ret.add(java);
        ret.add("-jar");
        ret.add(picard.getAbsolutePath()+"/SortSam.jar");
        ret.add("INPUT="+in.getAbsolutePath());
        ret.add("OUTPUT="+out.getAbsolutePath());
        ret.add("SO=coordinate");
        ret.add("VALIDATION_STRINGENCY=SILENT");
        ret.add("TMP_DIR="+tmpDir.getAbsolutePath());
        return ret;
    }
    
    public static List<String> createBuildBamIndexCommand(String java, File picard, File in, File out, File tmpDir){
        List<String> ret = new ArrayList<String>();
        ret.add(java);
        ret.add("-jar");
        ret.add(picard.getAbsolutePath()+"/BuildBamIndex.jar");
        ret.add("INPUT="+in.getAbsolutePath());
        ret.add("OUTPUT="+out.getAbsolutePath());
        ret.add("VALIDATION_STRINGENCY=SILENT");
        ret.add("TMP_DIR="+tmpDir.getAbsolutePath());
        return ret;
    }
    
    public static List<String> createRemoveDupCommand(String java, File picard, File in, File out, File metrics, File tmpDir){
        List<String> ret = new ArrayList<String>();
        ret.add(java);
        ret.add("-jar");
        ret.add(picard.getAbsolutePath()+"/MarkDuplicates.jar");
        ret.add("I="+in.getAbsolutePath());
        ret.add("O="+out.getAbsolutePath());
        ret.add("METRICS_FILE="+metrics.getAbsolutePath());
        ret.add("REMOVE_DUPLICATES=true");
        ret.add("VALIDATION_STRINGENCY=SILENT");
        ret.add("TMP_DIR="+tmpDir.getAbsolutePath());
        return ret;
    }
    
    public static List<String> createAddRGCommand(String java, File picard, File in, File out, String sortOrder, String RGID, String RGLB, String RGPL, String RGPU, String RGSM, File tmpDir){
        List<String> ret = new ArrayList<String>();
        ret.add(java);
        ret.add("-jar");
        ret.add(picard.getAbsolutePath()+"/AddOrReplaceReadGroups.jar");
        ret.add("INPUT="+in.getAbsolutePath());
        ret.add("OUTPUT="+out.getAbsolutePath());
        ret.add("VALIDATION_STRINGENCY=SILENT");
        ret.add("SO="+sortOrder);
        ret.add("RGID="+RGID);
        ret.add("RGLB="+RGLB);
        ret.add("RGPL="+RGPL);
        ret.add("RGPU="+RGPU);
        ret.add("RGSM="+RGSM);
        ret.add("TMP_DIR="+tmpDir.getAbsolutePath());
        return ret;
    }
    
    public static List<String> createMergeSamFilesCommand(String java, File picard, List<File> input, File output){
        List<String> ret = new ArrayList<String>();
        ret.add(java);
        ret.add("-jar");
        ret.add(picard.getAbsolutePath()+"/MergeSamFiles.jar");
        for(File in: input){
            ret.add("INPUT="+in.getAbsolutePath());
        }
        ret.add("OUTPUT="+output.getAbsolutePath());
        ret.add("VALIDATION_STRINGENCY=SILENT");
        return ret;
    }
    
}
