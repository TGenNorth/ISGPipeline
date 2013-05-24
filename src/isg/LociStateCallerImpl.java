/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.util.Collection;
import net.sf.picard.util.Interval;
import net.sf.picard.util.OverlapDetector;
import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;

/**
 *
 * @author jbeckstrom
 */
public class LociStateCallerImpl implements LociStateCaller{

    private final OverlapDetector<CalledState> overlapDetector;
    private final CalledState assumedState;

    public LociStateCallerImpl(OverlapDetector<CalledState> overlapDetector) {
        this(overlapDetector, null);
    }
    
    public LociStateCallerImpl(OverlapDetector<CalledState> overlapDetector, CalledState assumedState) {
        this.overlapDetector = overlapDetector;
        this.assumedState = assumedState;
    }

    @Override
    public CalledState call(String chr, int pos) {
        Interval i = new Interval(chr, pos, pos);
        return call(i);
    }
    
    private CalledState call(final Interval i){
        Collection<CalledState> overlaps = overlapDetector.getOverlaps(i);
        if(overlaps.isEmpty()){
            System.out.println("Couldn't find overlap for: "+i);
            return assumedState;
        }else if(overlaps.size()==1){
            return overlaps.iterator().next();
        }else{
            throw new IllegalStateException("found more than one overlap");
        }
    }
    
}
