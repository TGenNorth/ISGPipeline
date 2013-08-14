/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePairPatterns {
    
    private List<SequenceFilePairPattern> patterns = new ArrayList<SequenceFilePairPattern>();
    
    public SequenceFilePairPatterns(){}
    
    public SequenceFilePairPatterns(List<SequenceFilePairPattern> patterns){
        this.patterns.addAll(patterns);
    }
    
    public void addPattern(SequenceFilePairPattern pattern){
        patterns.add(pattern);
    }
    
    public SequenceFilePairPattern findPattern(File file){
        for (SequenceFilePairPattern pattern : patterns) {
            if (pattern.matches(file)) {
                return pattern;
            }
        }
        return null;
    }
}
