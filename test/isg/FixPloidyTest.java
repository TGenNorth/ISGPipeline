/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import java.util.HashSet;
import org.junit.rules.ExpectedException;
import org.junit.Rule;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class FixPloidyTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    public FixPloidyTest() {
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
     * Test of apply method, of class FixPloidy.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"), Allele.create("T"))).make())
                .make();
        FixPloidy instance = new FixPloidy(1);
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"))).make())
                .make();
        VariantContext result = instance.apply(vc);
        VariantContextTestUtils.assertVariantContextEquals(result, expResult);
    }

    /**
     * Test of fixPloidy method, of class FixPloidy.
     */
    @Test
    public void testFixPloidy() {
        System.out.println("fixPloidy");
        Genotype genotypeToFix = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("A"))).make();
        FixPloidy instance = new FixPloidy(1);
        Genotype expResult = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"))).make();
        Genotype result = instance.fixPloidy(genotypeToFix);
        assertTrue(expResult.sameGenotype(result));
    }
    
    @Test
    public void testFixPloidyThowException() {
        System.out.println("fixPloidyThowException");
        Genotype genotypeToFix = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("T"))).make();
        FixPloidy instance = new FixPloidy(1);
        exception.expect(IllegalStateException.class);
        instance.fixPloidy(genotypeToFix);
    }

    /**
     * Test of getUniqueCallableAlleles method, of class FixPloidy.
     */
    @Test
    public void testGetUniqueCallableAlleles() {
        System.out.println("getUniqueCallableAlleles");
        List<Genotype> genotypes = Arrays.asList(
                new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("A"))).make(),
                new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("T"))).make()
        );
        FixPloidy instance = new FixPloidy(1);
        Set<Allele> expResult = new HashSet<Allele>();
        Collections.addAll(expResult, Allele.create("A"), Allele.create("T"));
        Set<Allele> result = instance.getUniqueCallableAlleles(genotypes);
        assertEquals(expResult, result);
    }
}
