/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sf.picard.PicardException;
import org.broad.tribble.AbstractFeatureReader;
import org.broad.tribble.CloseableTribbleIterator;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.tgen.commons.mummer.snp.MumSNPCodec;
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 * An allele caller that uses a set of known snps to validate the ones being called.
 * If the snps don't match up an allele of 'N' is given.
 * 
 * @author jbeckstrom
 */
public class ValidatingMumSNPAlleleCaller implements SNPAlleleCaller<MumSNPFeature> {

    private final Map<String, Map<Integer, MumSNPFeature>> mumSnps =
            new HashMap<String, Map<Integer, MumSNPFeature>>();

    public ValidatingMumSNPAlleleCaller(File mumSnpFile) {
        CloseableTribbleIterator<MumSNPFeature> iter = createMumSnpIter(mumSnpFile);
        while (iter.hasNext()) {
            MumSNPFeature f = iter.next();
            putMumSnp(f.getChr(), f.getStart(), f);
            putMumSnp(f.getqFastaID(), f.getqPos(), f);
        }
    }

    private void putMumSnp(String chr, Integer pos, MumSNPFeature snp) {
        Map<Integer, MumSNPFeature> m = mumSnps.get(chr);
        if (m == null) {
            m = new HashMap<Integer, MumSNPFeature>();
            mumSnps.put(chr, m);
        }
        m.put(pos, snp);
    }

    @Override
    public Allele callAlternate(MumSNPFeature snp) {
        return call(snp.getqBase());
    }

    @Override
    public Allele callReference(MumSNPFeature snp) {
        return Allele.create(snp.getrBase(), true);
    }

    private Allele call(String base) {
        if (!base.equals(".")) {
            return Allele.create(base);
        } else {
            return Allele.NO_CALL;
        }
    }

    private CloseableTribbleIterator<MumSNPFeature> createMumSnpIter(File snps) {
        try {
            AbstractFeatureReader<MumSNPFeature> reader = AbstractFeatureReader.getFeatureReader(snps.getAbsolutePath(), new MumSNPCodec(), false);
            return reader.iterator();
        } catch (IOException ex) {
            throw new PicardException("An error occured trying to read file", ex);
        }
    }
}
