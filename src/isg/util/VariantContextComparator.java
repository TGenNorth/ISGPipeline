/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.util.Comparator;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextComparator implements Comparator<VariantContext> {
    private final SAMSequenceDictionary seqDict;

    /** Constructs a comparator using the supplied sequence header. */
    public VariantContextComparator(final SAMFileHeader header) {
        this(header.getSequenceDictionary());
    }

    public VariantContextComparator(final SAMSequenceDictionary seqDict) {
        this.seqDict = seqDict;
    }

    @Override
    public int compare(VariantContext lhs, VariantContext rhs) {
        final int lhsIndex = this.seqDict.getSequenceIndex(lhs.getChr());
        final int rhsIndex = this.seqDict.getSequenceIndex(rhs.getChr());
        int retval = lhsIndex - rhsIndex;
        if (retval == 0) {
            retval = lhs.getStart() - rhs.getStart();
        }
        if (retval == 0) {
            retval = lhs.getEnd() - rhs.getEnd();
        }
        return retval;
    }
    
}
