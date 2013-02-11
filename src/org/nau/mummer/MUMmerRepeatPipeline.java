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
public class MUMmerRepeatPipeline implements Runnable {

    private final MummerEnv env;
    private final File seqFasta;
    private final File outDir;
    private File delta;
    private File coords;

    public MUMmerRepeatPipeline(MummerEnv env, File seq, File outDir) {
        this.env = env;
        this.seqFasta = seq;
        this.outDir = outDir;
    }

    public File getCoords(){
        return coords;
    }
    
    @Override
    public void run() {
        
        final String ref = stripExtension(seqFasta);
        String prefix = ref + "_" + ref;
        
        delta = new File(outDir, prefix + ".delta");
        coords = new File(outDir, prefix + ".coords");
        
        //identify repeats

        if (!exists(delta)) { //run nucmer
            String[] cmd = createNucmerCommand(prefix);
            ExternalProcess.execute(cmd, outDir);
        }

        if (!exists(coords)) { //run show-coords
            String[] cmd = createShowCoordsCommand(delta);
            ExternalProcess.execute(cmd, outDir, coords);
        }
    }

    private String[] createNucmerCommand(String prefix) {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getNucmer().getAbsolutePath());
        ret.add("--maxmatch");
        ret.add("--nosimplify");
        ret.add("--prefix=" + prefix);
        ret.add(seqFasta.getAbsolutePath());
        ret.add(seqFasta.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    private String[] createShowCoordsCommand(File delta) {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getShowCoords().getAbsolutePath());
        ret.add("-r");
        ret.add(delta.getAbsolutePath());
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
        new MUMmerRepeatPipeline(env, refFasta, out).run();
    }
}
