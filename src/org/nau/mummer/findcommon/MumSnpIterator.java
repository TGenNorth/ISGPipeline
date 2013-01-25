/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.broad.tribble.CloseableTribbleIterator;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.tgen.commons.mummer.snp.MumSNPCodec;
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 * Iterate over snps
 * @author jbeckstrom
 */
public class MumSnpIterator implements Iterator<MumSNPFeature>{

    private CloseableTribbleIterator<MumSNPFeature> iter;
    private MumSNPFeature nextSnp;
    
    public MumSnpIterator(File mumSnpFile) {
        try {
            TribbleIndexedFeatureReader reader = new TribbleIndexedFeatureReader<MumSNPFeature>(mumSnpFile.getAbsolutePath(), new MumSNPCodec(), false);
            iter = reader.iterator();
        } catch (IOException ex) {
            Logger.getLogger(MumSnpIterator.class.getName()).log(Level.SEVERE, null, ex);
        }
        advanceSnp();
    }
    
    private void advanceSnp(){
        nextSnp = null;
        while(iter.hasNext()){
            MumSNPFeature f = iter.next();
            if(f.getrBase().matches("[ATCG]") && f.getqBase().matches("[ATCG]")){
                nextSnp = f;
                break;
            }
        }
    }
    
    @Override
    public boolean hasNext() {
        return nextSnp!=null;
    }

    @Override
    public MumSNPFeature next() {
        if(!hasNext()){
            throw new IllegalStateException("cannot call next() on exhausted iterator");
        }
        MumSNPFeature ret = nextSnp;
        advanceSnp();
        return ret;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
