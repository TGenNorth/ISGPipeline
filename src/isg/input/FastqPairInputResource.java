/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import org.broadinstitute.sting.utils.collections.Pair;

/**
 *
 * @author jbeckstrom
 */
public class FastqPairInputResource extends AbstractInputResource<Pair<File, File>> {
    
    public FastqPairInputResource(String sampleName, Pair<File, File> pair){
        super(sampleName, pair);
    }
    
    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public InputResourceType type() {
        return InputResourceType.FASTQ;
    }
}
