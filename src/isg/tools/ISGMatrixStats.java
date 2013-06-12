/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixStats {

    private static final byte AMBIGUOUS_BASE = (byte) 'N';
    private Map<String, SampleStats> statsMap = new HashMap<String, SampleStats>();
    private Set<String> sampleNames;

    public ISGMatrixStats(Set<String> sampleNames) {
        this.sampleNames = new HashSet<String>(sampleNames);
    }

    public void add(VariantContext vc) {
        if (!vc.isSNP()) {
            return; //only SNPs are supported
        }
        for (final String sampleName : sampleNames) {
            final Genotype g = vc.getGenotype(sampleName);
            if (g == null) {
                throw new IllegalArgumentException(String.format("Could not find "
                        + "genotype for sample '%s' in VariantContext '%s'", sampleName, vc.toString()));
            }
            final SampleStats sampleStats = getStatsForSample(sampleName);
            sampleStats.updateStats(vc.getChr(), g.getAlleles());
        }
    }

    private SampleStats getStatsForSample(final String sample) {
        SampleStats ret = statsMap.get(sample);
        if (ret == null) {
            ret = new SampleStats();
            statsMap.put(sample, ret);
        }
        return ret;
    }

    public static boolean isNonRef(final List<Allele> alleles) {
        for (Allele a : alleles) {
            if (a.isNonReference()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAmbiguous(final List<Allele> alleles) {
        for (Allele a : alleles) {
            for (byte b : a.getBases()) {
                if (b == AMBIGUOUS_BASE) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNoCall(final List<Allele> alleles) {
        for (Allele a : alleles) {
            if (a.isNoCall()) {
                return true;
            }
        }
        return false;
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

        public void updateStats(final String chr, final List<Allele> alleles) {
            final Stats chrStats = getStats(chr);
            if (isAmbiguous(alleles)) {
                stats.ambiguousCount++;
                chrStats.ambiguousCount++;
            } else if (isNoCall(alleles)) {
                stats.noCovCount++;
                chrStats.noCovCount++;
            } else if (isNonRef(alleles)) {
                stats.snpCount++;
                chrStats.snpCount++;
            } else {
                stats.refCount++;
                chrStats.refCount++;
            }
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
