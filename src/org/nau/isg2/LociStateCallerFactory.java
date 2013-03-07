/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2;

import java.io.File;
import java.io.IOException;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.picard.util.OverlapDetector;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.bed.BEDCodec;
import org.broad.tribble.bed.BEDFeature;
import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;

/**
 *
 * @author jbeckstrom
 */
public class LociStateCallerFactory {

    private LociStateCallerFactory() {
    }

    public static LociStateCaller createFromFile(File f) throws IOException{
        final String filename = f.getName();
        if(filename.endsWith(".interval_list")){
            return createFromIntervalList(f);
        }else if(filename.endsWith(".bed")){
            return createFromBED(f);
        }else{
            throw new IllegalArgumentException("Unsupported file type: "+f.getPath());
        }
    }
    
    public static LociStateCaller createFromBED(File f) throws IOException {
        final OverlapDetector<CalledState> overlapDetector = new OverlapDetector<CalledState>(0, 0);
        final AbstractFeatureReader<BEDFeature> reader = AbstractFeatureReader.getFeatureReader(f.getAbsolutePath(), new BEDCodec(), false);
        final CloseableTribbleIterator<BEDFeature> iter = reader.iterator();
        while(iter.hasNext()){
            final BEDFeature bed = iter.next();
            final Interval interval = new Interval(bed.getChr(), bed.getStart(), bed.getEnd());
            final CalledState calledState = CalledState.valueOf(bed.getName());
            overlapDetector.addLhs(calledState, interval);
        }
        return new LociStateCallerImpl(overlapDetector);
    }
    
    public static LociStateCaller createFromIntervalList(File f) throws IOException {
        final IntervalList intervalList = IntervalList.fromFile(f);
        final OverlapDetector<CalledState> overlapDetector = new OverlapDetector<CalledState>(0, 0);
        for(final Interval interval: intervalList.getIntervals()){
            overlapDetector.addLhs(CalledState.CALLABLE, interval);
        }
        return new LociStateCallerImpl(overlapDetector, CalledState.NO_COVERAGE);
    }
}
