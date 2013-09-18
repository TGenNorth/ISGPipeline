/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import isg.util.SequenceFilePairPattern;
import isg.util.SequenceFilePairPatterns;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.broadinstitute.sting.utils.collections.Pair;
import util.FileUtils;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class FastqInputResourceFactory implements InputResourceFactory {

    private final Map<String, File> fastqsInWaiting = new HashMap<String, File>();
    private final SequenceFilePairPatterns patterns;

    public FastqInputResourceFactory(SequenceFilePairPatterns patterns) {
        this.patterns = patterns;
    }
    
    @Override
    public boolean isResourceType(File f) {
        final String filename = f.getName();
        return (filename.endsWith(".fastq") 
                || filename.endsWith(".gz")
                || filename.endsWith(".txt"));
    }

    @Override
    public InputResource<?> create(File fastq) {
        SequenceFilePairPattern pattern = patterns.findPattern(fastq);
        if (pattern == null) {
            //single end
            final String sampleName = FileUtils.stripExtension(fastq);
            return new FastqInputResource(sampleName, fastq);
        } else {
            //try to find matching pairs
            final String filename = fastq.getName();
            if (fastqsInWaiting.containsKey(filename)) {
                final File fastq2 = fastqsInWaiting.remove(filename);
                final String sampleName = pattern.sample(fastq);
                Pair<File, File> pair = null;
                if (pattern.isFirst(fastq2)) {
                    pair = new Pair<File, File>(fastq2, fastq);
                } else {
                    pair = new Pair<File, File>(fastq, fastq2);
                }
                return new FastqPairInputResource(sampleName, pair);
            } else {
                //matching pair doesn't exist yet, so store it for later
                final String otherFilename = pattern.other(fastq).getName();
                fastqsInWaiting.put(otherFilename, fastq);
                return null;
            }
        }
    }
    
}
