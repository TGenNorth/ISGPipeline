/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.bwamatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class SequenceFilePairMatcher {

    private List<Pattern> patterns = new ArrayList<Pattern>();

    public SequenceFilePairMatcher() {
        patterns.add(Pattern.compile("(.*)_[0-9]+_([0-9])_sequence\\.txt"));
        patterns.add(Pattern.compile("(.*)_.*_L[0-9]+_R([0-9])_[0-9]+\\.fastq\\.gz"));
        patterns.add(Pattern.compile("(.*)_R([0-9])_[0-9]+\\.fastq\\.gz"));
        patterns.add(Pattern.compile("(.*)_R([0-9])\\.fastq\\.gz"));
        patterns.add(Pattern.compile("(.*)_([12])\\.fastq"));
    }

    public Collection<SequenceFilePair> process(File seqDir) {

        Map<String, SequenceFilePair> sequenceFilePairs = new HashMap<String, SequenceFilePair>();
        Collection<File> files = FileUtils.listFiles(seqDir, new String[]{"gz", "fastq", "txt"}, false);
        List<File> visited = new ArrayList<File>();
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
            while (sequenceFilePairs.containsKey(key)) {
                key = sfp.getName() + "-" + count;
                count++;
            }

            sfp.setName(key);
            sequenceFilePairs.put(key, sfp);
        }

        return sequenceFilePairs.values();
    }

    private Matcher getMatcher(File f) {

        final String str = f.getName();

        for (Pattern pattern : patterns) { //try matching all patterns

            Matcher matcher = pattern.matcher(str);

            if (matcher.find() && matcher.groupCount() == 2) {
                return matcher;
            }
        }

        return null;
    }

    private SequenceFilePair getPair(File first) {

        String str = first.getName();

        for (Pattern pattern : patterns) { //try matching all patterns

            Matcher matcher = pattern.matcher(str);

            if (matcher.find() && matcher.groupCount() == 2) {

                String sampleName = matcher.group(1);
                //switch id
                char id = matcher.group(2).charAt(0);
                char otherId = '1';
                if (id == '1') {
                    otherId = '2';
                }

                char[] arr = str.toCharArray();
                arr[matcher.start(2)] = otherId;
                File second = new File(first.getParentFile(), new String(arr));

                SequenceFilePair sfp = new SequenceFilePair(sampleName);
                sfp.setSeq1(first);
                if (second.exists()) {
                    sfp.setSeq2(second);
                } else {
                    System.out.println("Could not find file: " + second.getAbsolutePath() + " assuming single ended");
                }
                return sfp;

            }
        }

        String sampleName = first.getName().substring(0, first.getName().indexOf('.'));
        SequenceFilePair sfp = new SequenceFilePair(sampleName);
        sfp.setSeq1(first);
        return sfp;
    }
}
