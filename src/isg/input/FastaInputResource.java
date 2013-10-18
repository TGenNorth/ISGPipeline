/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.io.IOException;
import util.FileUtils;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class FastaInputResource extends AbstractInputResource<File>  {
    
    public FastaInputResource(String sampleName, File fasta){
        super(sampleName, fasta);
    }
    
    public static FastaInputResource create(File f) throws IOException {
        final String sampleName = FileUtils.stripExtension(f);
        return new FastaInputResource(sampleName, f);
    }

    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public InputResourceType type() {
        return InputResourceType.FASTA;
    }
}
