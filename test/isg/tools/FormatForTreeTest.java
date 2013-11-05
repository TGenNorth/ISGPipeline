/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import java.util.Arrays;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import java.util.Collections;
import isg.tools.FormatForTree.Converter;
import isg.tools.FormatForTree.FastaConverter;
import isg.matrix.VariantContextTabReader;
import java.io.File;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class FormatForTreeTest {
    
    public FormatForTreeTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of main method, of class FormatForTree.
     */
    @Test
    public void testConverter() {
        System.out.println("converter");
        Converter converter = new FastaConverter(true, true);
        
        VariantContext vc = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("T"))).make(),
                           new GenotypeBuilder("S2", Arrays.asList(Allele.create("A", true))).make())
                .make();
        converter.append(vc);
        vc = new VariantContextBuilder("", "chr1", 10, 10, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S1", Arrays.asList(Allele.create("A", true))).make(),
                           new GenotypeBuilder("S2", Arrays.asList(Allele.create("A", true))).make())
                .make();
        converter.append(vc);
        
        String result = converter.getSequence("S1");
        assertEquals("TA", result);
        
        result = converter.getSequence("S2");
        assertEquals("AA", result);
    }

}
