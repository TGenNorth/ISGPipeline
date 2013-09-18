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
public class VcfInputResourceFactory implements InputResourceFactory<File> {

    @Override
    public boolean isResourceType(File f) {
        return (f.getName().endsWith(".vcf"));
    }

    @Override
    public InputResource<File> create(File f) {
        try {
            final String sampleName = GenomicFileUtils.extractSampleNamesFromVCF(f).get(0);
            return new VcfInputResource(sampleName, f);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
    
}
