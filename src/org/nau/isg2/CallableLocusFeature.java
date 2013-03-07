/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2;

import org.broad.tribble.Feature;
import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;

/**
 *
 * @author jbeckstrom
 */
public class CallableLocusFeature implements Feature {

    private final String chr;
    private final int start;
    private final int end;
    private final CalledState calledState;

    public CallableLocusFeature(String chr, int start, int end, CalledState calledState) {
        this.chr = chr;
        this.start = start;
        this.end = end;
        this.calledState = calledState;
    }

    public CalledState getCalledState() {
        return calledState;
    }

    @Override
    public String getChr() {
        return chr;
    }

    @Override
    public int getEnd() {
        return end;
    }

    @Override
    public int getStart() {
        return start;
    }
    
}
