/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePairMatcher {
    
    private List<SequenceFilePairPattern> patterns = new ArrayList<SequenceFilePairPattern>();
    
    public SequenceFilePairMatcher() {
        this(Arrays.asList(new SequenceFilePairPattern("(.*)_[0-9]+_([0-9])_sequence\\..*"),
                new SequenceFilePairPattern("(.*)_[ATCG]+_L[0-9]+_R([0-9])_[0-9]+\\..*"),
                new SequenceFilePairPattern("(.*)_S[0-9]+_L[0-9]+_R([0-9])_[0-9]+\\..*"),
                new SequenceFilePairPattern("(.*)_R([0-9])_[0-9]+\\..*"),
                new SequenceFilePairPattern("(.*)_R([0-9])\\..*"),
                new SequenceFilePairPattern("(.*)_([12])\\..*")));
    }
    
    public SequenceFilePairMatcher(List<SequenceFilePairPattern> patterns) {
        this.patterns.addAll(patterns);
    }
    
    public Collection<SequenceFilePair> process(File seqDir) {
        Collection<File> files = FileUtils.listFiles(seqDir, new String[]{"gz", "fastq", "txt"}, false);
        return process(files);
    }
    
    public Collection<SequenceFilePair> process(Collection<File> files) {
        
        Map<String, SequenceFilePair> sequenceFilePairs = new HashMap<String, SequenceFilePair>();
        Set<File> visited = new HashSet<File>();
        for (File f : files) {
            
            if (visited.contains(f)) { //don't repeat files
                continue;
            }
            
            SequenceFilePair sfp = getPair(f);

            //save files already used
            visited.add(sfp.getSeq1());
            if (sfp.getSeq2() != null) {
                visited.add(sfp.getSeq2());
            }

            //generate unique key
            String key = sfp.getName();
            int count = 1;
            if (sequenceFilePairs.containsKey(key)) {
                key = sfp.getName() + "-" + count;
                count++;
            }
            
            sfp.setName(key);
            sequenceFilePairs.put(key, sfp);
        }
        
        return new ArrayList<SequenceFilePair>(sequenceFilePairs.values());
    }
    
    private SequenceFilePair getPair(File first) {
        
        String str = first.getName();
        
        for (SequenceFilePairPattern pattern : patterns) { //try matching all patterns

            if (pattern.matches(first)) {
                
                String sampleName = pattern.sample(first);
                File second = pattern.other(first);
                
                SequenceFilePair sfp = new SequenceFilePair(sampleName);
                sfp.setSeq1(first);
                sfp.setSeq2(second);
                
                return sfp;
                
            }
        }
        
        String sampleName = first.getName().substring(0, first.getName().indexOf('.'));
        SequenceFilePair sfp = new SequenceFilePair(sampleName);
        sfp.setSeq1(first);
        return sfp;
    }
}
