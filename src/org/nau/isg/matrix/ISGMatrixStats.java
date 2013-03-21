/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.matrix;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixStats {

    private Map<String, SampleStats> statsMap = new HashMap<String, SampleStats>();
    private List<String> sampleNames;

    public ISGMatrixStats(ISGMatrixHeader header) {
        this.sampleNames = header.getSampleNames();
        init();
    }

    private void init() {
        for (String str : sampleNames) {
            statsMap.put(str, new SampleStats());
        }
    }

    public void add(ISGMatrixRecord r) {
        char ref = r.getRef();
        for (int i = 0; i < sampleNames.size(); i++) {
            String sampleName = sampleNames.get(i);
            SampleStats sampleStats = statsMap.get(sampleName);
            Stats chromStats = sampleStats.getStats(r.getChrom());
            char state = r.getState(i);
            updateStats(chromStats, state, ref);
            updateStats(sampleStats.getStats(), state, ref);
        }
    }

    private void updateStats(final Stats stats, final char state, final char ref) {
        if (state == 'N') {
            stats.ambiguousCount++;
        } else if (state == '.') {
            stats.noCovCount++;
        } else if (state != ref) {
            stats.snpCount++;
        } else {
            stats.refCount++;
        }
    }

    public void writeToFile(File file) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(file);
        pw.print("Sample");
        pw.print("\t");
        pw.print("Chrom");
        pw.print("\t");

        pw.print("# Ambiguous");
        pw.print("\t");
        pw.print("# NoCov");
        pw.print("\t");
        pw.print("# Snp");
        pw.print("\t");
        pw.print("# Ref");
        pw.print("\t");

        pw.print("% Ambiguous");
        pw.print("\t");
        pw.print("% NoCov");
        pw.print("\t");
        pw.print("% Snp");
        pw.print("\t");
        pw.print("% Ref");
        pw.println();

        for (String sampleName : sampleNames) {
            SampleStats sampleStats = statsMap.get(sampleName);
            writeStats(pw, sampleStats.getStats(), sampleName, "All");
            for (String chrom : sampleStats.getChroms()) {
                Stats stats = sampleStats.getStats(chrom);
                writeStats(pw, stats, sampleName, chrom);
            }

        }
        pw.close();
    }

    private void writeStats(final PrintWriter pw, final Stats stats, final String sampleName, final String chrom) {
        pw.print(sampleName);
        pw.print("\t");
        pw.print(chrom);
        pw.print("\t");

        pw.print(stats.getAmbiguousCount());
        pw.print("\t");
        pw.print(stats.getNoCovCount());
        pw.print("\t");
        pw.print(stats.getSnpCount());
        pw.print("\t");
        pw.print(stats.getRefCount());
        pw.print("\t");

        pw.print(stats.getPercentAmbiguous());
        pw.print("\t");
        pw.print(stats.getPercentNotCovered());
        pw.print("\t");
        pw.print(stats.getPercentSnps());
        pw.print("\t");
        pw.print(stats.getPercentRef());
        pw.print("\t");
        pw.println();
    }

    private class SampleStats {

        private Stats stats = new Stats();
        private Map<String, Stats> chromStats = new HashMap<String, Stats>();
        private List<String> chroms = new ArrayList<String>();

        public Stats getStats() {
            return stats;
        }

        public Stats getStats(String chrom) {
            Stats ret = chromStats.get(chrom);
            if (ret == null) {
                ret = new Stats();
                chromStats.put(chrom, ret);
                chroms.add(chrom);
            }
            return ret;
        }

        public List<String> getChroms() {
            return Collections.unmodifiableList(chroms);
        }
    }

    private class Stats {

        private int ambiguousCount = 0;
        private int noCovCount = 0;
        private int snpCount = 0;
        private int refCount = 0;

        public int getAmbiguousCount() {
            return ambiguousCount;
        }

        public int getNoCovCount() {
            return noCovCount;
        }

        public int getSnpCount() {
            return snpCount;
        }

        public int getRefCount() {
            return refCount;
        }

        public int getPercentAmbiguous() {
            double percent = (((double) ambiguousCount / getTotal()) * 100);
            return (int) percent;
        }

        public int getPercentNotCovered() {
            double percent = (((double) noCovCount / getTotal()) * 100);
            return (int) percent;
        }

        public int getPercentSnps() {
            double percent = (((double) snpCount / getTotal()) * 100);
            return (int) percent;
        }

        public int getPercentRef() {
            double percent = (((double) refCount / getTotal()) * 100);
            return (int) percent;
        }

        public int getTotal() {
            return ambiguousCount + noCovCount + snpCount + refCount;
        }
    }
}
