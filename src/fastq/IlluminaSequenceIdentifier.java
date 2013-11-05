/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

/**
 *
 * @author jbeckstrom
 */
public class IlluminaSequenceIdentifier implements SequenceIdentifier{

    private final String instrumentName;
    private final int flowcellLane;
    private final int tileNumber;
    private final int xCoord;
    private final int yCoord;
    private final String index;
    private final int pairNum;

    public IlluminaSequenceIdentifier(String instrumentName, int flowcellLane, int tileNumber, int xCoord, int yCoord, String index, int pairNum) {
        this.instrumentName = instrumentName;
        this.flowcellLane = flowcellLane;
        this.tileNumber = tileNumber;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.index = index;
        this.pairNum = pairNum;
    }
    
    @Override
    public int getPairNumber() {
        return pairNum;
    }

    @Override
    public int getFlowcellLane() {
        return flowcellLane;
    }

    @Override
    public String getIndex() {
        return index;
    }

    @Override
    public String getInstrumentName() {
        return instrumentName;
    }

    @Override
    public int getTileNumber() {
        return tileNumber;
    }

    @Override
    public int getxCoord() {
        return xCoord;
    }

    @Override
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
        final IlluminaSequenceIdentifier other = (IlluminaSequenceIdentifier) obj;
        if ((this.instrumentName == null) ? (other.instrumentName != null) : !this.instrumentName.equals(other.instrumentName)) {
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
        if ((this.index == null) ? (other.index != null) : !this.index.equals(other.index)) {
            return false;
        }
        if (this.pairNum != other.pairNum) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.instrumentName != null ? this.instrumentName.hashCode() : 0);
        hash = 79 * hash + this.flowcellLane;
        hash = 79 * hash + this.tileNumber;
        hash = 79 * hash + this.xCoord;
        hash = 79 * hash + this.yCoord;
        hash = 79 * hash + (this.index != null ? this.index.hashCode() : 0);
        hash = 79 * hash + this.pairNum;
        return hash;
    }

    @Override
    public String toString() {
        return "IlluminaSequenceIdentifier{" + "instrumentName=" + instrumentName + ", flowcellLane=" + flowcellLane + ", tileNumber=" + tileNumber + ", xCoord=" + xCoord + ", yCoord=" + yCoord + ", index=" + index + ", pairNum=" + pairNum + '}';
    }
    
    
    
}
