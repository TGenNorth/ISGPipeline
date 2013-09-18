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
public class BamInputResource extends AbstractInputResource<File>  {
    
    public BamInputResource(String sampleName, File bam){
        super(sampleName, bam);
    }

    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }
}
