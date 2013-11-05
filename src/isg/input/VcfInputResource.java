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
public class VcfInputResource extends AbstractInputResource<File>  {
    
    public VcfInputResource(String sampleName, File fasta){
        super(sampleName, fasta);
    }
    
    public static VcfInputResource create(File vcf) throws IOException, UserException{
        List<String> sampleNames = GenomicFileUtils.extractSampleNamesFromVCF(vcf);
        if(sampleNames.size()>1){
            throw new MoreThanOneSampleException(vcf);
        }else if(sampleNames.isEmpty()){
            throw new NoSampleDetectedException(vcf);
        }
        return new VcfInputResource(sampleNames.get(0), vcf);
    }

    @Override
    public void apply(InputResourceVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public InputResourceType type() {
        return InputResourceType.VCF;
    }
}
