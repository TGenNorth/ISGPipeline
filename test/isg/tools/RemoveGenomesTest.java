/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabHeader;
import isg.matrix.HeaderAttribute;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import isg.VariantContextTestUtils;
import isg.matrix.HeaderSampleAttribute;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import net.sf.picard.util.CollectionUtil;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class RemoveGenomesTest {
    
    private static final File dataDir = new File("test"+File.separatorChar+"data");
    private static final File outFile = new File(dataDir, "remove_genomes.out");
    
    public RemoveGenomesTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        outFile.delete();
    }
    
    @Test
    public void testDoWork() throws FileNotFoundException, IOException {
        System.out.println("doWork");
        RemoveGenomes instance = new RemoveGenomes();
        instance.INPUT = new File(dataDir, "matrix_with_attributes.txt");
        instance.OUTPUT = outFile;
        instance.SAMPLE_NAME = Arrays.asList("S1");
        int result = instance.doWork();
        int expResult = 0;
        assertEquals(expResult, result);
        
        VariantContextTabReader reader = new VariantContextTabReader(outFile);
        VariantContext next = reader.nextRecord();
        VariantContext exp = new VariantContextBuilder(".", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S2", Arrays.asList(Allele.create("T")))
                               .attribute("attr", "S2")
                               .make(),
                           new GenotypeBuilder("S3", Arrays.asList(Allele.create("T")))
                               .attribute("attr", "S3")
                               .make())
                .make();
        VariantContextTestUtils.assertVariantContextEquals(exp, next);
        
        next = reader.nextRecord();
        exp = new VariantContextBuilder(".", "chr2", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("G", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("S2", Arrays.asList(Allele.create("T")))
                               .attribute("attr", "S2")
                               .make(),
                           new GenotypeBuilder("S3", Arrays.asList(Allele.create("G", true)))
                               .attribute("attr", "S3")
                               .make())
                .make();
        VariantContextTestUtils.assertVariantContextEquals(exp, next);
        
        assertNull(reader.nextRecord());
    }

    /**
     * Test of removeGenotypesFromVariantContext method, of class RemoveGenomes.
     */
    @Test
    public void testRemoveGenotypesFromVariantContext() {
        System.out.println("removeGenotypesFromVariantContext");
        Set<String> genotypesToRemove = CollectionUtil.makeSet("s1");
        VariantContext vc = new VariantContextBuilder(".", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(GenotypeBuilder.create("s1", Arrays.asList(Allele.create("T"))),
                           GenotypeBuilder.create("s2", Arrays.asList(Allele.create("A", true))))
                .make();
        VariantContext expResult = new VariantContextBuilder(".", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true)))
                .genotypes(GenotypeBuilder.create("s2", Arrays.asList(Allele.create("A", true))))
                .make();
        VariantContext result = RemoveGenomes.removeGenotypesFromVariantContext(genotypesToRemove, vc);
        VariantContextTestUtils.assertVariantContextEquals(expResult, result);
    }

    /**
     * Test of isSNP method, of class RemoveGenomes.
     */
    @Test
    public void testIsSNP() {
        System.out.println("isSNP");
        VariantContext vc = new VariantContextBuilder(".", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .make();
        boolean expResult = true;
        boolean result = RemoveGenomes.isSNP(vc);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testIsNotSNP() {
        System.out.println("isNotSNP");
        VariantContext vc = new VariantContextBuilder(".", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("N")))
                .make();
        boolean expResult = false;
        boolean result = RemoveGenomes.isSNP(vc);
        assertEquals(expResult, result);
    }

}
