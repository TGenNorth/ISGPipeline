package org.tgen.sol.SNP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tgen.sol.MappedBaseInfo;
import org.tgen.sol.PositionInfo;

public class SolSNPCaller {

	//Configuration
	private StrandMode strandMode;
	double callBias;
	Ploidy ploidy;
	//SNPCall consensus_call = new SNPCall();
	SNPCall forward_strand_call;
	SNPCall reverse_strand_call;
	final List<MappedBaseInfo> consensus_bases;
	final List<MappedBaseInfo> positive_strand_bases;
	final List<MappedBaseInfo> negative_strand_bases;
	public static double[][] TRANSITION_ERROR_PROBABLITY = {{0, 0, 0, 0},
			{0, 0, 0, 0},
			{0, 0, 0, 0},
			{0, 0, 0, 0}};

	public void strandCall (List<MappedBaseInfo> mappedBases, char reference, double tolerance) {
		positive_strand_bases.clear();
		negative_strand_bases.clear();
		for (MappedBaseInfo b : mappedBases) {
			if (b.strand)
				positive_strand_bases.add(b);
			else
				negative_strand_bases.add(b);
		}

		isSNP(positive_strand_bases, reference, callBias, forward_strand_call);
		isSNP(negative_strand_bases, reference, callBias, reverse_strand_call);
	}

	public SolSNPCaller (StrandMode sm, double tl, Ploidy pl) {
		strandMode = sm;
		callBias = tl;
		ploidy = pl;
		negative_strand_bases = new ArrayList<MappedBaseInfo>();
		positive_strand_bases = new ArrayList<MappedBaseInfo>();
		consensus_bases = new ArrayList<MappedBaseInfo>();
		reverse_strand_call = new SNPCall();
		forward_strand_call = new SNPCall();
	}

	public void isSNP (PositionInfo position, char reference, SNPCall consensus_call) {
		consensus_call.callType = CallType.Unknown;

		switch (strandMode) {
			case None: {
				isSNP(position.mappedBases, reference, callBias, consensus_call);
				break;
			}
			case OneStrandAndTotal: {

				isSNP(position.mappedBases, reference, callBias, consensus_call);

				strandCall(position.mappedBases, reference, callBias);

				if (!(forward_strand_call.callType == consensus_call.callType || reverse_strand_call.callType == consensus_call.callType)) {
					consensus_call.callType = CallType.NoCall;
				}
				break;
			}
			case NoneWithStrandInfo: {
				isSNP(position.mappedBases, reference, callBias, consensus_call);
				strandCall(position.mappedBases, reference, callBias);
				break;
			}
			case VariantConsensus: {
				strandCall(position.mappedBases, reference, callBias);

				if (forward_strand_call.callType == reverse_strand_call.callType) {
					consensus_call.allele1 = forward_strand_call.allele1;
					consensus_call.allele2 = forward_strand_call.allele2;
					consensus_call.callType = forward_strand_call.callType;
					consensus_call.genotypeProb = (forward_strand_call.genotypeProb + reverse_strand_call.genotypeProb) / 2;
					consensus_call.variantProb = (forward_strand_call.variantProb + reverse_strand_call.variantProb) / 2;
				} else if (forward_strand_call.callType == CallType.Heterozygote && reverse_strand_call.callType == CallType.HomozygoteNonReference
						|| forward_strand_call.callType == CallType.HomozygoteNonReference && reverse_strand_call.callType == CallType.Heterozygote) {
					//Indeterminate genotype call, but a variant nonetheless
					consensus_call.callType = CallType.Heterozygote;
					consensus_call.allele1 = reference;
					consensus_call.variantProb = (forward_strand_call.variantProb + reverse_strand_call.variantProb) / 2;

					if (forward_strand_call.allele2 != reverse_strand_call.allele2) {
						consensus_call.genotypeProb = +0.0;
						consensus_call.allele2 = 'N';
					} else {
						consensus_call.allele2 = forward_strand_call.allele2;
					}
				}
				// Assert that the non-reference alleles in the two strands are equal
				else if (forward_strand_call.callType != reverse_strand_call.callType || forward_strand_call.allele2 != reverse_strand_call.allele2) {
					consensus_call.genotypeProb = +0.0;
					consensus_call.variantProb = (forward_strand_call.variantProb + reverse_strand_call.variantProb) / 2;
					consensus_call.allele2 = 'N';

					if (forward_strand_call.allele1 != reverse_strand_call.allele1)
						consensus_call.allele1 = 'N';

					consensus_call.callType = CallType.NoCall;
				}
				break;
			}
			case GenotypeConsensus: {
				strandCall(position.mappedBases, reference, callBias);

				if (forward_strand_call.callType == reverse_strand_call.callType && forward_strand_call.allele2 == reverse_strand_call.allele2) {
					consensus_call.allele1 = forward_strand_call.allele1;
					consensus_call.allele2 = forward_strand_call.allele2;
					consensus_call.callType = forward_strand_call.callType;
					consensus_call.genotypeProb = (forward_strand_call.genotypeProb + reverse_strand_call.genotypeProb) / 2;
					consensus_call.variantProb = (forward_strand_call.variantProb + reverse_strand_call.variantProb) / 2;
				} else {
					consensus_call.callType = CallType.NoCall;
					consensus_call.genotypeProb = +0.0;
					consensus_call.allele2 = 'N';
					consensus_call.allele1 = 'N';
					consensus_call.variantProb = (forward_strand_call.variantProb + reverse_strand_call.variantProb) / 2;
				}
				break;
			}

		}
	}

	public static double CalculateAlleleBalance (List<MappedBaseInfo> mappedBases,
												 Character referenceNucleotide) {
		double refcount = 0;
		double altcount = 0;

		for (MappedBaseInfo m : mappedBases) {
			if (m.nucleotide == referenceNucleotide)
				refcount += 1 - Math.pow(10, (-m.quality / 10));
			else
				altcount += 1 - Math.pow(10, (-m.quality / 10));

		}

		return (altcount / (altcount + refcount));
	}

	public void isSNPnew (List<MappedBaseInfo> mappedBases, char reference, double tolerance, SNPCall call) {
		call.callType = CallType.NoCall;

		double score = SolSNPCaller.CalculateAlleleBalance(mappedBases, reference);

		double score_ref = tolerance + score;
		double score_het = Math.abs(0.5 - score);
		double score_hom = 1.0 - score;

		double min_distance = Math.min(score_ref, Math.min(score_het, score_hom));

		if (min_distance == score_ref) {
			call.callType = CallType.HomozygoteReference;
			call.allele1 = reference;
			call.allele2 = reference;

		} else if (min_distance == score_het)
			call.callType = CallType.Heterozygote;

		else if (min_distance == score_hom)
			call.callType = CallType.HomozygoteNonReference;

		return;

	}

	public void isSNP (List<MappedBaseInfo> mappedBases, char reference, double tolerance, SNPCall call) {
		int count = mappedBases.size();

		call.callType = CallType.NoCall;

		List<MappedBaseInfo> nonreferenceMappedBases = new ArrayList<MappedBaseInfo>();

		char referenceAllele = reference;
		char nonreferenceAllele = ' ';
		char third_nonreference = ' '; // if there is more than one non-reference nucleotide at this locus

		List<Double> refBases = new ArrayList<Double>();
		List<Double> nonrefBases = new ArrayList<Double>();

		for (MappedBaseInfo b : mappedBases) {
			if (b.nucleotide == referenceAllele)
				refBases.add(qualityToProbability(b.quality));
			else {
				if (nonreferenceAllele == ' ')
					nonreferenceAllele = b.nucleotide;
				else if (nonreferenceAllele != b.nucleotide)
					third_nonreference = b.nucleotide; //A third allele was found; Note this so we can resolve which one is the dominant one

				nonrefBases.add(qualityToProbability(b.quality));
				nonreferenceMappedBases.add(b);
			}
		}

		// if there is no evidence of a non-reference allele, we definitely cannot
		// call it a SNP
		if (nonreferenceAllele == ' ') {
			call.allele1 = referenceAllele;
			call.allele2 = referenceAllele;
			call.genotypeProb = 1.0;
			call.variantProb = 0.0;
			call.callType = CallType.HomozygoteReference;
			return;
		}

		//if there is no evidence of a reference allele, we 

		// if there is evidence of more than one non-reference allele, we need to select one
		if (third_nonreference != ' ') {
			// use the first one we saw as the reference
			// (note: this may not be desirable default behavior
			// if the algorithm is biased towards the reference
			SNPCall nonref_call = new SNPCall();
			isSNP(nonreferenceMappedBases, nonreferenceAllele, 0, nonref_call);

			switch (nonref_call.callType) {
				case HomozygoteNonReference: {
					if (refBases.size() == 0) {
						call.callType = nonref_call.callType;
						call.allele1 = third_nonreference;
						call.allele2 = third_nonreference;
						call.variantProb = 1;
						call.genotypeProb = nonref_call.genotypeProb;
						return;
					}
					nonreferenceAllele = third_nonreference;

					break;
				}
				case HomozygoteReference: {
					if (refBases.size() == 0) {
						call.callType = CallType.HomozygoteNonReference;
						call.allele1 = nonreferenceAllele;
						call.allele2 = nonreferenceAllele;
						call.variantProb = 1;
						call.genotypeProb = nonref_call.genotypeProb;
						return;
					}

					break;
				}
				case Heterozygote: {
					if (refBases.size() == 0) {
						call.callType = CallType.Heterozygote;
						call.allele1 = nonreferenceAllele;
						call.allele2 = third_nonreference;
						call.variantProb = 1;
						call.genotypeProb = nonref_call.genotypeProb;
						return;
					} else {
						nonreferenceAllele = 'N';
					}
				}
				default: {
					if (refBases.size() == 0)
						referenceAllele = 'N';
					nonreferenceAllele = 'N';
				}
			}
		}

		Collections.sort(refBases);
		Collections.sort(nonrefBases);

		// For sample -> reference distribution comparison:
		// Score = The sum of probability of the non-reference bases
		double score_against_reference = sumList(nonrefBases) / count;

		// For sample -> non-reference distribution comparison:
		// Score = The sum of probability of the reference bases
		double score_against_nonreference = sumList(refBases) / count;

		// For sample -> heterozygote distribution comparison:
		// Score = The sum of probability of the 'different' bases, choosing the ones with the best quality
		double score_against_heterozygote = 0;

		switch (ploidy)	 //BUGBUG: Heterozygous score calculated incorrectly if there's only one base mapped
		{
			case Diploid:
				if (refBases.size() > nonrefBases.size()) {
					score_against_heterozygote = sumList(refBases.subList((int) Math.floor(count / 2), refBases.size())) / count;
				} else if (refBases.size() < nonrefBases.size()) {
					score_against_heterozygote = sumList(nonrefBases.subList((int) Math.floor(count / 2), nonrefBases.size())) / count;
				} else
					score_against_heterozygote = Math.abs(sumList(refBases) - sumList(nonrefBases)) / count; // lowest possible score, perfect split (is this right?)
				break;
			case Haploid:
				score_against_heterozygote = 2; //impossible value
				break;
		}

		call.scores.put("score_ref", score_against_reference);
		call.scores.put("score_nonref", score_against_nonreference);
		call.scores.put("score_het", score_against_heterozygote);

		double score_SNP = Math.min(score_against_heterozygote, score_against_nonreference);

		call.scores.put("sens", score_against_reference - score_SNP);

		score_against_reference = score_against_reference + tolerance;

		if (score_SNP <= score_against_reference) {
			if (score_SNP == score_against_nonreference) {
				call.allele1 = nonreferenceAllele;
				call.allele2 = nonreferenceAllele;
				call.callType = CallType.HomozygoteNonReference;
				call.variantProb = 1;
			} else {
				call.allele1 = referenceAllele;
				call.allele2 = nonreferenceAllele;
				call.callType = CallType.Heterozygote;
				call.scores.put("sens", score_against_reference - score_SNP);
				call.variantProb = (score_against_reference > 0.5 ? 1.0 : 1.0 - score_against_heterozygote);
			}

			//variant called: prob(1 - no variant)

			call.genotypeProb = 1.0 - score_SNP;

		} else {
			call.allele1 = referenceAllele;
			call.allele2 = referenceAllele;
			call.callType = CallType.HomozygoteReference;
			call.variantProb = score_against_reference;
			call.genotypeProb = 1.0 - score_against_reference;

		}

	}

	public double sumList (List<Double> list) {
		double sum = 0;
		for (Double d : list)
			sum += d;
		return sum;
	}

	public double qualityToProbability (int quality) {
		return (1 - Math.pow(10, (-quality / 10)));
	}

}
