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
 *
 * @author jbeckstrom
 */
public class FilePairDetectionAlgorithm implements DetectionAlgorithm<File, File> {

    private final Pattern pattern;
    private final String first; 
    private final String second;
    private final int group;

    public FilePairDetectionAlgorithm(final String regex) {
        this(regex, "1", "2", 1);
    }

    public FilePairDetectionAlgorithm(final String regex, final String first, final String second) {
        this(regex, first, second, 1);
    }

    public FilePairDetectionAlgorithm(final String regex, final String first, final String second, final int group) {
        this.pattern = Pattern.compile(regex);
        this.first = first;
        this.second = second;
        this.group = group;
    }

    @Override
    public File apply(File f) {
        if (!detectable(f)) {
            throw new IllegalArgumentException("Cannot detect a pair for file: " + f.getPath() + " using pattern: "+pattern.pattern());
        }
        final Matcher m = pattern.matcher(f.getName());
        m.find();

        String before = m.group(group);
        String after = first.equals(before) ? second : first;
        
        StringBuilder sb = new StringBuilder(f.getName());
        sb.replace(m.start(group), m.start(group)+before.length(), after);
        
        return new File(f.getParentFile(), sb.toString());
    }

    @Override
    public boolean detectable(File f) {
        return pattern.matcher(f.getName()).matches();
    }
}
