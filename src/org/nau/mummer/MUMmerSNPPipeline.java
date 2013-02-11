/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.util.ProcessExecutor;
import org.nau.util.ExternalProcess;
import org.tgen.commons.pipeline.CommandUtils;
import org.tgen.commons.utils.BWACommandFactory;
import org.tgen.commons.utils.PicardCommandFactory;

/**
 *
 * @author jbeckstrom
 */
public class MUMmerSNPPipeline implements Runnable {

    private final MummerEnv env;
    private final File refFasta;
    private final File qryFasta;
    private final File outDir;

    public MUMmerSNPPipeline(MummerEnv env, File ref, File qry, File outDir) {
        this.env = env;
        this.refFasta = ref;
        this.qryFasta = qry;
        this.outDir = outDir;
    }

    @Override
    public void run() {

        final String ref = stripExtension(refFasta);
        final String qry = stripExtension(qryFasta);
        final String prefix = ref + "_" + qry;

        File delta = new File(outDir, prefix + ".delta");
        File filter = new File(outDir, prefix + ".filter");
        File snps = new File(outDir, prefix + ".snps");

        //snp detection

        if (!exists(delta)) { //run nucmer
            String[] cmd = createNucmerCommand(prefix);
            ExternalProcess.execute(cmd, outDir);
        }

        if (!exists(filter)) { //run delta-filter
            String[] cmd = createDeltaFilterCommand(delta);
            ExternalProcess.execute(cmd, outDir, filter);
        }

        if (!exists(snps)) { //run show-snps
            String[] cmd = createShowSnpsCommand(filter);
            ExternalProcess.execute(cmd, outDir, snps);
        }
    }

    private String[] createNucmerCommand(String prefix) {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getNucmer().getAbsolutePath());
        ret.add("--prefix=" + prefix);
        ret.add(refFasta.getAbsolutePath());
        ret.add(qryFasta.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    private String[] createDeltaFilterCommand(File delta) {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getDeltaFilter().getAbsolutePath());
        ret.add("-r");
        ret.add("-q");
        ret.add(delta.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    private String[] createShowSnpsCommand(File filter) {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getShowSnps().getAbsolutePath());
        ret.add("-lrTI");
        ret.add(filter.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    private String stripExtension(File f) {
        return stripExtension(f.getName());
    }

    private String stripExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }

    private boolean exists(File f) {
        return f.exists() && f.length() > 0;
    }

    public static void main(String[] args) throws FileNotFoundException {
        MummerEnv env = new MummerEnv(
                new File("/Users/jbeckstrom/Documents/tgen/MUMmer3.22"),
                new File(""));
        File refFasta = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test/MSHR1043.fasta");
        File qryFasta = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test/MSHR1655.fasta");
        File out = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test");
        new MUMmerSNPPipeline(env, refFasta, qryFasta, out).run();
    }
}
