/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isg2;

import java.util.Comparator;
import net.sf.samtools.SAMSequenceDictionary;
import org.broad.tribble.Feature;

/**
 *
 * @author jbeckstrom
 */
public class TribbleFeatureOverlapComparator implements Comparator<Feature> {

    private final SAMSequenceDictionary seqDict;
    
    public TribbleFeatureOverlapComparator(final SAMSequenceDictionary seqDict){
        this.seqDict = seqDict;
    }
    
    @Override
    public int compare(Feature lhs, Feature rhs) {
        final int lhsIndex = seqDict.getSequenceIndex(lhs.getChr());
        final int rhsIndex = seqDict.getSequenceIndex(rhs.getChr());
        int retval = lhsIndex - rhsIndex;

        if (retval == 0) {
            if (lhs.getEnd() < rhs.getStart()) {
                return -1;
            } else if (lhs.getStart() > rhs.getEnd()) {
                return 1;
            } else {
                return 0;
            }
        }
        return retval;
    }
    
}
