/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.coverage.bam;

import java.io.File;
import org.nau.coverage.bam.FindCoverage;
/**
 *
 * @author jbeckstrom
 */
public class FindCoverageRunner implements Runnable{

    private final File bamFile;
    private final File outFile;
    private final int minCoverage;
    
    public FindCoverageRunner(File bamFile, File outFile, int minCoverage){
        this.bamFile = bamFile;
        this.outFile = outFile;
        this.minCoverage = minCoverage;
    }
    
    private FindCoverage createInstance(){
        FindCoverage findCoverage = new FindCoverage();
        findCoverage.BAM_FILE = bamFile;
        findCoverage.MIN_COVERAGE = minCoverage;
        if(outFile.isDirectory()){
            String name = bamFile.getName().substring(0, bamFile.getName().lastIndexOf("."));
            findCoverage.OUT_FILE = new File(outFile.getAbsolutePath()+"/"+name+".interval_list");
        }else{
            findCoverage.OUT_FILE = outFile;
        }
        return findCoverage;
    }
    
    @Override
    public void run() {
        if(outFile.exists() && outFile.length()>0){
            System.out.println("File exists: "+outFile.getAbsolutePath());
            return;
        }
        createInstance().doWork();
    }
    
}
