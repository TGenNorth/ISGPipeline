/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.VariantContextTestUtils;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import java.util.List;
import java.util.ArrayList;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import org.broadinstitute.variant.variantcontext.Allele;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class CalculateMismatchTest {
    
    public CalculateMismatchTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of doWork method, of class CalculateMismatch.
     */
    @Test
    public void testCalculateMismatchIterator() {
        System.out.println("doWork");
        List<Allele> alleles = Arrays.asList(Allele.create("A", true));
        List<VariantContext> vcList = new ArrayList<VariantContext>();
        vcList.add(new VariantContextBuilder(".", "chr1", 1, 1, alleles).make());
        vcList.add(new VariantContextBuilder(".", "chr2", 1, 1, alleles).make());
        vcList.add(new VariantContextBuilder(".", "chr2", 3, 5, Arrays.asList(Allele.create("AAA", true))).make());
        vcList.add(new VariantContextBuilder(".", "chr2", 10, 10, alleles).make());
        
        CalculateMismatch.CalculateMismatchIterator instance = new CalculateMismatch.CalculateMismatchIterator(vcList.iterator());
        
        VariantContext expResult = new VariantContextBuilder(".", "chr1", 1, 1, alleles).attribute("Mismatch", -1).make();
        VariantContext result = instance.computeNext();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
        
        expResult = new VariantContextBuilder(".", "chr2", 1, 1, alleles).attribute("Mismatch", 2).make();
        result = instance.computeNext();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
        
        expResult = new VariantContextBuilder(".", "chr2", 3, 5, Arrays.asList(Allele.create("AAA", true))).attribute("Mismatch", 2).make();
        result = instance.computeNext();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
        
        expResult = new VariantContextBuilder(".", "chr2", 10, 10, alleles).attribute("Mismatch", 5).make();
        result = instance.computeNext();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }

}
