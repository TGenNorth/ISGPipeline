/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.util;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public class ProcessOutputHandlerSamFileWriterBuilder {

    private File samOrBamFile;
    private String RGID = "1";
    private String RGLB = ".";
    private String RGPL = ".";
    private String RGPU = ".";
    private String RGSM;

    public ProcessOutputHandlerSamFileWriterBuilder(File samOrBamFile) {
        this.samOrBamFile = samOrBamFile;
    }

    public ProcessOutputHandlerSamFileWriterBuilder RGID(String RGID) {
        this.RGID = RGID;
        return this;
    }

    public ProcessOutputHandlerSamFileWriterBuilder RGLB(String RGLB) {
        this.RGLB = RGLB;
        return this;
    }

    public ProcessOutputHandlerSamFileWriterBuilder RGPL(String RGPL) {
        this.RGPL = RGPL;
        return this;
    }

    public ProcessOutputHandlerSamFileWriterBuilder RGPU(String RGPU) {
        this.RGPU = RGPU;
        return this;
    }

    public ProcessOutputHandlerSamFileWriterBuilder RGSM(String RGSM) {
        this.RGSM = RGSM;
        return this;
    }

    public void SamOrBamFile(File samOrBamFile) {
        this.samOrBamFile = samOrBamFile;
    }
    
    public ProcessOutputHandlerSamFileWriter make(){
        return new ProcessOutputHandlerSamFileWriter(samOrBamFile, RGID, RGLB, RGPL, RGPU, RGSM);
    }
}
