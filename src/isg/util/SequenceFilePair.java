/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SequenceFilePair other = (SequenceFilePair) obj;
        if (this.seq1 != other.seq1 && (this.seq1 == null || !this.seq1.equals(other.seq1))) {
            return false;
        }
        if (this.seq2 != other.seq2 && (this.seq2 == null || !this.seq2.equals(other.seq2))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.seq1 != null ? this.seq1.hashCode() : 0);
        hash = 23 * hash + (this.seq2 != null ? this.seq2.hashCode() : 0);
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "SequenceFilePair{" + "seq1=" + seq1 + ", seq2=" + seq2 + ", name=" + name + '}';
    }
    
    
}
