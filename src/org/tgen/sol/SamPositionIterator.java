package org.tgen.sol;

import net.sf.samtools.SAMFileHeader.SortOrder;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 *
 */

/**
 * @author achristoforides
 *         <p/>
 *         SamPositionIterator is an interface to a SAM or BAM file that can be used to process the data as a sequence of positions in the genome,
 *         as opposed to mapped reads. The class returns PositionInfo objects with the coverage data, including bases aligned, quality scores, and strand.
 */
public class SamPositionIterator implements Enumeration<PositionInfo> {

	protected SAMFileReader samReader;
	protected Iterator<SAMRecord> samRecordIterator;
	protected String currentSequenceName = "";
	protected SAMRecord currentRecord = null;
	protected SAMRecord nextRecord = null;
	protected final LinkedHashMap<Integer, PositionInfo> localMap = new LinkedHashMap<Integer, PositionInfo>();
	protected final HashMap<Integer, Integer> cigarMap = new HashMap<Integer, Integer>();
	protected int head = 0;
	protected int minCoverage;
	protected int maxCoverage;
	protected int maxMateDistance;
	protected int minMAPQ;
	protected int minBaseQuality;
	private int minMateDistance;
	private int minAlignmentScore;

	public SAMFileReader getReader () {
		return samReader;
	}

	protected HashMap<Integer, Integer> ParseSAMString (char[] cigar) throws Exception {
		cigarMap.clear();

		int i = 0;
		int ref_pos = 0;
		int read_pos = 0;

		while (i < cigar.length - 1) {
			String size_str = "";
			Integer size;
			char type;

			//size part of the CIGAR string
			while (Character.isDigit(cigar[i])) {
				size_str += cigar[i];
				i++;
			}

			if (size_str.length() == 0)
				throw new Exception("Invalid CIGAR string.");

			size = Integer.parseInt(size_str);

			//type part of the CIGAR string
			type = cigar[i];
			i++;

			if (type == '=' || type == 'X')
				type = 'M'; // we get match/mismatch directly from the reference

			switch (type) {
				//normal match/mismatch
				case 'M': {
					for (int x = 0; x < size; x++) {
						cigarMap.put(read_pos, ref_pos);

						ref_pos++;
						read_pos++;
					}
					break;
				}
				//soft clip
				case 'S': {
					read_pos += size;
					break;
				}
				//insertion
				case 'I': {
					read_pos += size;
					break;
				}
				// skip
				case 'N': {
					ref_pos += size;
					break;
				}
				//deletion
				case 'D': {
					ref_pos += size;
					break;
				}
				//hard clip
				case 'H': {
					break;
				}
				//padding
				case 'P': {
					read_pos += size;
					break;
				}

			}

		}

		return cigarMap;
	}

	protected PositionInfo getNextPositionInfo () throws Exception {
		PositionInfo next_position_info = null;

		do {
			//First, look through existing data to see any 'finished' bases

			Iterator<PositionInfo> map_iterator = localMap.values().iterator();

			while (map_iterator.hasNext() && next_position_info == null) {
				PositionInfo entry = map_iterator.next();

				if (entry.position >= head) //positions at and beyond the head are still missing data
					break;

				if (entry.mappedBases.size() >= minCoverage)
					next_position_info = entry;
				else
					entry.mappedBases.clear();


				map_iterator.remove();
			}

			if (next_position_info != null)
				break;

			while (nextRecord == null) //fetch new record if one isn't waiting
			{
				nextRecord();

				if (isEndOfSequence())
					break;

				if (this.currentRecord != null && !this.currentRecord.getReadUnmappedFlag())
					nextRecord = this.currentRecord;
			}

			if (isEndOfSequence())
				break;

			try {

				String cigar_string = nextRecord.getCigarString();

				HashMap<Integer, Integer> read_map = ParseSAMString(cigar_string.toCharArray());

				byte[] bases = nextRecord.getReadBases();
				byte[] qualities = nextRecord.getBaseQualities();
				boolean strand = nextRecord.getReadNegativeStrandFlag();
				String sequence_name = nextRecord.getReferenceName();
				head = nextRecord.getAlignmentStart();

				for (int x = 0; x < nextRecord.getReadLength(); x++) {
					if (qualities[x] < minBaseQuality || bases[x] == 'N')
						continue;

					Integer map_pos = read_map.get(x);

					if (map_pos == null)
						continue; // This base does not map against the reference (clip, insertion)

					int position = head + map_pos;

					PositionInfo position_info = localMap.get(position);

					if (position_info == null) {
						// Tjhis is the first read that aligned on this position. We need a new PositionInfo object
						position_info = new PositionInfo();
						position_info.position = position;
						position_info.sequenceName = sequence_name;
						localMap.put(position, position_info);
					}

					if (maxCoverage != 0 && position_info.mappedBases.size() == maxCoverage) {
						//just exceeded maximum coverage, drop this PositionInfo completely
						localMap.remove(position_info);
					} else {
						MappedBaseInfo b = new MappedBaseInfo();
						b.nucleotide = (char) bases[x];
						b.quality = qualities[x];
						b.strand = strand;
						b.positionInRead = x;
						b.readLength = nextRecord.getReadLength();

						position_info.mappedBases.add(b);
					}

				}
			} catch (Exception e) {
				System.err.println("Error parsing read " + nextRecord.getReadName() + " : " + e.getStackTrace()[0].getLineNumber());
			}

			nextRecord = null; // processed.


		}
		while (next_position_info == null && !isEndOfSequence()); // Until we get a finished position, or the file is done.

		return next_position_info;
	}

        public SamPositionIterator (File file, int min_coverage, int max_coverage, int min_mapq, int min_mate_distance, int max_mate_distance, int min_base_quality, int min_alignment_score, String sequence, int start, int end) {
		samReader = new SAMFileReader(file, new File(file.getAbsolutePath() + ".bai"));

		assert (samReader.getFileHeader().getSortOrder() == SortOrder.coordinate); // The implementation assumes coordinates sorting.

		samRecordIterator = samReader.query(sequence, start, end, true);

		this.minCoverage = min_coverage;
		this.maxCoverage = max_coverage;
		this.minMAPQ = min_mapq;
		this.minMateDistance = min_mate_distance;
		this.maxMateDistance = max_mate_distance;
		this.minBaseQuality = min_base_quality;
		this.minAlignmentScore = min_alignment_score;

		nextRecord();
	}

	public SamPositionIterator (Iterator<SAMRecord> samRecordIterator, int min_coverage, int max_coverage, int min_mapq, int min_mate_distance, int max_mate_distance, int min_base_quality, int min_alignment_score) {
		this.samRecordIterator = samRecordIterator;
		this.minCoverage = min_coverage;
		this.maxCoverage = max_coverage;
		this.minMAPQ = min_mapq;
		this.minMateDistance = min_mate_distance;
		this.maxMateDistance = max_mate_distance;
		this.minBaseQuality = min_base_quality;
		this.minAlignmentScore = min_alignment_score;

		nextRecord();
	}

	public SamPositionIterator (File file, int min_coverage, int max_coverage, int min_mapq, int min_mate_distance, int max_mate_distance, int min_base_quality, int min_alignment_score) {
		samReader = new SAMFileReader(file);

		assert (samReader.getFileHeader().getSortOrder() == SortOrder.coordinate); // The implementation assumes coordinates sorting.
		samRecordIterator = samReader.iterator();

		this.minCoverage = min_coverage;
		this.maxCoverage = max_coverage;
		this.minMAPQ = min_mapq;
		this.minMateDistance = min_mate_distance;
		this.maxMateDistance = max_mate_distance;
		this.minBaseQuality = min_base_quality;
		this.minAlignmentScore = min_alignment_score;

		nextRecord();
	}

	private boolean nextRecord () {
		currentRecord = null;

		if (samRecordIterator.hasNext()) {
			try {
				do {
					currentRecord = samRecordIterator.next();
					if (currentRecord.getMappingQuality() < minMAPQ) {
						currentRecord = null;
					} else if (currentRecord.getReadPairedFlag() && (Math.abs(currentRecord.getInferredInsertSize()) > maxMateDistance || Math.abs(currentRecord.getInferredInsertSize()) < minMateDistance)) {
						currentRecord = null;
					} else if (minAlignmentScore != 0 && ((Integer) currentRecord.getAttribute("AS")) < minAlignmentScore) {
						currentRecord = null;
					}

				} while (currentRecord == null && samRecordIterator.hasNext());
			} catch (Exception e) {
				System.err.println("Validation exception: " + e.getMessage());
			}
		}
		return (currentRecord != null);

	}

	public boolean nextSequence () {
		if (currentRecord != null) {
			currentSequenceName = currentRecord.getReferenceName();
			head = 0;
			localMap.clear();

			return true;
		} else {
			return false;
		}

	}

	public String getCurrentSequence () {
		return currentSequenceName;
	}

	private boolean isEndOfSequence () {
		return (currentRecord == null || !currentSequenceName.equals(currentRecord.getReferenceName()));
	}

	public boolean hasMoreElements () {
		return (!isEndOfSequence() || localMap.size() > 0); // while there's records in the SAM or positions in the hash map, we're not done.
	}

	public PositionInfo nextElement () {
		try {
			return getNextPositionInfo();
		} catch (Exception e) {
			return null;
		}


	}

	/**
	 * @param args
	 */
}
