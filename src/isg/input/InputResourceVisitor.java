/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

/**
 *
 * @author jbeckstrom
 */
public interface InputResourceVisitor {
    
    public void visit(VcfInputResource resource);
    
    public void visit(FastaInputResource resource);
    
    public void visit(BamInputResource resource);
    
    public void visit(FastqInputResource resource);
    
    public void visit(FastqPairInputResource resource);
    
}
