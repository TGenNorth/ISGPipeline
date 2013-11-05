/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.snps;

import isg.VariantContextTestUtils;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import net.sf.samtools.SAMSequenceRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.picard.reference.ReferenceSequence;
import java.util.List;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class MumSNPToVariantContextTest {
    
    public MumSNPToVariantContextTest() {
    }

    /**
     * Test of apply method, of class MumSNPToVariantContext.
     */
    @Test
    public void testApply() {
        System.out.println("apply");
        
        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("A1", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("A2", 1, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        MumSNPToVariantContext instance = new MumSNPToVariantContext(dict, "abc");
        MumSNPFeature snp = new MumSNPFeature(1,"A","T",10,0,0,0,0,0,0,"","",1,1,"A1","B1");
        
        VariantContext expResult = new VariantContextBuilder("", "A1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("abc", Arrays.asList(Allele.create("T"))).make())
                .make();
        VariantContext result = instance.apply(snp);
        VariantContextTestUtils.assertVariantContextEquals(result, expResult);
    }
    
    @Test
    public void testApply2() {
        System.out.println("apply2");
        
        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("B1", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("B2", 1, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        MumSNPToVariantContext instance = new MumSNPToVariantContext(dict, "abc");
        MumSNPFeature snp = new MumSNPFeature(1,"A","T",10,0,0,0,0,0,0,"","",1,1,"A1","B1");
        
        VariantContext expResult = new VariantContextBuilder("", "B1", 10, 10, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("T", true), Allele.create("A")))
                .genotypes(new GenotypeBuilder("abc", Arrays.asList(Allele.create("A"))).make())
                .make();
        VariantContext result = instance.apply(snp);
        VariantContextTestUtils.assertVariantContextEquals(result, expResult);
    }

    /**
     * Test of determineReference method, of class MumSNPToVariantContext.
     */
    @Test
    public void testDetermineReference() {
        System.out.println("determineReference");
        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("chr1", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("chr2", 1, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);

        
        MumSNPToVariantContext instance = new MumSNPToVariantContext(dict, "");

        MumSNPFeature snp = new MumSNPFeature(0,"","",0,0,0,0,0,0,0,"","",1,1,"chr1","");
        int expResult = 0;
        int result = instance.determineReference(snp);
        assertEquals(expResult, result);
        
        snp = new MumSNPFeature(0,"","",0,0,0,0,0,0,0,"","",1,1,"","chr1");
        expResult = 1;
        result = instance.determineReference(snp);
        assertEquals(expResult, result);
    }
    
    private SAMSequenceDictionary createDict(List<ReferenceSequence> refSeqList) {
        final List<SAMSequenceRecord> r = new ArrayList<SAMSequenceRecord>();
        for (ReferenceSequence refSeq : refSeqList) {
            r.add(new SAMSequenceRecord(refSeq.getName()));
        }
        return new SAMSequenceDictionary(r);
    }
}
