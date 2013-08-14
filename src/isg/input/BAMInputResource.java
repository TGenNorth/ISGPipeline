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
public class BAMInputResource extends InputResource {
    
    private final File bamFile;
    
    private BAMInputResource(String sample, File file){
        super(sample);
        this.bamFile = file;
    }
    
    public static BAMInputResource create(File bamFile) throws IOException{
        List<String> sampleNames = GenomicFileUtils.extractSampleNamesFromBAM(bamFile);
        if(sampleNames.isEmpty()){
            throw new IllegalArgumentException("no sample name found in file: "+bamFile);
        }else if(sampleNames.size()>1){
            throw new IllegalArgumentException("more than one sample found in file: "+bamFile);
        }
        return new BAMInputResource(sampleNames.get(0), bamFile);
    }

    public File getBamFile() {
        return bamFile;
    }
    
}
