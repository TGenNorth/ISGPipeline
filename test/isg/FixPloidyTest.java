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
        FixPloidy instance = new FixPloidy(.75);
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
        FixPloidy instance = new FixPloidy(.75);
        Genotype genotypeToFix = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("G")))
                .AD(new int[]{0,8,2})
                .make();
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("A"), Allele.create("G")))
                .genotypes(genotypeToFix)
                .make();
        Genotype expResult = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"))).make();
        Genotype result = instance.fixPloidy(genotypeToFix, vc);
        assertTrue(expResult.sameGenotype(result));
        
        genotypeToFix = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("G")))
                .AD(new int[]{0,2,8})
                .make();
        expResult = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("G"))).make();
        result = instance.fixPloidy(genotypeToFix, vc);
        assertTrue(expResult.sameGenotype(result));
        
        genotypeToFix = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("G"), Allele.create("T", true)))
                .AD(new int[]{8,1,1})
                .make();
        expResult = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("T", true))).make();
        result = instance.fixPloidy(genotypeToFix, vc);
        assertTrue(expResult.sameGenotype(result));
    }
    
    @Test
    public void testFixPloidyThowException() {
        System.out.println("fixPloidyThowException");
        Genotype genotypeToFix = new GenotypeBuilder("chr1", Arrays.asList(Allele.create("A"), Allele.create("T", true))).make();
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A"), Allele.create("T", true)))
                .genotypes(genotypeToFix)
                .make();
        FixPloidy instance = new FixPloidy(.75);
        exception.expect(IllegalStateException.class);
        instance.fixPloidy(genotypeToFix, vc);
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
        FixPloidy instance = new FixPloidy(.75);
        Set<Allele> expResult = new HashSet<Allele>();
        Collections.addAll(expResult, Allele.create("A"), Allele.create("T"));
        Set<Allele> result = instance.getUniqueCallableAlleles(genotypes);
        assertEquals(expResult, result);
    }
}
