/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.util.List;
import net.sf.picard.util.OverlapDetector;
import net.sf.picard.util.Interval;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class MarkDuplicatesTest {
    
    public MarkDuplicatesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of apply method, of class MarkDuplicates.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        OverlapDetector<Interval> od = new OverlapDetector<Interval>(0,0);
        od.addLhs(null, new Interval("chr1", 10, 20));
        MarkDuplicates instance = new MarkDuplicates(od);
        
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Arrays.asList(Allele.create("A", true))).make();
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Arrays.asList(Allele.create("A", true))).make();
        VariantContext result = instance.apply(vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
        
        vc = new VariantContextBuilder("", "chr1", 15, 15, Arrays.asList(Allele.create("A", true))).make();
        expResult = new VariantContextBuilder("", "chr1", 15, 15, Arrays.asList(Allele.create("A", true)))
                .filter(MarkDuplicates.DUPLICATE_FILTER).make();
        result = instance.apply(vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }

    /**
     * Test of markDuplicated method, of class MarkDuplicates.
     */
    @Test
    public void testMarkDuplicated() {
        System.out.println("markDuplicated");
        List<Allele> alleles = Arrays.asList(Allele.create("A", true));
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, alleles).make();
        MarkDuplicates instance = new MarkDuplicates(new OverlapDetector<Interval>(0,0));
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, alleles)
                .filter(MarkDuplicates.DUPLICATE_FILTER).make();
        VariantContext result = instance.markDuplicated(vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }
}
