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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbeckstrom
 */
public class QSubJob {

    private File script;
    private String name;
    private String command;
    private List<String> dependencies;

    public QSubJob(File script, String name, List<String> commands) {
        this.script = script;
        this.name = name;
        for (String str : commands) {
            if (command == null) {
                command = str;
            } else {
                command += " " + str;
            }
        }
    }

    public QSubJob(File script, String name, List<String> commands, List<String> dependencies) {
        this.script = script;
        this.name = name;
        this.dependencies = dependencies;
        for (String str : commands) {
            if (command == null) {
                command = str;
            } else {
                command += " " + str;
            }
        }
    }

    private List<String> getCommands() {
        List<String> ret = new ArrayList<String>();
        ret.add("qsub");
        ret.add("-N");
        ret.add(name);
        if(dependencies!=null && dependencies.size()>0){
            ret.add("-W");
            ret.add("depend=afterok:"+getDependenciesAsString());
        }
        ret.add("-v");
        ret.add("command=\"" + command + "\"");
        ret.add(script.getAbsolutePath());
        return ret;
    }
    
    private String getDependenciesAsString(){
        String ret = null;
        for (String str : dependencies) {
            if (ret == null) {
                ret = str;
            } else {
                ret += ":" + str;
            }
        }
        return ret;
    }

    public String start() {
        String jobid = null;
        List<String> commands = getCommands();
        System.out.println( commands );
        ProcessBuilder pb = new ProcessBuilder(commands);
        try {
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            jobid = reader.readLine();
            if(jobid!=null) jobid = jobid.trim();
        } catch (IOException ex) {
            Logger.getLogger(QSubJob.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobid;
    }
}
