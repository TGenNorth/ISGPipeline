/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.gatk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.nau.util.ExternalProcess;

/**
 *
 * @author jbeckstrom
 */
public class CallableLociRunner implements Runnable{

    private final File input;
    private final File output;
    private final File summary;
    private final File ref;

    public CallableLociRunner(File input, File output, File ref, File summary) {
        this.input = input;
        this.output = output;
        this.summary = summary;
        this.ref = ref;
    }
    
    public boolean exists() {
        return (output.exists() && output.length() > 0);
    }

    private String[] createCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(System.getProperty("java.home") 
                + File.separator + "bin" 
                + File.separator + "java");
        ret.add("-cp");
        ret.add(System.getProperty("java.class.path"));
        ret.add("org.broadinstitute.sting.gatk.CommandLineGATK");
        ret.add("-T");
        ret.add("CallableLoci");
        ret.add("-I");
        ret.add(input.getAbsolutePath());
        ret.add("-R");
        ret.add(ref.getAbsolutePath());
        ret.add("-summary");
        ret.add(summary.getAbsolutePath());
        ret.add("-o");
        ret.add(output.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    @Override
    public void run() {
        if (exists()) {
            System.out.println(output.getAbsolutePath() + " already exists.");
            return;
        }
        String[] cmd = createCommand();
        ExternalProcess.execute(cmd);
    }
    
}
