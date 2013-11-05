/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.Genotype;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static isg.VariantContextTestUtils.*;

/**
 *
 * @author jbeckstrom
 */
public class MarkAmbiguousTest {
    
    private final MarkAmbiguousInfo info = new MarkAmbiguousInfo.Builder()
                .maxNumAlt(1)
                .minAF(.75)
                .minDP(10)
                .minGQ(10)
                .minQual(17)
                .build();
    
    public MarkAmbiguousTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of apply method, of class CallAmbiguous.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        
        MarkAmbiguous instance = new MarkAmbiguous(info);
        
        VariantContext expResult = createSNP("C", "G");
        VariantContext result = instance.apply(createSNP("C", "G"));
        assertVariantContextEquals(expResult, result);
        
    }
    
    /**
     * Test of apply method, of class CallAmbiguous with a minimum depth cutoff.
     */
    @Test
    public void testApplyMinDP() {
        System.out.println("applyMinDP");
        MarkAmbiguous instance = new MarkAmbiguous(info);
        Map<String, Object> attr = new HashMap<String,Object>();
        attr.put("DP", 9);
        VariantContext expResult = createSNP("C", "N", attr);
        VariantContext result = instance.apply(createSNP("C", "G", attr));
        assertVariantContextEquals(expResult, result);
        
        attr.put("DP", 10);
        expResult = createSNP("C", "G", attr);
        result = instance.apply(createSNP("C", "G", attr));
        assertVariantContextEquals(expResult, result);
        
    }
    
    /**
     * Test of apply method, of class CallAmbiguous with a minimum allele frequency cutoff.
     */
    @Test
    public void testApplyMinAF() {
        System.out.println("applyMinAF");
        MarkAmbiguous instance = new MarkAmbiguous(info);
        
        Genotype genotype = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A")))
                .AD(new int[]{3,7})
                .make();
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("A")))
                .genotypes(genotype)
                .make();
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("N")))
                .genotypes(new GenotypeBuilder("chr1", Arrays.asList(Allele.create("N"))).make())
                .make();
        
        VariantContext result = instance.apply(vc);
        assertVariantContextEquals(expResult, result);
    }
    
    @Test
    public void testApplyMinAF2() {
        System.out.println("applyMinAF2");
        MarkAmbiguous instance = new MarkAmbiguous(info);
        
        Genotype genotype = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A")))
                .AD(new int[]{2,8})
                .make();
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("A")))
                .genotypes(genotype)
                .make();
        VariantContext expResult = vc;
        
        VariantContext result = instance.apply(vc);
        assertVariantContextEquals(expResult, result);
    }
    
    /**
     * Test of apply method, of class CallAmbiguous with a minimum quality cutoff.
     */
    @Test
    public void testApplyMinQual() {
        System.out.println("applyMinQual");
        MarkAmbiguous instance = new MarkAmbiguous(info);
        VariantContext expResult = createSNP("C", "N", Collections.EMPTY_MAP, -1);
        VariantContext result = instance.apply(createSNP("C", "G", Collections.EMPTY_MAP, -1));
        assertVariantContextEquals(expResult, result);
        
        expResult = createSNP("C", "G", Collections.EMPTY_MAP, -1.7);
        result = instance.apply(createSNP("C", "G", Collections.EMPTY_MAP, -1.7));
        assertVariantContextEquals(expResult, result);
        
    }
    
    /**
     * Test of apply method, of class CallAmbiguous with a 'N' base call.
     */
    @Test
    public void testApplyNCall() {
        System.out.println("applyMaxNumAlt");
        MarkAmbiguous instance = new MarkAmbiguous(info);
        VariantContext expResult = createSNP("C", "N");
        VariantContext result = instance.apply(createSNP("C", "N"));
        assertVariantContextEquals(expResult, result);
        
    }

    /**
     * Test of makeAmbiguous method, of class CallAmbiguous.
     */
    @Test
    public void testMakeAmbiguous() {
        System.out.println("makeAmbiguous");
        
        VariantContext vc = createSNP("C", "G");
        VariantContext expResult = createSNP("C", "N");
        MarkAmbiguous instance = new MarkAmbiguous(null);
        
        VariantContext result = instance.makeAmbiguous(vc);
        assertVariantContextEquals(expResult, result);
        
    }
    
//    @Test
//    public void testIsBelowMinAF() {
//        System.out.println("isBelowMinAF");
//        MarkAmbiguous instance = new MarkAmbiguous(info);
//        Map<String, Object> attr = new HashMap<String,Object>();
//        attr.put("AF", 0.5);
//        VariantContext vc = createSNP("C", "N", attr);
//        assertTrue(instance.isBelowMinAF(vc));
//        
//        attr.put("AF", Arrays.asList("1.0", "1.0"));
//        vc = createSNP("C", "N", attr);
//        assertFalse(instance.isBelowMinAF(vc));
//        
//        attr.put("AF", Arrays.asList("1.0", "0.5"));
//        vc = createSNP("C", "N", attr);
//        assertTrue(instance.isBelowMinAF(vc));
//        
//        attr.clear();
//        vc = createSNP("C", "N", attr);
//        assertFalse(instance.isBelowMinAF(vc));
//    }
    
    
}
