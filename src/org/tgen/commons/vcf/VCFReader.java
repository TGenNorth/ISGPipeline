/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.vcf;

import java.io.File;
import java.io.IOException;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.broadinstitute.sting.utils.codecs.vcf.VCFCodec;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class VCFReader {

    private CloseableTribbleIterator iter;
    private VCFHeader header;

    public VCFReader(File file) throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        TribbleIndexedFeatureReader<VariantContext> source = new TribbleIndexedFeatureReader<VariantContext>(file.getAbsolutePath(), new VCFCodec(), false);
        header = (VCFHeader) source.getHeader();
        iter = source.iterator();
    }

    public VCFHeader getHeader() {
        return header;
    }

    public VariantContext next() {
        VariantContext ret = null;
        boolean bad = false;
        while (true) {
            try {
                if(bad) iter.next();
                ret = (VariantContext) iter.next();
                break;
            } catch (Exception e) {
                System.out.println("skipping... "+e.getMessage());
                bad = true;
            }
        }
        return ret;
    }
    
    public static void main(String[] args) throws Exception{
        VCFReader r = new VCFReader(new File("merge.vcf"));
        VariantContext vc = r.next();
        System.out.println(vc.getGenotype(0).getPloidy());
        System.out.println(vc.getGenotype(1).getPloidy());
    }
    
}
