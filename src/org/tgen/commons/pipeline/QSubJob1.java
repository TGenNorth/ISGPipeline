/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbeckstrom
 */
public class QSubJob1 extends Job {

    public QSubJob1(Collection<String> commands, Map<String, String> env, File workingDir, StreamHandler errorHandler){
        super(commands, env, workingDir, new BufferedStreamHandler(), errorHandler);
    }
    
    public String getJobId() {
        return ((BufferedStreamHandler) outputHandler).getBuffer();
    }
    
    public static QSubJob1 create(File script, String name, List<String> commands, List<Job> dependencies, File wd) {
        String command = createCommand(commands);
        List<String> qsubCommands = wrapCommandInQsub(command, dependencies, name, script);
        Map<String, String> env = Collections.emptyMap();
        return new QSubJob1(qsubCommands, env, wd, new StandardStreamHandler());
    }
    
    public static String createCommand(List<String> commands){
        String ret = null;
        for (String str : commands) {
            if (ret == null) {
                ret = str;
            } else {
                ret += " " + str;
            }
        }
        return ret;
    }
    
    public static List<String> wrapCommandInQsub(String command, List<Job> dependencies, String name, File script) {
        List<String> ret = new ArrayList<String>();
        ret.add("qsub");
        ret.add("-N");
        ret.add(name);
        String dependencyStr = getDependenciesAsString(dependencies);
        if (dependencyStr!=null) {
            ret.add("-W");
            ret.add("depend=afterok:" + dependencyStr);
        }
        ret.add("-v");
        ret.add("command=\"" + command + "\"");
        ret.add(script.getAbsolutePath());
        return ret;
    }

    public static String getDependenciesAsString(List<Job> dependencies) {
        String ret = null;
        for (Job job : dependencies) {
            if (job instanceof QSubJob1) {
                
                QSubJob1 qSubJob = (QSubJob1) job;
                if (ret == null) {
                    ret = qSubJob.getJobId();
                } else {
                    ret += ":" + qSubJob.getJobId();
                }
            }
        }
        return ret;
    }

}
