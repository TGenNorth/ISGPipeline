/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Arrays;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextUtilsTest {
    
    public VariantContextUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of countUniqueAlleles method, of class VariantContextUtils.
     */
    @Test
    public void testCountUniqueAlleles() {
        System.out.println("countUniqueAlleles");
        Genotype g = GenotypeBuilder.create("abc", Arrays.asList(Allele.create("A"), Allele.create("T"), Allele.create("A")));
        int expResult = 2;
        int result = VariantContextUtils.countUniqueAlleles(g);
        assertEquals(expResult, result);
        
        g = GenotypeBuilder.create("abc", Arrays.asList(Allele.create("A"), Allele.create("A", true), Allele.create("T")));
        expResult = 3;
        result = VariantContextUtils.countUniqueAlleles(g);
        assertEquals(expResult, result);
    }
}
