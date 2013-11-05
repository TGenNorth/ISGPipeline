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
            final Interval refCoord = toInterval(coord.getCoord(refIndex));
            final Interval qryCoord = toInterval(coord.getCoord(queryIndex));
            if (refCoord.isNegativeStrand() && qryCoord.isNegativeStrand()) {
                throw new IllegalStateException("Ref and Qry coords are both reversed");
            }

            final Collection<Interval> overlaps = overlapDetector.getOverlaps(qryCoord);

            for (Interval overlap : overlaps) {
                intervalList.add(translate(overlap, qryCoord, refCoord));
            }
        }

        intervalList.unique();
        intervalList.sort();
        intervalList.write(OUTPUT);

        return 0;
    }
    
    public Interval toInterval(Coord coord){
        return new Interval(coord.getChr(), 
                coord.getStart(), coord.getEnd(), coord.isReversed(), ".");
    }
    
    /**
     * 
     * @param i1 interval to translate
     * @param i2 interval that overlaps i1
     * @param i3 target interval that matches i2
     * @return 
     */
    public Interval translate(Interval i1, Interval i2, Interval i3){
        return translate(calculateOverlap(i1, i2), i3);
    }

    public IntervalOverlapInfo calculateOverlap(Interval subject, Interval target) {
        Interval intersect = subject.intersect(target);
        int offset = intersect.getStart() - target.getStart();
        int length = intersect.length() - 1;
        return new IntervalOverlapInfo(offset, length, target.isNegativeStrand());
    }

    public Interval translate(IntervalOverlapInfo overlapInfo, Interval target) {
        if (overlapInfo.reversed || target.isNegativeStrand()) {
            return new Interval(target.getSequence(),
                    Math.max(target.getStart(), target.getEnd() - overlapInfo.offset - overlapInfo.length),
                    Math.max(target.getStart(), target.getEnd() - overlapInfo.offset),
                    true,
                    ".");
        } else {
            return new Interval(target.getSequence(),
                    Math.min(target.getEnd(), target.getStart() + overlapInfo.offset),
                    Math.min(target.getEnd(), target.getStart() + overlapInfo.offset + overlapInfo.length),
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

    public static final class IntervalOverlapInfo {

        public final int offset;
        public final int length;
        public final boolean reversed;

        public IntervalOverlapInfo(int offset, int length, boolean reversed) {
            this.offset = offset;
            this.length = length;
            this.reversed = reversed;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final IntervalOverlapInfo other = (IntervalOverlapInfo) obj;
            if (this.offset != other.offset) {
                return false;
            }
            if (this.length != other.length) {
                return false;
            }
            if (this.reversed != other.reversed) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            return hash;
        }

        @Override
        public String toString() {
            return "IntervalOverlapInfo{" + "offset=" + offset + ", length=" + length + ", reversed=" + reversed + '}';
        }
        
        
    }
}
