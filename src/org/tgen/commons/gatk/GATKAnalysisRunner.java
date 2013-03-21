/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.gatk;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.util.ProcessExecutor;
import org.broadinstitute.sting.gatk.CommandLineGATK;

/**
 *
 * @author jbeckstrom
 */
public abstract class GATKAnalysisRunner implements Runnable {

    protected final File in;
    protected final File ref;
    //path to gatk jar
    private final String gatk;

    public GATKAnalysisRunner(String gatk, File in, File ref) {
        this.gatk = gatk;
        this.in = in;
        this.ref = ref;
    }

    public void run() {
        if (beforeRun()) {
            ProcessExecutor.execute(getCommand());
        }
    }

    private String[] getCommand() {
        List<String> command = new ArrayList<String>();
        command.add(System.getProperty("java.home") + "/bin/java");
        command.add("-jar");
        command.add(gatk);
        command.add("-T");
        command.add(getAnalysisType());
        command.add("-R");
        command.add(ref.getAbsolutePath());
        command.add("-I");
        command.add(in.getAbsolutePath());
        command.addAll(getArguments());
        return command.toArray(new String[0]);
    }

    private String[] argv() {
        final List<String> args = new ArrayList<String>();
        args.add("-T");
        args.add(getAnalysisType());
        args.add("-R");
        args.add(ref.getAbsolutePath());
        args.add("-I");
        args.add(in.getAbsolutePath());
        args.addAll(getArguments());
        return args.toArray(new String[args.size()]);
    }

    protected void addArg(List<String> args, String key, String value) {
        args.add(key);
        args.add(value);
    }

    protected void addArg(List<String> args, String key, int value) {
        if (value != -1) {
            addArg(args, key, Integer.toString(value));
        }
    }

    protected abstract boolean beforeRun();

    protected abstract String getAnalysisType();

    protected abstract List<String> getArguments();
}
