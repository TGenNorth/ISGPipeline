package isg.tools;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import mummer.coords.Coord;
import mummer.coords.CoordsDups;
import mummer.coords.CoordsRecord;
import mummer.coords.CoordsRecordIterator;
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

/**
 *
 * @author jbeckstrom
 */
public class FindParalogs extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Find duplicated regions";
    @Option(doc = "Genome vs. Genome coords file", optional = false)
    public File SELF_COORDS;// = new File("dist/mummer/MSHR1043_MSHR1043.coords");
    @Option(doc = "Genome vs. Reference coords file", optional = false)
    public File REF_COORDS;// = new File("dist/mummer/ref_MSHR1043.coords");
    @Option(doc = "Output interval file.")
    public File OUTPUT;// = new File("dist/dups/MSHR1043.interval_list");
    @Option(doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE;// = new File("dist/ref.fasta");

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
            Iterator<Interval> coordsDupsIter = new CoordsDups(SELF_COORDS);
            while (coordsDupsIter.hasNext()) {
                Interval interval = coordsDupsIter.next();
                overlapDetector.addLhs(interval, interval);
            }
        }

        Iterator<CoordsRecord> coordsIter = new CoordsRecordIterator(REF_COORDS);
        int refIndex = -1;
        int queryIndex = -1;

        //create overlap detector 
        while (coordsIter.hasNext()) {
            CoordsRecord coord = coordsIter.next();
            if (refIndex == -1) {
                refIndex = determineReferenceCoordIndex(coord, ref.getSequenceDictionary());
                queryIndex = refIndex == 1 ? 0 : 1;
            }
            final Coord refCoord = coord.getCoord(refIndex);
            final Coord qryCoord = coord.getCoord(queryIndex);
            if (refCoord.isReversed() && qryCoord.isReversed()) {
                throw new IllegalStateException("Ref and Qry coords are both reversed");
            }

            final Interval intvl = new Interval(qryCoord.getChr(), qryCoord.getStart(), qryCoord.getEnd());
            final Collection<Interval> overlaps = overlapDetector.getOverlaps(intvl);

            for (Interval overlap : overlaps) {
                Interval intersect = intvl.intersect(overlap);
                int offset = intersect.getStart() - intvl.getStart();
                int length = intersect.length();
                intervalList.add(translate(offset, length, qryCoord, refCoord));
            }
        }

        intervalList.unique();
        intervalList.sort();
        intervalList.write(OUTPUT);

        return 0;
    }

    public Interval translate(int offset, int length, Coord subject, Coord target) {
        if (subject.isReversed() || target.isReversed()) {
            return new Interval(target.getChr(),
                    target.getEnd() - offset - length,
                    target.getEnd() - offset,
                    true,
                    ".");
        } else {
            return new Interval(target.getChr(),
                    target.getStart() + offset,
                    target.getStart() + offset + length,
                    false,
                    ".");
        }
    }

    public int determineReferenceCoordIndex(CoordsRecord coord, SAMSequenceDictionary seqDict) {
        if (seqDict.getSequence(coord.getCoord(0).getChr()) != null) {
            return 0;
        } else if (seqDict.getSequence(coord.getCoord(1).getChr()) != null) {
            return 1;
        } else {
            throw new IllegalArgumentException("Could not find reference coord in sequence dictionary.");
        }
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
