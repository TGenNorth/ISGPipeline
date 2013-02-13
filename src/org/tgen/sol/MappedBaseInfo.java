/**
 * 
 */
package org.tgen.sol;

/**
 * @author achristoforides
 *
 * Contains information for a single base within a mapped read;
 */
public class MappedBaseInfo {
	public char nucleotide;
	public int quality;
	public int positionInRead;
	public int readLength;
	public boolean strand;
}
