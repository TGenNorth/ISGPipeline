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
import net.sf.picard.util.Interval;
import net.sf.samtools.util.CloserUtil;

/**
 * Iterates over a self-coords comparison ignoring coordinates that are identical 
 * in both the query and reference. In doing so, only the duplicated regions will
 * be returned by the iterator.
 * 
 * @author jbeckstrom
 */
public class CoordsDups extends AbstractIterator<Interval> implements Closeable {

    private final Iterator<CoordsRecord> iter;

    public CoordsDups(File coords) {
        this.iter = new CoordsRecordIterator(coords);
    }

    public CoordsDups(Iterator<CoordsRecord> iter) {
        this.iter = iter;
    }

    @Override
    protected Interval computeNext() {
        while (iter.hasNext()) {
            CoordsRecord next = iter.next();
            Coord c1 = next.getCoord(0);
            Coord c2 = next.getCoord(1);
            if (c1.getStart() != c2.getStart()
                    || c1.getEnd() != c2.getEnd()
                    || !c1.getChr().equals(c2.getChr())) {
                return new Interval(c1.getChr(), c1.getStart(), c1.getEnd(), false, ".");
            }
        }
        return endOfData();
    }

    @Override
    public void close() throws IOException {
        CloserUtil.close(iter);
    }

}
