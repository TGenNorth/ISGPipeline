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
public class FindCommonCoords1 implements Runnable {

    private File coordsFile1, coordsFile2, fasta, intervalFile;
    private boolean skipFirst;
    
    public FindCommonCoords1(File coords1, File coords2, File fasta, File intervalFile){
        this(coords1, coords2, fasta, intervalFile, false);
    }
    
    public FindCommonCoords1(File coords1, File coords2, File fasta, File intervalFile, boolean skipFirst){
        this.coordsFile1 = coords1;
        this.coordsFile2 = coords2;
        this.fasta = fasta;
        this.intervalFile = intervalFile;
        this.skipFirst = skipFirst;
    }
    
    @Override
    public void run() {
        if(intervalFile.exists() && intervalFile.length()>0){
            System.out.println(intervalFile.getAbsolutePath()+" already exists");
            return;
        }
        ReferenceSequenceFile refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(fasta);
        IntervalList intervalList = createIntervalList(refSeq.getSequenceDictionary());
        
        List<CoordsRecord> coords1 = CoordsUtils.readCoords(coordsFile1, skipFirst);
        List<CoordsRecord> coords2 = CoordsUtils.readCoords(coordsFile2, skipFirst);

        addIntervals(coords1, coords2, intervalList);
        intervalList.write(intervalFile);
    }

    private void addIntervals(List<CoordsRecord> coords1, List<CoordsRecord> coords2, IntervalList intervalList) {

        //create overlap detector 
        final OverlapDetector<Interval> overlapDetector = createOverlapDetector(coords1);

        for (CoordsRecord coord : coords2) {
            Coord c1 = coord.getCoord(0);
            Interval i1 = new Interval(c1.getChr(), c1.getStart(), c1.getEnd());
            Collection<Interval> overlapingCoords = overlapDetector.getOverlaps(i1);
            for (Interval i2 : overlapingCoords) {
                intervalList.add(intersect(i1, i2));
            }
        }


        intervalList.unique(false);
    }

    private Interval intersect(Interval i1, Interval i2) {
        if (!i1.intersects(i2)) {
            throw new IllegalArgumentException(i1 + " does not intersect " + i2);
        }
        return new Interval(i1.getSequence(),
                Math.max(i1.getStart(), i2.getStart()),
                Math.min(i1.getEnd(), i2.getEnd()),
                i1.isNegativeStrand(),
                ".");
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
