/**
 *
 */
package org.tgen.sol.SNP;

import java.util.HashMap;

enum CallType {
	NoCall, //Could not call (ambiguity or other problem)
	Unknown, //Not considered a SNP
	Uncallable, //Not enough information to call
	HomozygoteReference, //Homozygous genotype call, matching the reference sequence
	Heterozygote, //Heterozygous genotype call
	HomozygoteNonReference //Homozygous genotype call, not matching the reference sequence
}

/**
 * @author Alexis
 */
public class SNPCall {

	CallType callType;
	public char allele1;
	public char allele2;
	public double genotypeProb;
	public double variantProb;
	public String ID = null;
	HashMap<String, Double> scores = new HashMap<String, Double>();
	public String misc = new String();
        
        public char getAlternateAllele(){
            switch(callType){
                case HomozygoteReference:
                    return '.';
                case Heterozygote:
                case HomozygoteNonReference:
                    return allele2;
                default:
                    return 'N';
            }
        }

	public String toString () {
		return new String(allele1 + "" + allele2);
	}

	public String toString2 () {
		return new String(allele2 + "" + allele1);
	}
}

