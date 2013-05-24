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
public class BWASampeCommandLineFunctionTest {
    
    public BWASampeCommandLineFunctionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of commandLine method, of class BWASampeCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        BWASampeCommandLineFunction instance = new BWASampeCommandLineFunction();
        instance.fq1 = new File("sample_1.fastq");
        instance.fq2 = new File("sample_2.fastq");
        instance.prefix = "ref";
        instance.sai1 = new File("sample_1.sai");
        instance.sai2 = new File("sample_2.sai");
        instance.sam = new File("sample.sam");
        String expResult = " 'bwa'  'sampe'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
    }
}
