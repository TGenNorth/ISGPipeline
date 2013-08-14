/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.io.IOException;
import java.util.List;
import util.FileUtils;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class FASTAInputResource extends InputResource {
    
    private final File fastaFile;
    
    private FASTAInputResource(String sample, File file){
        super(sample);
        this.fastaFile = file;
    }
    
    public static FASTAInputResource create(File fastaFile) throws IOException{
        final String sample = FileUtils.stripExtension(fastaFile);
        return new FASTAInputResource(sample, fastaFile);
    }

    public File getFastaFile() {
        return fastaFile;
    }
    
}
