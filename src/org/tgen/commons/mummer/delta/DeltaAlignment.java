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
public class DeltaAlignment {

    private final String refName;
    private final int refStart;
    private final int refEnd;
    private final String qryName;
    private final int qryStart;
    private final int qryEnd;
    private final List<DeltaMatch> refMatches = new ArrayList<DeltaMatch>();
    private final List<DeltaMatch> qryMatches = new ArrayList<DeltaMatch>();

    protected DeltaAlignment(String refName,
            int refStart, 
            int refEnd, 
            String qryName,
            int qryStart, 
            int qryEnd, 
            List<DeltaMatch> refMatches,
            List<DeltaMatch> qryMatches) {
        
        this.refName = refName;
        this.refStart = refStart;
        this.qryStart = qryStart;
        this.qryName = qryName;
        this.refEnd = refEnd;
        this.qryEnd = qryEnd;
        this.refMatches.addAll(refMatches);
        this.qryMatches.addAll(qryMatches);
    }

    public int getQryEnd() {
        return qryEnd;
    }

    public List<DeltaMatch> getQryMatches() {
        return qryMatches;
    }

    public int getQryStart() {
        return qryStart;
    }

    public int getRefEnd() {
        return refEnd;
    }

    public List<DeltaMatch> getRefMatches() {
        return refMatches;
    }

    public int getRefStart() {
        return refStart;
    }

    public String getQryName() {
        return qryName;
    }

    public String getRefName() {
        return refName;
    }
    
    public boolean isReverse(){
        return qryStart>qryEnd;
    }
    
    public List<DeltaMatch> translate(int start, int end){
        if(start < refStart || end > refEnd){
            throw new IllegalArgumentException("cannot translate interval outside reference interval");
        }
        List<DeltaMatch> ret = new ArrayList<DeltaMatch>();
        int refOffsetStart = start - refStart;
        int refOffsetEnd = end - refStart;
        for(int i=0; i<refMatches.size(); i++){
            DeltaMatch rm = refMatches.get(i);
            DeltaMatch qm = qryMatches.get(i);
            if(refOffsetStart > rm.getEnd() || refOffsetEnd < rm.getStart()){
                continue;
            }
            int mOffStart = Math.max(refOffsetStart - rm.getStart(), 0);
            int mOffEnd = Math.min(refOffsetEnd - rm.getStart(), rm.length());
            int qOffStart = qm.getStart()+mOffStart;
            int qOffEnd = qm.getStart()+mOffEnd;
            ret.add(new DeltaMatch(qryOffsetToPos(qOffStart), qryOffsetToPos(qOffEnd)));
        }
        return ret;
    }

    public int translate(int posInRef){
        if(posInRef < refStart || posInRef > refEnd){
            throw new IllegalArgumentException("cannot translate position outside reference interval");
        }
        int refOffset = posInRef - refStart;
        int index = indexOf(refOffset);
        if(index==-1){
            //cannot translate; position falls within indel
            return -1;
        }
        DeltaMatch refMatch = refMatches.get(index);
        DeltaMatch qryMatch = qryMatches.get(index);
        int qryOffset = qryMatch.getStart() + (refOffset - refMatch.getStart());
        return qryOffsetToPos(qryOffset);
    }
    
    private int qryOffsetToPos(int qryOffset){
        if(qryStart>qryEnd){
            //reverse
            return qryStart-qryOffset;
        }else{
            //forward
            return qryStart+qryOffset;
        }
    }
    
    private int indexOf(int offsetInRef){
        for(int i=0; i<refMatches.size(); i++){
            DeltaMatch m = refMatches.get(i);
            if(offsetInRef>=m.getStart() && offsetInRef<m.getEnd()){
                return i;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        return "DeltaAlignment{" + "refStart=" + refStart + ", refEnd=" + refEnd + ", qryStart=" + qryStart + ", qryEnd=" + qryEnd + '}';
    }
    
    
}
