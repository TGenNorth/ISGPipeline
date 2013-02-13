/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.bwamatch;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePair {
    
    private File seq1, seq2;
    private String name;
    
    public SequenceFilePair(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
    
    void setName(String name) {
        this.name = name;
    }

    public File getSeq1() {
        return seq1;
    }

    public void setSeq1(File seq1) {
        this.seq1 = seq1;
    }

    public File getSeq2() {
        return seq2;
    }

    public void setSeq2(File seq2) {
        this.seq2 = seq2;
    }
}
