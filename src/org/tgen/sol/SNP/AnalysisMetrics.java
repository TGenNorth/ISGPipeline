package org.tgen.sol.SNP;

import org.tgen.sol.MappedBaseInfo;
import org.tgen.sol.PositionInfo;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Alexis
 * Date: Oct 21, 2010
 * Time: 3:54:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnalysisMetrics {

	public int snp_count = 0;
	public int transitions = 0;
	public int transversions = 0;
	public double balance_ref = 0.0;
	public double balance_het = 0.0;
	public double balance_hom = 0.0;
	public int n_ref = 0;
	public int n_het = 0;
	public int n_hom = 0;
	public int mismatch_total = 0;
	public int max_coverage = 0;
	final HashMap<String, Integer> mismatch_counts = new HashMap<String, Integer>();
	Set<Character> nuc_values = new TreeSet<Character>();
	final Map<SNPCallPair, Long> calltable = new HashMap<SNPCallPair, Long>();
	final SNPCallPair lookup = new SNPCallPair(CallType.Uncallable, CallType.Uncallable);

	AnalysisMetrics () {


	}

	public void UpdateSNPMetrics (SNPCall snp, SNPCall known_call, char reference_nucleotide) {

		if (snp.callType == CallType.Heterozygote || snp.callType == CallType.HomozygoteNonReference) {
			nuc_values.clear();

			nuc_values.add(reference_nucleotide);
			nuc_values.add(snp.allele1);
			nuc_values.add(snp.allele2);

			if (nuc_values.contains('G') && nuc_values.contains('A'))
				transitions++;
			else if (nuc_values.contains('C') && nuc_values.contains('T'))
				transitions++;
			else
				transversions++;
		}

		lookup.call1 = snp.callType;
		lookup.call2 = known_call.callType;

		Long count = calltable.get(lookup);
		if (count == null) {
			count = new Long(1);
			calltable.put((SNPCallPair) lookup.clone(), count);
		} else {
			count = count + 1;
			calltable.put(lookup, count);
		}
	}

	public void UpdatePositionMetrics (PositionInfo p, char reference_nucleotide) {
		Iterator<MappedBaseInfo> i = p.mappedBases.iterator();
		while (i.hasNext()) {
			MappedBaseInfo b = i.next();

			if (b.nucleotide != reference_nucleotide) {
				mismatch_total++;
				String mismatch_lookup = "";
				mismatch_lookup += reference_nucleotide;
				mismatch_lookup += b.nucleotide;

				Integer n = null;

				n = mismatch_counts.get(mismatch_lookup);

				if (n == null) {
					n = new Integer(0);
				}
				n = n + 1;
				mismatch_counts.put(mismatch_lookup, n);
			}
		}

	}


}
