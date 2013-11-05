/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import java.util.HashMap;
import java.util.Collection;
import java.util.Map;
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
public class PatternBuilderTest {
    
    public PatternBuilderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of build method, of class PatternBuilder.
     */
    @Test
    public void testBuild() {
        System.out.println("build");
        PatternBuilder pb = new PatternBuilder();
        pb.addAllele("S1", Allele.NO_CALL);
        pb.addAllele("S2", Allele.create("A"));
        pb.addAllele("S3", Allele.create("A"));
        pb.addAllele("S4", Allele.create("T"));
        pb.addAllele("S5", Allele.create("A"));
        pb.addAllele("S6", Allele.create("N"));
        
        Map<String, String> expResult = new HashMap<String, String>();
        expResult.put("S1", ".");
        expResult.put("S2", "1");
        expResult.put("S3", "1");
        expResult.put("S4", "2");
        expResult.put("S5", "1");
        expResult.put("S6", "N");
        
        Map result = pb.build();
        assertEquals(expResult, result);
    }

}
