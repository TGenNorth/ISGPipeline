/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class BWASamseCommandLineFunctionTest {
    
    public BWASamseCommandLineFunctionTest() {
    }

    /**
     * Test of commandLine method, of class BWASamseCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        BWASamseCommandLineFunction instance = new BWASamseCommandLineFunction();
        instance.fq = new File("sample.fastq");
        instance.prefix = "ref";
        instance.sai = new File("sample.sai");
        instance.sam = new File("sample.sam");
        String expResult = " 'bwa'  'samse'  '-f'  'sample.sam'  'ref'"
                + "  'sample.sai'  'sample.fastq' ";
        String result = instance.commandLine();
        assertEquals(expResult, result);
    }
}
