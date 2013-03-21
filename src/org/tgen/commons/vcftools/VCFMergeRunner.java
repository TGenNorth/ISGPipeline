/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.vcftools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.tgen.commons.samtools.tabix.BGZipRunner;
import org.tgen.commons.samtools.tabix.TabixRunner;

/**
 *
 * @author jbeckstrom
 */
public class VCFMergeRunner implements Runnable {

    private VCFTools vcfTools;
    private Collection<File> vcfFiles;
    private File outFile;
    private boolean overwrite;

    public VCFMergeRunner(VCFTools vcfTools, Collection<File> vcfFiles, File outFile) {
        this(vcfTools, vcfFiles, outFile, true);
    }
    
    public VCFMergeRunner(VCFTools vcfTools, Collection<File> vcfFiles, File outFile, boolean overwrite) {
        this.vcfTools = vcfTools;
        this.vcfFiles = vcfFiles;
        if (outFile.isDirectory()) {
            outFile = new File(outFile.getAbsolutePath() + "/merge.vcf");
        }
        this.outFile = outFile;
        this.overwrite = overwrite;
    }

    private List<String> getCommands(List<File> input) {
        List<String> commands = new ArrayList<String>();
        commands.add(vcfTools.getMergeVCF().getAbsolutePath());
        commands.add("-d");
        commands.add("-s");
        for (File i : input) {
            commands.add(i.getAbsolutePath());
        }
        return commands;
    }

    private List<File> prepareFiles() {
        List<File> ret = new ArrayList<File>();
        for (File vcfFile : vcfFiles) {
            File zippedFile = new File(vcfFile.getAbsolutePath() + ".gz");
//            if (!zippedFile.exists() || zippedFile.length() == 0) {
            BGZipRunner bgzipRunner = new BGZipRunner(vcfTools.getTabix(), vcfFile, zippedFile);
            bgzipRunner.run();
            TabixRunner tabixRunner = new TabixRunner(vcfTools.getTabix(), zippedFile);
            tabixRunner.run();
//            }
            ret.add(zippedFile);
        }
        return ret;
    }

    public void run() {
        if(!overwrite && outFile.exists() && outFile.length()>0) return;
        try {
            List<String> commands = getCommands(prepareFiles());
            
            ProcessBuilder pb = new ProcessBuilder(commands);
            pb.directory(vcfTools.getPerlDirectory());
            Map<String, String> env = pb.environment();
            env.put("PATH", env.get("PATH") + ":" + vcfTools.getTabix().getTabixDir().getAbsolutePath());
            System.out.println(env);
            System.out.println(commands);
//            pb.redirectErrorStream(true);
            Process p = pb.start();
            FileUtils.copyInputStreamToFile(p.getInputStream(), outFile);
        } catch (IOException ex) {
            Logger.getLogger(VCFMergeRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
