/**
 * 
 */
package org.tgen.sol;

import java.util.ArrayList;
import java.util.List;

/**
 * @author achristoforides
 * Contains alignment information for a single-base position.
 */
public class PositionInfo  {
	public int position;
	public String sequenceName;
	public List<MappedBaseInfo> mappedBases = new ArrayList<MappedBaseInfo>();
}