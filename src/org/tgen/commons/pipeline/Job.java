/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jbeckstrom
 */
public class Job implements Runnable{
    
    protected List<Job> dependencies = new ArrayList<Job>();
    private boolean finished;
    private boolean running;
    protected int exitStatus = -1;
    protected final StreamHandler outputHandler;
    protected final StreamHandler errorHandler;
    private final List<String> commands = new ArrayList<String>();
    private final Map<String, String> env;
    private final File workingDir;
    
    public Job(Collection<String> commands){
        this(commands, new HashMap<String, String>(), new File("."));
    }
    
    public Job(Collection<String> commands, File wd){
        this(commands, new HashMap<String, String>(), wd);
    }
    
    public Job(Collection<String> commands, Map<String, String> env){
        this(commands, env, new File("."));
    }
    
    public Job(Collection<String> commands, Map<String, String> env, File workingDir){
        this(commands, env, new File("."), new StandardStreamHandler(), new StandardStreamHandler());
    }
    
    public Job(Collection<String> commands, Map<String, String> env, File workingDir, StreamHandler outputHandler, StreamHandler errorHandler){
        this.outputHandler = outputHandler;
        this.errorHandler = errorHandler;
        this.commands.addAll(commands);
        this.env = env;
        this.workingDir = workingDir;
        
        if(!workingDir.isDirectory()){
            workingDir.mkdirs();
        }
    }
    
    public void addDependency(Job job){
        dependencies.add(job);
    }
    
    public void addAllDependencies(Collection<Job> dependenciesToAdd){
        for(Job dependency: dependenciesToAdd){
            addDependency(dependency);
        }
    }
    
    public boolean isReady(){
        for(Job dependency: dependencies){
            if(!dependency.isFinished()) return false;
        }
        return true;
    }
    
    public boolean isRunning(){
        return running;
    }
    
    public boolean isFinished(){
        return finished;
    }
    
    public int getExitStatus(){
        return exitStatus;
    }
    
    private void start() {
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(workingDir);
        pb.environment().putAll(env);
        try {
            Process p = pb.start();
            outputHandler.setInputStream(p.getInputStream());
            errorHandler.setInputStream(p.getErrorStream());
            new Thread( outputHandler ).start();
            new Thread( errorHandler ).start();
            exitStatus = p.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Job.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        running = true;
        start();
        running = false;
        finished = true;
    }
    
}
