/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import isg.detector.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePairPattern {

    private final Pattern pattern;
    private final String first; 
    private final String second;
    private final int sampleGroup;
    private final int readGroup;

    public SequenceFilePairPattern(final String regex) {
        this(regex, "1", "2", 1, 2);
    }

    public SequenceFilePairPattern(final String regex, final String first, final String second) {
        this(regex, first, second, 1, 2);
    }

    public SequenceFilePairPattern(final String regex, final String first, final String second, final int sampleGroup, final int readGroup) {
        this.pattern = Pattern.compile(regex);
        this.first = first;
        this.second = second;
        this.sampleGroup = sampleGroup;
        this.readGroup = readGroup;
    }
    
    public String sample(File f){
        if (!matches(f)) {
            throw new IllegalArgumentException("Cannot detect a pair for file: " + f.getPath() + " using pattern: "+pattern.pattern());
        }
        final Matcher m = pattern.matcher(f.getName());
        m.find();

        return m.group(sampleGroup);
    }

    public File other(File f) {
        if (!matches(f)) {
            throw new IllegalArgumentException("Cannot detect a pair for file: " + f.getPath() + " using pattern: "+pattern.pattern());
        }
        final Matcher m = pattern.matcher(f.getName());
        m.find();

        String before = m.group(readGroup);
        String after = first.equals(before) ? second : first;
        
        StringBuilder sb = new StringBuilder(f.getName());
        sb.replace(m.start(readGroup), m.start(readGroup)+before.length(), after);
        
        return new File(f.getParentFile(), sb.toString());
    }
    
    public boolean isFirst(File f){
        return readGroupMatches(f, first);
    }
    
    public boolean isSecond(File f){
        return readGroupMatches(f, second);
    }
    
    private boolean readGroupMatches(File f, String rg){
        final Matcher m = pattern.matcher(f.getName());
        if(m.find()){
            return rg.equals(m.group(readGroup));
        }
        return false;
    }

    public boolean matches(File f) {
        return pattern.matcher(f.getName()).matches();
    }
}
