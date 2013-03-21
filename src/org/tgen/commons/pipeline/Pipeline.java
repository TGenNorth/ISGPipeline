/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Pipeline is backed by a queue, thus jobs must finish in the order in which
 * they are submitted. Once the pipeline begins, no more jobs will be accepted.
 * @author jbeckstrom
 */
public class Pipeline implements Runnable{

    private boolean started = false;
    private Queue<Job> jobQueue = new LinkedList<Job>();

    public Pipeline(){}
    
    public void submitJob(Job job) {
        if (started) {
            throw new IllegalStateException("Cannot submit job to pipeline after start has been called.");
        }
        jobQueue.add(job);
    }

    public void start() {
        started = true;
        while(!jobQueue.isEmpty()){
            jobQueue.poll().run();
        }
    }

    public void run() {
        start();
    }
}
