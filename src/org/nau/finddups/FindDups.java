/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.finddups;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.utils.CoordsUtils;

/**
 *
 * @author jbeckstrom
 */
public class FindDups extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Find duplicated regions";
    @Option(doc = "Reference coords", optional = false)
    public File COORDS;// = new File("Bp4160ref.coords");
    @Option(doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE;// = new File("MSHR1655_chromosomes.fasta");
    @Option(doc = "Output interval file.")
    public File OUTPUT;// = new File("out.interval_list");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new FindDups().instanceMain(args));
    }

    @Override
    protected int doWork() {

        // read in coords of genome compared to reference
        // create interval list using genome coords
        // loop through coords looking for overlaps in interval list
        // find common denominator which will be the duplicated region

        IoUtil.assertFileIsReadable(COORDS);
        IoUtil.assertFileIsReadable(REFERENCE_SEQUENCE);
        IoUtil.assertFileIsWritable(OUTPUT);

        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);
        assertDictionaryExists(ref);
        assertFastaIsIndexed(ref);

        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(ref.getSequenceDictionary());
        final IntervalList intervalList = new IntervalList(header);

        List<CoordsRecord> coords = CoordsUtils.readCoords(COORDS, false);

        //create overlap detector 
        OverlapDetector<Coord> overlapDetector = new OverlapDetector<Coord>(0, 0);
        for (CoordsRecord coord : coords) {
            Coord c = coord.getCoord(1);
            c = c.fixLength();
            Interval i = new Interval(c.getChr(), c.getStart(), c.getEnd());
            overlapDetector.addLhs(c, i);
        }

        for (CoordsRecord coord : coords) {
            Coord c1 = coord.getCoord(1);
            c1 = c1.fixLength();
            Interval i = new Interval(c1.getChr(), c1.getStart(), c1.getEnd());
            Collection<Coord> overlapingCoords = overlapDetector.getOverlaps(i);
            for (Coord c2 : overlapingCoords) {
                if (c2.equals(c1)) {
                    continue;
                }
                
//                System.out.println(c1.getStart()+" "+c1.getEnd());
//                System.out.println(c2.getStart()+" "+c2.getEnd());
                
                Coord ic = c1.intersects(c2);
                Coord oc = ic.getCoordsRecord().getOther(ic);
                //add interval to list
//                System.out.println(oc.getStart()+" "+oc.getEnd());
//                System.out.println();
                Interval iToAdd = new Interval(oc.getChr(), oc.getStart(), oc.getEnd(), false, c1.getStart()+"_"+c1.getEnd()+":"+c2.getStart()+"_"+c2.getEnd());
                intervalList.add(iToAdd);
            }
        }


        intervalList.unique();
        intervalList.write(OUTPUT);


        return 0;
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
