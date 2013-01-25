/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.snpclassifier;

import org.biojava.bio.Annotation;
import org.biojavax.bio.seq.RichFeature;

/**
 *
 * @author jbeckstrom
 */
class CDS {
    private final String seq;
    private final int start;
    private final int end;
    private final int transTable;
    private final int codonStart;
    private final Strand strand;

    public CDS(String seq, int start, int end, int transTable, int codonStart, Strand strand) {
        this.seq = seq;
        this.start = start;
        this.end = end;
        this.transTable = transTable;
        this.codonStart = codonStart;
        this.strand = strand;
    }

    public static CDS create(RichFeature feature, String dna) {
        if (!feature.getType().equalsIgnoreCase("CDS")) {
            throw new IllegalArgumentException("Invalid feature type: " + feature.getType());
        }
        int start = feature.getLocation().getMin();
        int stop = feature.getLocation().getMax();
        int transTable = 11;
        int codonStart = 1;
        
        final Annotation annot = feature.getAnnotation();
        if(annot.containsProperty("transl_table")){
            transTable = Integer.parseInt( (String)annot.getProperty("transl_table") );
        }
        if(annot.containsProperty("codon_start")){
            codonStart = Integer.parseInt( (String)annot.getProperty("codon_start") );
        }
        
        Strand strand = Strand.POSITIVE;
        if (feature.getStrand().toString().equals("NEGATIVE")) {
//            dna = feature.getSequence().subStr(start, stop).toUpperCase();
            dna = SnpClassifierUtils.revComp(dna);
            strand = Strand.NEGATIVE;
        }else{
//            dna = feature.getSequence().subStr(start, stop).toUpperCase();
        }
        return new CDS(dna, start, stop, transTable, codonStart, strand);
    }

    public int convertRefToCDSPos(int rPos) {
        if (strand == Strand.NEGATIVE) {
            return end - rPos + 1;
        } else {
            return rPos - start + 1;
        }
    }
    
    public char getCDSBase(int rPos){
        int cdsPos = convertRefToCDSPos(rPos);
        return seq.charAt(cdsPos-1);
    }

    public boolean shouldCompliment(int rPos, char rBase) {
        int cdsPos = convertRefToCDSPos(rPos);
        char cdsBase = seq.charAt(cdsPos-1);
        if (rBase == cdsBase) {
            return false;
        } else if (rBase == SnpClassifierUtils.compliment(cdsBase)) {
            return true;
        } else {
            throw new IllegalStateException("CDS state doesn't match ref state: " + cdsBase + " != " + rBase + " @ " + cdsPos);
        }
    }

    public Codon getCodon(int rPos) {
        Codon ret = null;
        int genePos = convertRefToCDSPos(rPos);
        try {
            ret = Codon.create(seq.substring(codonStart-1), genePos);
        }catch(Exception e){
            System.out.println("Could not create codon for cds: "+toString());
        }
        return ret;
    }

    public int getCodonStart() {
        return codonStart;
    }

    public int getTranslationTable() {
        return transTable;
    }

    public Strand getStrand() {
        return strand;
    }

    @Override
    public String toString() {
        return "CDS{" + "seq=" + seq + ", start=" + start + ", end=" + end + ", transTable=" + transTable + ", codonStart=" + codonStart + ", strand=" + strand + '}';
    }
    
}
