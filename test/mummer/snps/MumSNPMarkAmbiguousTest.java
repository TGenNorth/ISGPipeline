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
public class MumSNPMarkAmbiguousTest {

    public MumSNPMarkAmbiguousTest() {
    }
    
    @Test
    public void testMakeAmbiguous() {
        System.out.println("makeAmbiguous");
        
        VariantContext vc = VariantContextTestUtils.createSNP(1, "A1", "abc", "N", "T");
        
        VariantContext result = MumSNPMarkAmbiguous.makeAmbiguous(vc);
        VariantContext expResult = new VariantContextBuilder("", "A1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("N", true)))
                .genotypes(new GenotypeBuilder("abc", Arrays.asList(Allele.create("N", true))).make())
                .make();
        VariantContextTestUtils.assertVariantContextEquals(result, expResult);

    }

    @Test
    public void testMergeFull() {
        System.out.println("mergeFull");
        
        List<ReferenceSequence> refSeqList = new ArrayList<ReferenceSequence>();
        refSeqList.add(new ReferenceSequence("A1", 0, "".getBytes()));
        refSeqList.add(new ReferenceSequence("A2", 1, "".getBytes()));
        final SAMSequenceDictionary dict = createDict(refSeqList);
        
        List<VariantContext> lhs = new ArrayList<VariantContext>();
        lhs.add(VariantContextTestUtils.createSNP(1, "A1", "abc", "A", "T"));
        lhs.add(VariantContextTestUtils.createSNP(10, "A1", "abc", "G", "C"));
        
        List<VariantContext> rhs = new ArrayList<VariantContext>();
        rhs.add(VariantContextTestUtils.createSNP(1, "A1", "abc", "A", "T"));
        
        MumSNPMarkAmbiguous instance = new MumSNPMarkAmbiguous(Arrays.asList(rhs.iterator(), lhs.iterator()), dict);
        VariantContext expResult = new VariantContextBuilder("", "A1", 1, 1, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("A", true), Allele.create("T")))
                .genotypes(new GenotypeBuilder("abc", Arrays.asList(Allele.create("T"))).make())
                .make();
        VariantContext result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(result, expResult);
        
        expResult = new VariantContextBuilder("", "A1", 10, 10, Collections.EMPTY_LIST)
                .alleles(Arrays.asList(Allele.create("G", true), Allele.create("N")))
                .genotypes(new GenotypeBuilder("abc", Arrays.asList(Allele.create("N"))).make())
                .make();
        result = instance.next();
        VariantContextTestUtils.assertVariantContextEquals(result, expResult);
        
        assertFalse(instance.hasNext());
    }
    
    private SAMSequenceDictionary createDict(List<ReferenceSequence> refSeqList) {
        final List<SAMSequenceRecord> r = new ArrayList<SAMSequenceRecord>();
        for (ReferenceSequence refSeq : refSeqList) {
            r.add(new SAMSequenceRecord(refSeq.getName()));
        }
        return new SAMSequenceDictionary(r);
    }
}
