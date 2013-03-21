/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.mummer.delta;

/**
 *
 * @author jbeckstrom
 */
public final class DeltaMatch {
    private final int start;
    private final int end;

    public DeltaMatch(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }
    
    public int length(){
        return end - start;
    }

    @Override
    public String toString() {
        return "DeltaMatch{" + "start=" + start + ", end=" + end + '}';
    }
    
}
