/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.vcf;

import net.sf.samtools.util.CloseableIterator;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextIterator implements CloseableIterator<VariantContext>{

    private final VCFReader reader;
    private VariantContext next = null;;
    
    public VariantContextIterator(VCFReader reader){
        this.reader = reader;
        advance();
    }
    
    public void close() {
        //do nothing
    }
    
    private void advance(){
        next = reader.next();
    }

    public boolean hasNext() {
        return next!=null;
    }

    public VariantContext next() {
        VariantContext ret = next;
        advance();
        return ret;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
