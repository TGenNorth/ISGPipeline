/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.io.IOException;
import java.util.List;
import util.FileUtils;
import util.GenomicFileUtils;
import util.InterleavedFastqDetector;

/**
 *
 * @author jbeckstrom
 */
public class FASTQInputResource extends InputResource {
    
    private final File readsFile;
    private final File matesFile;
    private final boolean interleaved;
    
    public FASTQInputResource(String sample, File reads, boolean interleaved){
        super(sample);
        this.readsFile = reads;
        this.matesFile = null;
        this.interleaved = interleaved;
    }
    
    public FASTQInputResource(String sample, File reads, File mates){
        super(sample);
        this.readsFile = reads;
        this.matesFile = mates;
        this.interleaved = false;
    }
    
    public static FASTQInputResource create(File reads) throws IOException{
        final String sample = FileUtils.stripExtension(reads.getName());
        final boolean interleaved = InterleavedFastqDetector.isInterleaved(reads);
        return new FASTQInputResource(sample, reads, interleaved);
    }

    public boolean isInterleaved() {
        return interleaved;
    }
    
    public boolean isPaired(){
        return (interleaved || matesFile!=null);
    }

    public File getMatesFile() {
        return matesFile;
    }

    public File getReadsFile() {
        return readsFile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FASTQInputResource other = (FASTQInputResource) obj;
        if ((this.sample == null) ? (other.sample != null) : !this.sample.equals(other.sample)) {
            return false;
        }
        if (this.readsFile != other.readsFile && (this.readsFile == null || !this.readsFile.equals(other.readsFile))) {
            return false;
        }
        if (this.matesFile != other.matesFile && (this.matesFile == null || !this.matesFile.equals(other.matesFile))) {
            return false;
        }
        if (this.interleaved != other.interleaved) {
            return false;
        }
        return true;
    }

}
