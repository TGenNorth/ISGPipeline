/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import isg.input.InputResourceValidationExceptions.MoreThanOneSampleException;
import isg.input.InputResourceValidationExceptions.NoSampleDetectedException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.broadinstitute.sting.utils.exceptions.UserException;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class BamInputResource extends AbstractInputResource<File>  {
    
    public BamInputResource(String sampleName, File bam){
        super(sampleName, bam);
    }
    
    public static BamInputResource create(File bam) throws IOException, UserException{
        List<String> sampleNames = GenomicFileUtils.extractSampleNamesFromBAM(bam);
        if(sampleNames.size()>1){
            throw new MoreThanOneSampleException(bam);
        }else if(sampleNames.isEmpty()){
            throw new NoSampleDetectedException(bam);
        }
        return new BamInputResource(sampleNames.get(0), bam);
    }

    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public InputResourceType type() {
        return InputResourceType.BAM;
    }
}
