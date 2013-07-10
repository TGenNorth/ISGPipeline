/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

/**
 *
 * @author jbeckstrom
 */
public class CasavaSequenceIdentifier implements SequenceIdentifier{

    private final String instrumentName;
    private final String runId;
    private final String flowcellId;
    private final int flowcellLane;
    private final int tileNumber;
    private final int xCoord;
    private final int yCoord;
    
    private final int pairNum;
    private final String failFilter;
    private final int controlBits;
    private final String index;

    public CasavaSequenceIdentifier(String instrumentName, String runId, String flowcellId, int flowcellLane, int tileNumber, int xCoord, int yCoord, int pairNum, String failFilter, int controlBits, String index) {
        this.instrumentName = instrumentName;
        this.runId = runId;
        this.flowcellId = flowcellId;
        this.flowcellLane = flowcellLane;
        this.tileNumber = tileNumber;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.pairNum = pairNum;
        this.failFilter = failFilter;
        this.controlBits = controlBits;
        this.index = index;
    }

    public int getControlBits() {
        return controlBits;
    }

    public String getFailFilter() {
        return failFilter;
    }

    public String getFlowcellId() {
        return flowcellId;
    }

    public int getFlowcellLane() {
        return flowcellLane;
    }

    public String getIndex() {
        return index;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    @Override
    public int getPairNumber() {
        return pairNum;
    }

    public String getRunId() {
        return runId;
    }

    public int getTileNumber() {
        return tileNumber;
    }

    public int getxCoord() {
        return xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CasavaSequenceIdentifier other = (CasavaSequenceIdentifier) obj;
        if ((this.instrumentName == null) ? (other.instrumentName != null) : !this.instrumentName.equals(other.instrumentName)) {
            return false;
        }
        if ((this.runId == null) ? (other.runId != null) : !this.runId.equals(other.runId)) {
            return false;
        }
        if ((this.flowcellId == null) ? (other.flowcellId != null) : !this.flowcellId.equals(other.flowcellId)) {
            return false;
        }
        if (this.flowcellLane != other.flowcellLane) {
            return false;
        }
        if (this.tileNumber != other.tileNumber) {
            return false;
        }
        if (this.xCoord != other.xCoord) {
            return false;
        }
        if (this.yCoord != other.yCoord) {
            return false;
        }
        if (this.pairNum != other.pairNum) {
            return false;
        }
        if ((this.failFilter == null) ? (other.failFilter != null) : !this.failFilter.equals(other.failFilter)) {
            return false;
        }
        if (this.controlBits != other.controlBits) {
            return false;
        }
        if ((this.index == null) ? (other.index != null) : !this.index.equals(other.index)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.instrumentName != null ? this.instrumentName.hashCode() : 0);
        hash = 73 * hash + (this.runId != null ? this.runId.hashCode() : 0);
        hash = 73 * hash + (this.flowcellId != null ? this.flowcellId.hashCode() : 0);
        hash = 73 * hash + this.flowcellLane;
        hash = 73 * hash + this.tileNumber;
        hash = 73 * hash + this.xCoord;
        hash = 73 * hash + this.yCoord;
        hash = 73 * hash + this.pairNum;
        hash = 73 * hash + (this.failFilter != null ? this.failFilter.hashCode() : 0);
        hash = 73 * hash + this.controlBits;
        hash = 73 * hash + (this.index != null ? this.index.hashCode() : 0);
        return hash;
    }
    
}
