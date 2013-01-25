/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.solsnp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tgen.commons.utils.FileUtils;
import org.tgen.sol.SNP.SolSNP;

/**
 *
 * @author jbeckstrom
 */
public class SolSNPRunner implements Runnable {

    private File inFile;
    private File outFile;
    private File refFile;
    private File optionsFile;
    private String outputFormat = "VCF";
    private String outputMode = "VariantsAndNonReference";
    private String ploidy = "Haploid";
    private double filter = 0.85;

    public SolSNPRunner(File inFile, File outFile, File refFile) {
        this(inFile, outFile, refFile, .85, null);
    }

    public SolSNPRunner(File inFile, File outFile, File refFile, double filter) {
        this(inFile, outFile, refFile, filter, null);
    }

    public SolSNPRunner(File inFile, File outFile, File refFile, File optionsFile) {
        this(inFile, outFile, refFile, .85, optionsFile);
    }

    public SolSNPRunner(File inFile, File outFile, File refFile, double filter, File optionsFile) {
        this.inFile = inFile;
        this.refFile = refFile;
        if (outFile.isDirectory()) {
            outFile = new File(outFile.getAbsolutePath() + "/" + FileUtils.getFilenameWithoutExtension(inFile) + ".vcf");
        }
        this.outFile = outFile;
        this.filter = filter;
        this.optionsFile = optionsFile;
    }

    private String[] getArguments() {
        List<String> commands = new ArrayList<String>();
        commands.add("I=" + inFile.getAbsolutePath());
        commands.add("O=" + outFile.getAbsolutePath());
        commands.add("R=" + refFile.getAbsolutePath());
        commands.add("OUTPUT_FORMAT=" + outputFormat);
        commands.add("OUTPUT_MODE=" + outputMode);
        commands.add("PLOIDY=" + ploidy);
        commands.add("FILTER=" + filter);
        commands.add("MINIMUM_COVERAGE=0");
        commands.add("VALIDATION_STRINGENCY=LENIENT");
        if (optionsFile != null) {
            commands.add("OPTIONS_FILE=" + optionsFile.getAbsolutePath());
        }
        return commands.toArray(new String[commands.size()]);
    }

    @Override
    public void run() {
        if (outFile.exists() && outFile.length() > 0) {
            System.out.println("File exists: " + outFile.getAbsolutePath());
            return;
        }

        String[] argv = getArguments();
        new SolSNP().instanceMain(argv);
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.home"));
    }
}
