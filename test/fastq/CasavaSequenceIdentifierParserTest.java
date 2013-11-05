/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class CasavaSequenceIdentifierParserTest {
    
    public CasavaSequenceIdentifierParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of canParse method, of class CasavaSequenceIdentifierParser.
     */
    @Test
    public void testCanParse() {
        System.out.println("canParse");
        String str = "@EAS139:136:FC706VJ:2:2104:15343:197393 1:Y:18:ATCACG";
        CasavaSequenceIdentifierParser instance = new CasavaSequenceIdentifierParser();
        boolean expResult = true;
        boolean result = instance.canParse(str);
        assertEquals(expResult, result);
        assertFalse(instance.canParse("@HWUSI-EAS100R:6:73:941:1973#0/1"));
    }

    /**
     * Test of parse method, of class CasavaSequenceIdentifierParser.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        String str = "@EAS139:136:FC706VJ:2:2104:15343:197393 1:Y:18:ATCACG";
        CasavaSequenceIdentifierParser instance = new CasavaSequenceIdentifierParser();
        SequenceIdentifier expResult = 
                new CasavaSequenceIdentifier("EAS139","136","FC706VJ",2,2104,15343,197393,1,"Y",18,"ATCACG");
        SequenceIdentifier result = instance.parse(str);
        assertEquals(expResult, result);
    }
}
