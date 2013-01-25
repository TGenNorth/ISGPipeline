/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.SAMSequenceRecord;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.utils.CoordsUtils;

/**
 *
 * @author jbeckstrom
 */
public class FindCommon extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Find common regions of a pairwise coords comparison";
    @Option(doc = "coords file of seq1 vs seq2", optional = false)
    public File COORDS1;// = new File("BpK96243.coords");
    @Option(doc = "coords file of seq2 vs seq1", optional = false)
    public File COORDS2;// = new File("MSHR1655.coords");
    @Option(doc = "fasta sequence file of seq1.")
    public File FASTA1;// = new File("MSHR1655_chromosomes.fasta");
    @Option(doc = "fasta sequence file of seq2.")
    public File FASTA2;// = new File("BpK96243.fasta");
    @Option(doc = "prefix of output files.")
    public String OUTPUT_PREFIX;// = "out";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new FindCommon().instanceMain(args));
    }

    @Override
    protected int doWork() {

        //read both coords files
        //create overlap detector with one coords file
        //search overlap detector with other coords file
        //compile interval list with intersecting regions
        //uniqueify the interval list

        IoUtil.assertFileIsReadable(COORDS1);
        IoUtil.assertFileIsReadable(COORDS2);
        IoUtil.assertFileIsReadable(FASTA2);
        IoUtil.assertFileIsReadable(FASTA1);

        final ReferenceSequences refSeqs = new ReferenceSequences();
        refSeqs.add(createRefSeq(FASTA1));
        refSeqs.add(createRefSeq(FASTA2));

        List<CoordsRecord> coords1 = CoordsUtils.readCoords(COORDS1, false);
        List<CoordsRecord> coords2 = CoordsUtils.readCoords(COORDS2, false);

        IntervalList intervalList1 = createIntervals(coords1, coords2, refSeqs);
        intervalList1.write(new File(OUTPUT_PREFIX + "_1.interval_list"));

        IntervalList intervalList2 = createIntervals(coords2, coords1, refSeqs);
        intervalList2.write(new File(OUTPUT_PREFIX + "_2.interval_list"));

        return 0;
    }

    private IntervalList createIntervals(List<CoordsRecord> coords1, List<CoordsRecord> coords2, ReferenceSequences refSeqs) {
        IntervalList intervalList = null;

        //create overlap detector 
        final OverlapDetector<Interval> overlapDetector = createOverlapDetector(coords1);

        for (CoordsRecord coord : coords2) {
            Coord c1 = coord.getCoord(0);
            if (intervalList == null) {
                intervalList = createIntervalList(refSeqs.find(c1.getChr()).getSequenceDictionary());
            }
            Interval i1 = new Interval(c1.getChr(), c1.getStart(), c1.getEnd());
            Collection<Interval> overlapingCoords = overlapDetector.getOverlaps(i1);
            for (Interval i2 : overlapingCoords) {
                intervalList.add(intersect(i1, i2));
            }
        }


        intervalList.unique(false);
        return intervalList;
    }

    private Interval intersect(Interval i1, Interval i2) {
        if (!i1.intersects(i2)) {
            throw new IllegalArgumentException(i1 + " does not intersect " + i2);
        }
        return new Interval(i1.getSequence(),
                Math.max(i1.getStart(), i2.getStart()),
                Math.min(i1.getEnd(), i2.getEnd()),
                i1.isNegativeStrand(),
                "");
    }

    private IntervalList createIntervalList(final SAMSequenceDictionary dict) {
        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(dict);
        return new IntervalList(header);
    }

    private OverlapDetector<Interval> createOverlapDetector(List<CoordsRecord> coords) {
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0, 0);
        for (CoordsRecord coord : coords) {
            Coord c = coord.getCoord(1);
            Interval i = new Interval(c.getChr(), c.getStart(), c.getEnd());
            overlapDetector.addLhs(i, i);
        }
        return overlapDetector;
    }

    private ReferenceSequenceFile createRefSeq(File fasta) {
        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(fasta);
        assertDictionaryExists(ref);
        assertFastaIsIndexed(ref);
        return ref;
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

    private class ReferenceSequences {

        private List<ReferenceSequenceFile> refSeqs = new ArrayList<ReferenceSequenceFile>();

        public void add(final ReferenceSequenceFile ref) {
            refSeqs.add(ref);
        }

        public ReferenceSequenceFile find(String seq) {
            for (final ReferenceSequenceFile ref : refSeqs) {
                final SAMSequenceRecord rec = ref.getSequenceDictionary().getSequence(seq);
                if (rec != null) {
                    return ref;
                }
            }
            return null;
        }
    }
}
