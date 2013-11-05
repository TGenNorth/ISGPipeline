/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import isg.util.Algorithm;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.picard.util.OverlapDetector;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.variant.vcf.VCFConstants;
import util.VariantContextUtils;

/**
 * Marks a VariantContext object as duplicated by assigning a flag to the FILTER 
 * field if in a duplicated region.
 * 
 * @author jbeckstrom
 */
public class MarkDuplicates implements Algorithm<VariantContext, VariantContext> {

    public static final String DUPLICATE_FILTER = "DUP";
    private final OverlapDetector<Interval> overlapDetector;

    public MarkDuplicates(OverlapDetector<Interval> overlapDetector){
        this.overlapDetector = overlapDetector;
    }
    
    public static MarkDuplicates createFromIntervalListFile(File f){
        IntervalList list = IntervalList.fromFile(f);
        OverlapDetector<Interval> overlapDetector = new OverlapDetector<Interval>(0,0);
        for(Interval i: list.getUniqueIntervals()){
            overlapDetector.addLhs(null, i);
        }
        return new MarkDuplicates(overlapDetector);
    }
    
    @Override
    public VariantContext apply(VariantContext vc) {
        Collection<Interval> overlaps = overlapDetector.getOverlaps(new Interval(vc.getChr(), vc.getStart(), vc.getEnd()));
        if (!overlaps.isEmpty()) {
            return markDuplicated(vc);
        }
        return vc;
    }
    
    public VariantContext markDuplicated(VariantContext vc){
        return new VariantContextBuilder(vc).filter(DUPLICATE_FILTER).make();
    }
}
