package org.nau.coverage.bam;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;
import net.sf.samtools.SAMSequenceDictionary;

public class FindCoverage extends CommandLineProgram {

    // Usage and parameters
    @Usage(programVersion = "0.3")
    public String USAGE = "Finds covered regions of a BAM file.";
    @Option(doc = "Input BAM file.", optional = false)
    public File BAM_FILE;// = new File("/Users/jbeckstrom/NetBeansProjects/GATK/resources/Yp-2194_rmdups_rg.bam");
    @Option(doc = "Output interval list.", optional = false)
    public File OUT_FILE;// = new File("out.interval_list");
    @Option(doc = "Minimum coverage", optional = false)
    public int MIN_COVERAGE = 3;
    private Map<Integer, Map<Integer, Integer>> coverageCount = new HashMap<Integer, Map<Integer, Integer>>();

    @Override
    protected int doWork() {
        SAMFileReader reader = new SAMFileReader(BAM_FILE);
        reader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
        SAMRecordIterator iter = reader.iterator();
        IntervalList intervalList = new IntervalList(reader.getFileHeader());

        int curPos = 0;
        int curSeqIndex = -1;
        CoverageInterval covInterval = new CoverageInterval(0, curPos, curPos);

        while (iter.hasNext()) {
            SAMRecord r = iter.next();
            int start = r.getAlignmentStart();
            int end = r.getAlignmentEnd();
            int seqIndex = r.getReferenceIndex();

            //update coverage counts
            processRecord(r);

            int targetPos = start;
            if (curSeqIndex != seqIndex || !iter.hasNext()) {
                targetPos = end;
            }

            Map<Integer, Integer> map = coverageCount.get(curSeqIndex);
            if (map != null) {
                while (curPos < targetPos) {
                    Integer count = map.remove(curPos);
                    if (count != null && count >= MIN_COVERAGE) {
                        if (covInterval == null) {
                            covInterval = new CoverageInterval(seqIndex, curPos, curPos);
                        } else if (covInterval.abutsEnd(seqIndex, curPos)) {
                            covInterval.setEnd(curPos);
                        } else {
                            convertAndAddIntervalToList(covInterval, intervalList);
                            covInterval = new CoverageInterval(seqIndex, curPos, curPos);
                        }
                    }
                    curPos++;
                }
            }
            curPos = start;
            curSeqIndex = seqIndex;
        }

        //add the last interval
        convertAndAddIntervalToList(covInterval, intervalList);

        intervalList.write(OUT_FILE);

        return 0;
    }

    private void convertAndAddIntervalToList(CoverageInterval covInterval, IntervalList iList) {
        Interval intervalToAdd = toInterval(covInterval, iList.getHeader().getSequenceDictionary());
        iList.add(intervalToAdd);
    }

    private Interval toInterval(CoverageInterval covInterval, SAMSequenceDictionary dict) {
        String seqName = dict.getSequence(covInterval.getSeqIndex()).getSequenceName();
        return new Interval(seqName, covInterval.getStart(), covInterval.getEnd(), false, ".");
    }

    private void processRecord(SAMRecord r) {
        //update coverage counts
        int pos = r.getAlignmentStart();
        for (CigarElement ce : r.getCigar().getCigarElements()) {
//            System.out.println(ce.getOperator());
            if (ce.getOperator() == CigarOperator.M) {
                //only increment coverage where there is a matching base
                incrementCoverage(r.getReferenceIndex(), pos, ce.getLength());
            }
            pos += ce.getLength();
        }
    }

    private void incrementCoverage(int seqIndex, int pos, int length) {
        Map<Integer, Integer> map = coverageCount.get(seqIndex);
        if (map == null) {
            //initialize map of positions to counts
            map = new HashMap<Integer, Integer>();
            coverageCount.put(seqIndex, map);
        }
        incrementCoverage(map, pos, length);
    }

    private void incrementCoverage(Map<Integer, Integer> map, int pos, int length) {
        for (int i = 0; i < length; i++) {
            Integer count = map.get(pos);
            if (count == null) {
                //initialize count
                count = new Integer(0);
                map.put(pos, count);
            }
            map.put(pos, ++count);
            pos++;
        }
    }

    private class CoverageInterval {

        private final int seqIndex;
        private final int start;
        private int end;

        public CoverageInterval(int seqIndex, int start, int end) {
            this.seqIndex = seqIndex;
            this.start = start;
            this.end = end;
        }

        public void setEnd(int end) {
            if (end < start) {
                throw new IllegalArgumentException("end must be greater than start.");
            }
            this.end = end;
        }

        public int getEnd() {
            return end;
        }

        public int getSeqIndex() {
            return seqIndex;
        }

        public int getStart() {
            return start;
        }

        public boolean abutsEnd(int seqIndex, int pos) {
            return (this.seqIndex == seqIndex) && (pos == this.end + 1);
        }
    }

    public static void main(String args[]) {
        System.exit(new FindCoverage().instanceMain(args));
    }
}
