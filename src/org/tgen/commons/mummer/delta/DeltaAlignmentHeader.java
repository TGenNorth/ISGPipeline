/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.mummer.delta;

/**
 *
 * @author jbeckstrom
 */
public class DeltaAlignmentHeader {

    private int refStart;
    private int refEnd;
    private int queryStart;
    private int queryEnd;

    public DeltaAlignmentHeader(int refStart, int refEnd, int queryStart, int queryEnd) {
        this.refStart = refStart;
        this.refEnd = refEnd;
        this.queryEnd = queryEnd;
        this.queryStart = queryStart;
    }

    public int getQueryEnd() {
        return queryEnd;
    }

    public int getQueryStart() {
        return queryStart;
    }

    public int getRefEnd() {
        return refEnd;
    }

    public int getRefStart() {
        return refStart;
    }

}
