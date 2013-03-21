/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg.tools.snpclassifier;

/**
 *
 * @author jbeckstrom
 */
class Codon {
    private final String dna;
    private final int index;

    public Codon(char[] arr, int index) {
        this(new String(arr), index);
    }

    public Codon(String dna, int index) {
        if (dna.length() != 3) {
            throw new IllegalArgumentException("Invalid codon DNA sequence: " + dna);
        }
        this.dna = dna;
        this.index = index;
    }

    /**
     * Creates a Codon object from the 1-based position in the dna sequence
     * @param dna sequence to extract codon from
     * @param pos 1-based position in dna sequence where codon resides
     * @return Codon object
     */
    public static Codon create(String dna, int pos) {
        if (dna.length() % 3 != 0) {
            throw new IllegalArgumentException("Invalid codon DNA sequence: " + dna);
        }
        int index = pos%3;
        String codonSeq = null;
        int codonIndex = -1;
        
        if(index==1){
            codonSeq = dna.substring(pos-1, pos+2);
            codonIndex = 0;
        }else if(index==2){
            codonSeq = dna.substring(pos-2, pos+1);
            codonIndex = 1;
        }else{
            codonSeq = dna.substring(pos-3, pos);
            codonIndex = 2;
        }
        
        return new Codon(codonSeq, codonIndex);
    }
    
    public Codon getDerivedCodon(char alelle) {
        if(index==-1) throw new IllegalStateException("Cannot calculate derived codon if index is not set.");
        char[] arr = dna.toCharArray();
        arr[index] = alelle;
        return new Codon(arr, index);
    }

    public String getAminoAcid(int transTable) {
        return SnpClassifierUtils.getTranslation(dna, transTable);
    }

    public String getDna() {
        return dna;
    }
    
    
}
