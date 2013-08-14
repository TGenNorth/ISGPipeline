/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.io.IOException;
import java.util.List;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class VCFInputResource extends InputResource {
    
    private final File vcfFile;
    
    private VCFInputResource(String sample, File file){
        super(sample);
        this.vcfFile = file;
    }
    
    public static VCFInputResource create(File vcfFile) throws IOException{
        List<String> sampleNames = GenomicFileUtils.extractSampleNamesFromVCF(vcfFile);
        if(sampleNames.isEmpty()){
            throw new IllegalArgumentException("no sample name found in file: "+vcfFile);
        }else if(sampleNames.size()>1){
            throw new IllegalArgumentException("more than one sample found in file: "+vcfFile);
        }
        return new VCFInputResource(sampleNames.get(0), vcfFile);
    }

    public File getVcfFile() {
        return vcfFile;
    }
    
}
