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
public class VcfInputResource extends AbstractInputResource<File>  {
    
    public VcfInputResource(String sampleName, File fasta){
        super(sampleName, fasta);
    }

    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }
}
