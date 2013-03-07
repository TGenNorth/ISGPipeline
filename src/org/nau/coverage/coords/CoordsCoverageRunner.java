/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.coverage.coords;

import java.io.File;
import java.util.List;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.samtools.SAMFileHeader;
import org.tgen.commons.coords.CoordsRecord;
import org.tgen.commons.feature.Coord;
import org.tgen.commons.utils.CoordsUtils;

/**
 *
 * @author jbeckstrom
 */
public class CoordsCoverageRunner implements Runnable {

    private final File coordsFile;
    private final File refFile;
    private final File outFile;

    public CoordsCoverageRunner(File coords, File ref, File out) {
        this.coordsFile = coords;
        this.refFile = ref;
        this.outFile = out;
    }

    @Override
    public void run() {
        if (outFile.exists() && outFile.length() > 0) {
            return;
        }
        
        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(refFile);

        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(ref.getSequenceDictionary());
        final IntervalList intervalList = new IntervalList(header);

        List<CoordsRecord> coords = CoordsUtils.readCoords(coordsFile, false);

        for (CoordsRecord coord : coords) {
            Coord c1 = coord.getCoord(0);
            Interval intvl = new Interval(c1.getChr(), c1.getStart(), c1.getEnd(), false, ".");
            intervalList.add(intvl);
        }

        intervalList.unique();
        intervalList.write(outFile);
    }

}
