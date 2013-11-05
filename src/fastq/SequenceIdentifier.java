/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

/**
 *
 * @author jbeckstrom
 */
public interface SequenceIdentifier {
    
    public int getPairNumber();

    public int getFlowcellLane();
    
    public String getIndex();

    public String getInstrumentName();

    public int getTileNumber();

    public int getxCoord();

    public int getyCoord();
    
}
