/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Uses a regex to identify the sample name in a filename. One example 
 * of usage would be for a reads file produced by Illumina where the sample name
 * is a prefix of the filename.
 * 
 * @author jbeckstrom
 */
public class RegexSampleNameDetectionAlgorithm implements SampleNameDetectionAlgorithm {

    private final Pattern pattern;
    private final int group;
    
    public RegexSampleNameDetectionAlgorithm(String regex, int group){
        this.pattern = Pattern.compile(regex);
        this.group = group;
    }
    
    @Override
    public boolean detectable(File f) {
        return pattern.matcher(f.getName()).matches();
    }

    @Override
    public String apply(File f) {
        if (!detectable(f)) {
            throw new IllegalArgumentException("Cannot detect a pair for file: " + f.getPath() + " using pattern: "+pattern.pattern());
        }
        final Matcher m = pattern.matcher(f.getName());
        m.find();
        return m.group(group);
    }
    
}
