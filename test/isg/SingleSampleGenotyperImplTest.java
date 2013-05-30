/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import java.util.Arrays;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
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
public class SingleSampleGenotyperImplTest {
    
    public SingleSampleGenotyperImplTest() {
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
     * Test of sample method, of class SingleSampleGenotyperImpl.
     */
    @Test
    public void testSample() {
        System.out.println("sample");
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", createLociStateCaller(CalledState.CALLABLE));
        String expResult = "s1";
        String result = instance.sample();
        assertEquals(expResult, result);
    }

    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl locus is in an
     * area where ref is 'N'.
     */
    @Test
    public void testGenotypeRefN() {
        System.out.println("GenotypeRefN");
        Allele ref = Allele.create("N", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", createLociStateCaller(CalledState.REF_N));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(ref)).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl where locus in not covered.
     */
    @Test
    public void testGenotypeNoCoverage() {
        System.out.println("GenotypeNoCoverage");
        Allele ref = Allele.create("A", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", createLociStateCaller(CalledState.NO_COVERAGE));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(Allele.NO_CALL)).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl where locus is callable.
     */
    @Test
    public void testGenotypeCallable() {
        System.out.println("GenotypeCallable");
        Allele ref = Allele.create("A", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", 
                createLociStateCaller(CalledState.CALLABLE));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(ref)).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl where locus is callable.
     */
    @Test
    public void testGenotypePass() {
        System.out.println("GenotypePass");
        Allele ref = Allele.create("A", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", 
                createLociStateCaller(CalledState.PASS));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(ref)).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl where locus has low coverage.
     */
    @Test
    public void testGenotypeLowCoverage() {
        System.out.println("GenotypeLowCoverage");
        Allele ref = Allele.create("A", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", 
                createLociStateCaller(CalledState.LOW_COVERAGE));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(Allele.create("N"))).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl where locus has excessive coverage.
     */
    @Test
    public void testGenotypeExcessiveCoverage() {
        System.out.println("GenotypeExcessiveCoverage");
        Allele ref = Allele.create("A", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", 
                createLociStateCaller(CalledState.EXCESSIVE_COVERAGE));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(Allele.create("N"))).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /**
     * Test of genotype method, of class SingleSampleGenotyperImpl where locus has poor mapping quality.
     */
    @Test
    public void testGenotypePoorMappingQuality() {
        System.out.println("GenotypePoorMappingQuality");
        Allele ref = Allele.create("A", true);
        String chr = "s1";
        int pos = 0;
        SingleSampleGenotyperImpl instance = new SingleSampleGenotyperImpl("s1", 
                createLociStateCaller(CalledState.POOR_MAPPING_QUALITY));
        Genotype expResult = new GenotypeBuilder("s1", Arrays.asList(Allele.create("N"))).make();
        Genotype result = instance.genotype(ref, chr, pos);
        assertTrue(expResult.sameGenotype(result));
    }
    
    /*
     * create a LociStateCaller that always calls the provided CalledState
     */
    private LociStateCaller createLociStateCaller(final CalledState calledState){
        return new LociStateCaller(){

            @Override
            public CalledState call(String chr, int pos) {
                return calledState;
            }
            
        };
    }
}
