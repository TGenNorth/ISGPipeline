/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import java.util.Iterator;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.samtools.SAMSequenceRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.sf.samtools.SAMSequenceDictionary;
import java.util.List;
import java.util.Map;
import org.broadinstitute.variant.variantcontext.Allele;
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
public class MergingVariantContextIteratorTest {

    public MergingVariantContextIteratorTest() {
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
     * Test of mapping method, of class MergingVariantContextIterator.
     */
    @Test
    public void testMapping() {
//        System.out.println("mapping");
//        List<K> keys = null;
//        List<V> values = null;
//        Map expResult = null;
//        Map result = MergingVariantContextIterator.mapping(keys, values);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }
    
    /**
     * Test of merging a single chromosome and two samples
     */
    @Test
    public void testSingleChrMultipleSampleMerge() {
        System.out.println("singleChrMultipleSampleMerge");

        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("chr1", 0, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        List<Iterator<VariantContext>> vcIters = new ArrayList<Iterator<VariantContext>>();
        List<VariantContext> s1 = new ArrayList<VariantContext>();
        s1.add(VariantContextTestUtils.createSNP(1, "chr1", "s1", "A", "T"));
        s1.add(VariantContextTestUtils.createSNP(10, "chr1", "s1", "C", "G"));
        vcIters.add(s1.iterator());
        
        List<VariantContext> s2 = new ArrayList<VariantContext>();
        s2.add(VariantContextTestUtils.createSNP(1, "chr1", "s2", "A", "C"));
        s2.add(VariantContextTestUtils.createSNP(10, "chr1", "s2", "C", "G"));
        vcIters.add(s2.iterator());
        
        List<VariantContext> s3 = new ArrayList<VariantContext>();
        s3.add(VariantContextTestUtils.createSNP(1, "chr1", "s3", "A", "C"));
        s3.add(VariantContextTestUtils.createSNP(20, "chr1", "s3", "G", "A"));
        vcIters.add(s3.iterator());
        
        MergingVariantContextIterator instance = new MergingVariantContextIterator(vcIters, dict);
        
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T"), Allele.create("C")))
                .genotypes(Arrays.asList(createGenotype("s1", "T"), createGenotype("s2", "C"), createGenotype("s3", "C")))
                .make();
        VariantContext result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr1", 10, 10, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("C", true), Allele.create("G")))
                .genotypes(Arrays.asList(createGenotype("s1", "G"), createGenotype("s2", "G")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
//        
        expResult = new VariantContextBuilder("", "chr1", 20, 20, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("G", true), Allele.create("A")))
                .genotypes(Arrays.asList(createGenotype("s3", "A")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);

        assertFalse(instance.hasNext());
    }

    /**
     * Test of merging a single chromosome and two samples
     */
    @Test
    public void testSingleChrTwoSampleMerge() {
        System.out.println("singleChrTwoSampleMerge");

        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("chr1", 0, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        List<Iterator<VariantContext>> vcIters = new ArrayList<Iterator<VariantContext>>();
        List<VariantContext> s1 = new ArrayList<VariantContext>();
        s1.add(VariantContextTestUtils.createSNP(1, "chr1", "s1", "A", "T"));
        s1.add(VariantContextTestUtils.createSNP(10, "chr1", "s1", "C", "G"));
        s1.add(VariantContextTestUtils.createSNP(20, "chr1", "s1", "G", "A"));
        vcIters.add(s1.iterator());
        
        List<VariantContext> s2 = new ArrayList<VariantContext>();
        s2.add(VariantContextTestUtils.createSNP(1, "chr1", "s2", "A", "C"));
        s2.add(VariantContextTestUtils.createSNP(20, "chr1", "s2", "G", "A"));
        s2.add(VariantContextTestUtils.createSNP(30, "chr1", "s2", "T", "A"));
        vcIters.add(s2.iterator());
        
        MergingVariantContextIterator instance = new MergingVariantContextIterator(vcIters, dict);
        
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T"), Allele.create("C")))
                .genotypes(Arrays.asList(createGenotype("s1", "T"), createGenotype("s2", "C")))
                .make();
        VariantContext result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr1", 10, 10, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("C", true), Allele.create("G")))
                .genotypes(Arrays.asList(createGenotype("s1", "G")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr1", 20, 20, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("G", true), Allele.create("A")))
                .genotypes(Arrays.asList(createGenotype("s1", "A"), createGenotype("s2", "A")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr1", 30, 30, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("A")))
                .genotypes(Arrays.asList(createGenotype("s2", "A")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        assertFalse(instance.hasNext());
    }
    
    /**
     * Test of merging multiple chromosomes and two samples
     */
    @Test
    public void testMultipleChrTwoSampleMerge() {
        System.out.println("multipleChrTwoSampleMerge");

        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("chr0", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("chr1", 1, "".getBytes()));
        refSeqList.add(new ReferenceSequence("chr2", 2, "".getBytes()));
        refSeqList.add(new ReferenceSequence("chr3", 3, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        List<Iterator<VariantContext>> vcIters = new ArrayList<Iterator<VariantContext>>();
        List<VariantContext> s1 = new ArrayList<VariantContext>();
        s1.add(VariantContextTestUtils.createSNP(1, "chr1", "s1", "A", "T"));
        s1.add(VariantContextTestUtils.createSNP(10, "chr2", "s1", "C", "G"));
        s1.add(VariantContextTestUtils.createSNP(20, "chr3", "s1", "T", "A"));
        vcIters.add(s1.iterator());
        
        List<VariantContext> s2 = new ArrayList<VariantContext>();
        s2.add(VariantContextTestUtils.createSNP(1, "chr1", "s2", "A", "C"));
        s2.add(VariantContextTestUtils.createSNP(20, "chr1", "s2", "G", "A"));
        s2.add(VariantContextTestUtils.createSNP(20, "chr3", "s2", "T", "A"));
        vcIters.add(s2.iterator());
        
        MergingVariantContextIterator instance = new MergingVariantContextIterator(vcIters, dict);
        
        VariantContext expResult = new VariantContextBuilder("", "chr1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T"), Allele.create("C")))
                .genotypes(Arrays.asList(createGenotype("s1", "T"), createGenotype("s2", "C")))
                .make();
        VariantContext result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr1", 20, 20, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("G", true), Allele.create("A")))
                .genotypes(Arrays.asList(createGenotype("s2", "A")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr2", 10, 10, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("C", true), Allele.create("G")))
                .genotypes(Arrays.asList(createGenotype("s1", "G")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        expResult = new VariantContextBuilder("", "chr3", 20, 20, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("A")))
                .genotypes(Arrays.asList(createGenotype("s1", "A"), createGenotype("s2", "A")))
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(expResult, result, true);
        
        assertFalse(instance.hasNext());
    }
    
    private Genotype createGenotype(final String sample, final String baseStr){
        return new GenotypeBuilder(sample, Arrays.asList(Allele.create(baseStr))).make();
    }

    private SAMSequenceDictionary createDict(List<ReferenceSequence> refSeqList) {
        final List<SAMSequenceRecord> r = new ArrayList<SAMSequenceRecord>();
        for (ReferenceSequence refSeq : refSeqList) {
            r.add(new SAMSequenceRecord(refSeq.getName()));
        }
        return new SAMSequenceDictionary(r);
    }
}
