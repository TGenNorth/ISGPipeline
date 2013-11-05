/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mummer.snps;

import java.util.Arrays;
import java.util.List;
import org.broad.tribble.Feature;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;


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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MumSNPFeature other = (MumSNPFeature) obj;
        if (this.rPos != other.rPos) {
            return false;
        }
        if ((this.rBase == null) ? (other.rBase != null) : !this.rBase.equals(other.rBase)) {
            return false;
        }
        if ((this.qBase == null) ? (other.qBase != null) : !this.qBase.equals(other.qBase)) {
            return false;
        }
        if (this.qPos != other.qPos) {
            return false;
        }
        if (this.buff != other.buff) {
            return false;
        }
        if (this.dist != other.dist) {
            return false;
        }
        if (this.rNumRepeat != other.rNumRepeat) {
            return false;
        }
        if (this.qNumRepeat != other.qNumRepeat) {
            return false;
        }
        if (this.rLength != other.rLength) {
            return false;
        }
        if (this.qLength != other.qLength) {
            return false;
        }
        if ((this.rContext == null) ? (other.rContext != null) : !this.rContext.equals(other.rContext)) {
            return false;
        }
        if ((this.qContext == null) ? (other.qContext != null) : !this.qContext.equals(other.qContext)) {
            return false;
        }
        if (this.rDir != other.rDir) {
            return false;
        }
        if (this.qDir != other.qDir) {
            return false;
        }
        if ((this.rFastaID == null) ? (other.rFastaID != null) : !this.rFastaID.equals(other.rFastaID)) {
            return false;
        }
        if ((this.qFastaID == null) ? (other.qFastaID != null) : !this.qFastaID.equals(other.qFastaID)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.rPos;
        hash = 29 * hash + (this.rBase != null ? this.rBase.hashCode() : 0);
        hash = 29 * hash + (this.qBase != null ? this.qBase.hashCode() : 0);
        hash = 29 * hash + this.qPos;
        hash = 29 * hash + this.buff;
        hash = 29 * hash + this.dist;
        hash = 29 * hash + this.rNumRepeat;
        hash = 29 * hash + this.qNumRepeat;
        hash = 29 * hash + this.rLength;
        hash = 29 * hash + this.qLength;
        hash = 29 * hash + (this.rContext != null ? this.rContext.hashCode() : 0);
        hash = 29 * hash + (this.qContext != null ? this.qContext.hashCode() : 0);
        hash = 29 * hash + this.rDir;
        hash = 29 * hash + this.qDir;
        hash = 29 * hash + (this.rFastaID != null ? this.rFastaID.hashCode() : 0);
        hash = 29 * hash + (this.qFastaID != null ? this.qFastaID.hashCode() : 0);
        return hash;
    }
    
    
}
