/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.coords;

import com.google.common.collect.AbstractIterator;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import mummer.coords.Coord;
import mummer.coords.CoordsFileReader;
import mummer.coords.CoordsRecord;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.util.CloserUtil;

/**
 * Iterates over the reference coordinates of a coords file. To facilitate 
 * post-processing, the coordinates are converted into Interval objects.
 * 
 * @author jbeckstrom
 */
public class CoordsCoverage extends AbstractIterator<Interval> implements Closeable {

    private final Iterator<CoordsRecord> iter;
    private final SAMSequenceDictionary seqDict;
    private int refCoordIndex = -1;

    public CoordsCoverage(File coords, File ref) {
        this.iter = new CoordsRecordIterator(coords);
        this.seqDict = ReferenceSequenceFileFactory
                .getReferenceSequenceFile(ref)
                .getSequenceDictionary();
    }
    
    public CoordsCoverage(Iterator<CoordsRecord> iter, SAMSequenceDictionary seqDict) {
        this.iter = iter;
        this.seqDict = seqDict;
    }

    @Override
    protected Interval computeNext() {
        return iter.hasNext() ? toInterval(iter.next()) : endOfData();
    }
    
    private Interval toInterval(CoordsRecord r){
        if(refCoordIndex==-1){
            refCoordIndex = determineReferenceCoordIndex(r, seqDict);
        }
        final Coord coord = r.getCoord(refCoordIndex);
        return new Interval(coord.getChr(), coord.getStart(), 
                    coord.getEnd(), false, ".");
    }
    
    public int determineReferenceCoordIndex(CoordsRecord coord, SAMSequenceDictionary seqDict){
        if(seqDict.getSequence(coord.getCoord(0).getChr()) != null){
            return 0;
        }else if(seqDict.getSequence(coord.getCoord(1).getChr()) != null){
            return 1;
        }else{
            throw new IllegalArgumentException("Could not find reference coord in sequence dictionary.");
        }  
    }

    @Override
    public void close() throws IOException {
        CloserUtil.close(iter);
    }
    
}
