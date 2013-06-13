/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.io.File;
import java.io.IOException;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.picard.util.OverlapDetector;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.bed.BEDCodec;
import org.broad.tribble.bed.BEDFeature;

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
        int error = -1;
        while(iter.hasNext()){
            final BEDFeature bed = iter.next();
            if(error==-1){
                //fix for 1-based bed files produced by old versions of GATK
                error = bed.getStart()==2 ? 1 : 0;
            }
            final Interval interval = new Interval(bed.getChr(), bed.getStart()-error, bed.getEnd());
            final CalledState calledState = CalledState.valueOf(bed.getName());
            overlapDetector.addLhs(calledState, interval);
        }
        return new LociStateCallerImpl(overlapDetector, CalledState.NO_COVERAGE);
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
