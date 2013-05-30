/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jbeckstrom
 */
public class GenotypeNoCallsTest {
    
    public GenotypeNoCallsTest() {
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
     * Test of apply method, of class GenotypeNoCalls where one sample is not called.
     */
    @Test
    public void testApplySingleNoCall() {
        System.out.println("ApplySingleNoCall");
        
        List<SingleSampleGenotyper> genotypers = Arrays.asList(
                createSingleSampleGenotyper("S1", CalledState.CALLABLE),
                createSingleSampleGenotyper("S2", CalledState.CALLABLE));
        
        VariantContext vc = VariantContextTestUtils.createSNP(1, "chr1", "S1", "A", "T");
        GenotypeNoCalls instance = new GenotypeNoCalls(genotypers);
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"))).make(),
                           new GenotypeBuilder("S2", Arrays.asList(Allele.create("A", true))).make())
                .make();
        VariantContext result = instance.apply(vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }
    
    /**
     * Test of apply method, of class GenotypeNoCalls where one sample is not called.
     */
    @Test
    public void testApplyMultipleNoCall() {
        System.out.println("ApplyMultipleNoCall");
        
        List<SingleSampleGenotyper> genotypers = Arrays.asList(
                createSingleSampleGenotyper("S1", CalledState.CALLABLE),
                createSingleSampleGenotyper("S2", CalledState.CALLABLE),
                createSingleSampleGenotyper("S3", CalledState.NO_COVERAGE));
        
        VariantContext vc = VariantContextTestUtils.createSNP(1, "chr1", "S1", "A", "T");
        GenotypeNoCalls instance = new GenotypeNoCalls(genotypers);
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"))).make(),
                           new GenotypeBuilder("S2", Arrays.asList(Allele.create("A", true))).make(),
                           new GenotypeBuilder("S3", Arrays.asList(Allele.NO_CALL)).make())
                .make();
        VariantContext result = instance.apply(vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }
    
    /**
     * Test of apply method, of class GenotypeNoCalls where one sample is not called.
     */
    @Test
    public void testApplyNoCallToMultipleCalled() {
        System.out.println("ApplyNoCallToMultipleCalled");
        
        List<SingleSampleGenotyper> genotypers = Arrays.asList(
                createSingleSampleGenotyper("S1", CalledState.CALLABLE),
                createSingleSampleGenotyper("S2", CalledState.CALLABLE),
                createSingleSampleGenotyper("S3", CalledState.CALLABLE));
        
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"))).make(),
                           new GenotypeBuilder("S3", Arrays.asList(Allele.create("T"))).make())
                .make();
        
        GenotypeNoCalls instance = new GenotypeNoCalls(genotypers);
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"))).make(),
                           new GenotypeBuilder("S2", Arrays.asList(Allele.create("A", true))).make(),
                           new GenotypeBuilder("S3", Arrays.asList(Allele.create("T"))).make())
                .make();
        VariantContext result = instance.apply(vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }
    
    private SingleSampleGenotyper createSingleSampleGenotyper(final String sample, final CalledState calledState){
        return new SingleSampleGenotyperImpl(sample, createLociStateCaller(calledState));
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
