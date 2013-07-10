/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

/**
 *
 * @author jbeckstrom
 */
public class UnknownSequenceIdentifier implements SequenceIdentifier{

    @Override
    public int getPairNumber() {
        return -1;
    }

    @Override
    public int getFlowcellLane() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getIndex() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getInstrumentName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getTileNumber() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getxCoord() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getyCoord() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
