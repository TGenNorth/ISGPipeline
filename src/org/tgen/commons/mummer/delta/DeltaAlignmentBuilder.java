/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.mummer.delta;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class DeltaAlignmentBuilder {

    private int refStart, refEnd, qryStart, qryEnd;
    private String refName, qryName;
    private List<DeltaMatch> refMatches = new ArrayList<DeltaMatch>();
    private List<DeltaMatch> qryMatches = new ArrayList<DeltaMatch>();
    private int refOffset = 0;
    private int qryOffset = 0;
    private boolean done = false;

    public DeltaAlignmentBuilder(String refName, int refStart, int refEnd, String qryName, int qryStart, int qryEnd) {
        if(refStart > refEnd){
            throw new IllegalArgumentException("reference must be in forward direction.");
        }
        this.refName = refName;
        this.qryName = qryName;
        this.refStart = refStart;
        this.refEnd = refEnd;
        this.qryStart = qryStart;
        this.qryEnd = qryEnd;
    }

    public void appendOffset(int dist) {
        if(done){
            throw new IllegalStateException("cannot call appendOffset after zero.");
        }
        if (dist == 0) {
            done = true;
            dist = refEnd - (refStart + refOffset - 1) + 1;
        }
        int absDist = Math.abs(dist);
        if(absDist > 1){
            DeltaMatch rMatch = new DeltaMatch(refOffset, refOffset+absDist-1);
            DeltaMatch qMatch = new DeltaMatch(qryOffset, qryOffset+absDist-1);
            refMatches.add(rMatch);
            qryMatches.add(qMatch);
            qryOffset += absDist-1;
            refOffset += absDist-1;
        }
        if (dist > 0) {
            //distance to next insertion in reference
            refOffset++;
        } else {
            //distance to next deletion in reference
            qryOffset++;
        }
    }
    
    public DeltaAlignment build(){
        if(!done){
            throw new IllegalStateException("Cannot build incomplete alignment.");
        }
        return new DeltaAlignment(refName, refStart, refEnd, qryName, qryStart, qryEnd, refMatches, qryMatches);
    }
    
    public String toString(){
        String ref = "ABCDACBDCAC";
        String qry = "BCCDACDCAC";
        for(int i=0; i<refMatches.size(); i++){
            DeltaMatch rm = refMatches.get(i);
            DeltaMatch qm = qryMatches.get(i);
            System.out.println(ref.substring(rm.getStart(), rm.getEnd()));
            System.out.println(qry.substring(qm.getStart(), qm.getEnd()));
        }
        return refMatches.toString() + "\n" + qryMatches.toString();
    }
    
    public static void main(String[] args){
        DeltaAlignmentBuilder b = new DeltaAlignmentBuilder("", 1,11, "", 1,10);
        b.appendOffset(1);
        b.appendOffset(-3);
        b.appendOffset(4);
        b.appendOffset(0);
        System.out.println(b);
    }
}
