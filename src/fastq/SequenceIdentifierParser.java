/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

/**
 *
 * @author jbeckstrom
 */
public interface SequenceIdentifierParser {
    
    public boolean canParse(String str);
    
    public SequenceIdentifier parse(String str);
    
}
