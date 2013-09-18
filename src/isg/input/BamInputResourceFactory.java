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
public class BamInputResourceFactory implements InputResourceFactory<File> {

    @Override
    public boolean isResourceType(File f) {
        return (f.getName().endsWith(".bam"));
    }

    @Override
    public InputResource<File> create(File f) {
        try {
            final String sampleName = GenomicFileUtils.extractSampleNamesFromBAM(f).get(0);
            return new BamInputResource(sampleName, f);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
}
