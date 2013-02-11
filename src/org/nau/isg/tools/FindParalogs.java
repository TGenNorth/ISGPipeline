package org.nau.isg.tools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.util.Collection;
import java.util.List;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.picard.util.OverlapDetector;
import net.sf.samtools.SAMFileHeader;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.utils.CoordsUtils;

/**
 *
 * @author jbeckstrom
 */
public class FindParalogs extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Find duplicated regions";
    @Option(doc = "Genome vs. Genome coords file", optional = false)
    public File SELF_COORDS = new File("dist/mummer/MSHR1043_MSHR1043.coords");
    @Option(doc = "Genome vs. Reference coords file", optional = false)
    public File REF_COORDS = new File("dist/mummer/ref_MSHR1043.coords");
    @Option(doc = "Output interval file.")
    public File OUTPUT = new File("dist/dups/MSHR1043.interval_list");
    @Option(doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE = new File("dist/ref.fasta");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new FindParalogs().instanceMain(args));
    }

    @Override
    protected int doWork() {

        // read in self coords file and create overlap detector
        // process coords and query overlap detector using query coord
        // if overlaps are found, grab overlapping region from reference coord
        // add coord to interval list
        // make interval list unique

        IoUtil.assertFileIsReadable(SELF_COORDS);
        IoUtil.assertFileIsReadable(REF_COORDS);
        IoUtil.assertFileIsReadable(REFERENCE_SEQUENCE);
        IoUtil.assertFileIsWritable(OUTPUT);

        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
        assertDictionaryExists(ref);
        assertFastaIsIndexed(ref);

        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(ref.getSequenceDictionary());
        final IntervalList intervalList = new IntervalList(header);

        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        {
            List<CoordsRecord> coords = CoordsUtils.readCoords(SELF_COORDS, false);
            for (CoordsRecord coord : coords) {
                Coord c1 = coord.getCoord(0);
                Coord c2 = coord.getCoord(1);
                if (c1.getStart() == c2.getStart() && c1.getEnd() == c2.getEnd() && c1.getChr().equals(c2.getChr())) {
                    continue;
                } else {
                    Interval interval = new Interval(c1.getChr(), c1.getStart(), c1.getEnd());
                    overlapDetector.addLhs(interval, interval);
                }
            }
        }

        List<CoordsRecord> coords = CoordsUtils.readCoords(REF_COORDS, false);
        int refIndex = 0;
        int queryIndex = 1;

        //create overlap detector 
        for (CoordsRecord coord : coords) {
            Coord c = coord.getCoord(refIndex);
            if(c.isReversed()){
                throw new IllegalStateException("Unexpected reversal of reference coord.");
            }
            Interval intvl = new Interval(c.getChr(), c.getStart(), c.getEnd());
            Collection<Interval> overlaps = overlapDetector.getOverlaps(intvl);
            for (Interval overlap : overlaps) {
                Interval intersect = intvl.intersect(overlap);
                int offset = intersect.getStart() - intvl.getStart();
                int length = intersect.length();
                Coord c2 = coord.getCoord(queryIndex);
                if (c2.isReversed()) {
                    Interval i2 = new Interval(c2.getChr(), c2.getEnd() - offset - length, c2.getEnd() - offset, true, ".");
                    intervalList.add(i2);
                } else {
                    Interval i2 = new Interval(c2.getChr(), c2.getStart() + offset, c2.getStart() + offset + length, false, ".");
                    intervalList.add(i2);
                }

            }
        }



        intervalList.unique();
        intervalList.sort();
        intervalList.write(OUTPUT);


        return 0;
    }

    private OverlapDetector<Interval> createOverlapDetector(IntervalList iList) {
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        for (Interval invl : iList.getIntervals()) {
            overlapDetector.addLhs(invl, invl);
        }
        return overlapDetector;
    }

    private void assertDictionaryExists(ReferenceSequenceFile ref) {
        if (ref.getSequenceDictionary() == null) {
            throw new PicardException("Fasta file must have a dictionary");
        }
    }

    private void assertFastaIsIndexed(ReferenceSequenceFile ref) {
        if (!ref.isIndexed()) {
            throw new PicardException("Fasta file must be indexed");
        }
    }
}
