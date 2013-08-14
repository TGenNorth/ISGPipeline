/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import isg.util.SequenceFilePairPattern;
import isg.util.SequenceFilePairPatterns;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class FASTQInputResources {
    
    private final List<FASTQInputResource> resources = new ArrayList<FASTQInputResource>();
    private final Map<String, File> fastqsInWaiting = new HashMap<String, File>();
    private final SequenceFilePairPatterns patterns;
    
    public FASTQInputResources(SequenceFilePairPatterns patterns){
        this.patterns = patterns;
    }
    
    public void addFile(File fastq) throws IOException {
        SequenceFilePairPattern pattern = patterns.findPattern(fastq);
        if (pattern == null) {
            //single end
            resources.add(FASTQInputResource.create(fastq));
        } else {
            //try to find matching pairs
            final String filename = fastq.getName();
            if (fastqsInWaiting.containsKey(filename)) {
                final File fastq2 = fastqsInWaiting.remove(filename);
                final String sample = pattern.sample(fastq);
                if (pattern.isFirst(fastq2)) {
                    resources.add(new FASTQInputResource(sample, fastq2, fastq));
                } else {
                    resources.add(new FASTQInputResource(sample, fastq, fastq2));
                }
            } else {
                //matching pair doesn't exist yet, so store it for later
                final String otherFilename = pattern.other(fastq).getName();
                fastqsInWaiting.put(otherFilename, fastq);
            }
        }
    }
    
    public List<FASTQInputResource> getResources(){
        return resources;
    }
    
    public boolean isPending(){
        return !fastqsInWaiting.isEmpty();
    }
}
