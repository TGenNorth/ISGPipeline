/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.io.IOException;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class FastaInputResourceFactory implements InputResourceFactory<File> {

    @Override
    public boolean isResourceType(File f) {
        return (f.getName().endsWith(".fasta"));
    }

    @Override
    public InputResource<File> create(File f) {
        try {
            final String sampleName = GenomicFileUtils.extractFirstSampleName(f);
            return new FastaInputResource(sampleName, f);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
}
