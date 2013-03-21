/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.solsnp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tgen.commons.utils.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class SolSNPRunner implements Runnable {

    private File solsnp;
    private File inFile;
    private File outFile;
    private File refFile;
    private File optionsFile;
    private String outputFormat = "VCF";
    private String outputMode = "AllCallable";
    private String ploidy = "Haploid";
    private double filter = 0.85;

    public SolSNPRunner(File solsnp, File inFile, File outFile, File refFile) {
        this(solsnp, inFile, outFile, refFile, .85, null);
    }
    
    public SolSNPRunner(File solsnp, File inFile, File outFile, File refFile, double filter) {
        this(solsnp, inFile, outFile, refFile, filter, null);
    }

    public SolSNPRunner(File solsnp, File inFile, File outFile, File refFile, File optionsFile) {
        this(solsnp, inFile, outFile, refFile, .85, optionsFile);
    }

    public SolSNPRunner(File solsnp, File inFile, File outFile, File refFile, double filter, File optionsFile) {
        System.out.println("solSNPRunner");
        this.inFile = inFile;
        this.solsnp = solsnp;
        this.refFile = refFile;
        if (outFile.isDirectory()) {
            outFile = new File(outFile.getAbsolutePath() + "/" + FileUtils.getFilenameWithoutExtension(inFile) + ".vcf");
        }
        this.outFile = outFile;
        this.filter = filter;
        this.optionsFile = optionsFile;
    }

    private List<String> getCommands() {
        List<String> commands = new ArrayList<String>();
        commands.add(System.getProperty("java.home") + "/bin/java");
        commands.add("-jar");
        commands.add(solsnp.getAbsolutePath());
        commands.add("I=" + inFile.getAbsolutePath());
        commands.add("O=" + outFile.getAbsolutePath());
        commands.add("R=" + refFile.getAbsolutePath());
        if (optionsFile == null) {
            commands.add("OUTPUT_FORMAT=" + outputFormat);
            commands.add("OUTPUT_MODE=" + outputMode);
            commands.add("PLOIDY=" + ploidy);
            commands.add("FILTER=" + filter);
            commands.add("MINIMUM_COVERAGE=0");
        }else{
            commands.add("OPTIONS_FILE=" + optionsFile.getAbsolutePath());
        }

        return commands;
    }

    public void run() {
        if (outFile.exists() && outFile.length() > 0) {
            System.out.println("File exists: " + outFile.getAbsolutePath());
            return;
        }
        try {
            List<String> commands = getCommands();
            System.out.println(commands);
            ProcessBuilder pb = new ProcessBuilder(commands);
            Process p = pb.start();
            p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(SolSNPRunner.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SolSNPRunner.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.home"));
    }
}
