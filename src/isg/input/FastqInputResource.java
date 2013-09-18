/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public class FastqInputResource extends AbstractInputResource<File>  {
    
    public FastqInputResource(String sampleName, File fastq){
        super(sampleName, fastq);
    }
    
    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }
}
