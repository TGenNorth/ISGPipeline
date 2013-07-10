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
public class IlluminaSequenceIdentifierParserTest {
    
    public IlluminaSequenceIdentifierParserTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of canParse method, of class IlluminaSequenceIdentifierParser.
     */
    @Test
    public void testCanParse() {
        System.out.println("canParse");
        IlluminaSequenceIdentifierParser instance = new IlluminaSequenceIdentifierParser();
        assertTrue(instance.canParse("@HWUSI-EAS100R:6:73:941:1973#0/1"));
        assertTrue(instance.canParse("@HWUSI-EAS100R:6:73:941:1973#NNNNNN/1"));
        assertTrue(instance.canParse("HWUSI-EAS100R:6:73:941:1973#NNNNNN/1"));
        assertFalse(instance.canParse("@EAS139:136:FC706VJ:2:2104:15343:197393 1:Y:18:ATCACG"));
    }

    /**
     * Test of parse method, of class IlluminaSequenceIdentifierParser.
     */
    @Test
    public void testParse() {
        System.out.println("parse");
        String str = "@HWUSI-EAS100R:6:73:941:1973#0/1";
        IlluminaSequenceIdentifierParser instance = new IlluminaSequenceIdentifierParser();
        SequenceIdentifier expResult = 
                new IlluminaSequenceIdentifier("HWUSI-EAS100R",6,73,941,1973,"0",1);
        SequenceIdentifier result = instance.parse(str);
        assertEquals(expResult, result);
    }
}
