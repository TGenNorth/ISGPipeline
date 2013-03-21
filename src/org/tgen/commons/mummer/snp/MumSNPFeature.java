/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.mummer.snp;

import java.util.Arrays;
import java.util.List;
import org.broad.tribble.Feature;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;


/**
 *
 * @author jbeckstrom
 */
public class MumSNPFeature implements Feature{

    public static final String INDEL_BASE = ".";

    private final int rPos;
    private final String rBase;
    private final String qBase;
    private final int qPos;
    private final int buff;
    private final int dist;
    private final int rNumRepeat;
    private final int qNumRepeat;
    private final int rLength;
    private final int qLength;
    private final String rContext;
    private final String qContext;
    private final int rDir;
    private final int qDir;
    private final String rFastaID;
    private final String qFastaID;

    public MumSNPFeature(int rPos,
                         String rBase,
                         String qBase,
                         int qPos,
                         int buff,
                         int dist,
                         int rNumRepeat,
                         int qNumRepeat,
                         int rLength,
                         int qLength,
                         String rContext,
                         String qContext,
                         int rDir,
                         int qDir,
                         String rFastaID,
                         String qFastaID) {
        this.rPos = rPos;
        this.rBase = rBase;
        this.qBase = qBase;
        this.qPos = qPos;
        this.buff = buff;
        this.dist = dist;
        this.rNumRepeat = rNumRepeat;
        this.qNumRepeat = qNumRepeat;
        this.rLength = rLength;
        this.qLength = qLength;
        this.rContext = rContext;
        this.qContext = qContext;
        this.rDir = rDir;
        this.qDir = qDir;
        this.rFastaID = rFastaID;
        this.qFastaID = qFastaID;
    }

    public String getChr() {
        return rFastaID;
    }

    public int getStart() {
        return rPos;
    }

    public int getEnd() {
        return rPos;
    }

    public int getBuff() {
        return buff;
    }

    public int getDist() {
        return dist;
    }

    public String getqBase() {
        return qBase;
    }

    public String getqContext() {
        return qContext;
    }

    public int getqDir() {
        return qDir;
    }

    public String getqFastaID() {
        return qFastaID;
    }

    public int getqLength() {
        return qLength;
    }

    public int getqNumRepeat() {
        return qNumRepeat;
    }

    public int getqPos() {
        return qPos;
    }

    public String getrBase() {
        return rBase;
    }

    public String getrContext() {
        return rContext;
    }

    public int getrDir() {
        return rDir;
    }

    public int getrLength() {
        return rLength;
    }

    public int getrNumRepeat() {
        return rNumRepeat;
    }

    public VariantContext toVaraintContext(final String sampleName){
        VariantContextBuilder vcBldr = new VariantContextBuilder();
        vcBldr.chr(rFastaID);
        vcBldr.start(rPos);
        vcBldr.stop(rPos);
        
        Allele refAllele = Allele.create(rBase, true);
        Allele altAllele = Allele.create(qBase);
        if(rNumRepeat>0 || qNumRepeat>0){
            altAllele = Allele.create("N");
        }
        List<Allele> alleles = Arrays.asList(refAllele, altAllele);
        
        Genotype g = GenotypeBuilder.create(sampleName, alleles);
        vcBldr.genotypes(g);
        vcBldr.alleles(alleles);
        
        return vcBldr.make();
    }
}
