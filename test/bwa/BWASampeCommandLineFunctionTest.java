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
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        String expResult = " 'bwa'  'sampe'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_a() {
        System.out.println("testCommandLineWith_a");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.a = 1;
        String expResult = " 'bwa'  'sampe' '-a' '1'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_o() {
        System.out.println("testCommandLineWith_o");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.o = 1;
        String expResult = " 'bwa'  'sampe' '-o' '1'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_n() {
        System.out.println("testCommandLineWith_n");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.n = 1;
        String expResult = " 'bwa'  'sampe' '-n' '1'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_N() {
        System.out.println("testCommandLineWith_N");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.N = 1;
        String expResult = " 'bwa'  'sampe' '-N' '1'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_c() {
        System.out.println("testCommandLineWith_c");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.c = 0.1F;
        String expResult = " 'bwa'  'sampe' '-c' '0.1'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_P() {
        System.out.println("testCommandLineWith_P");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.P = true;
        String expResult = " 'bwa'  'sampe' '-P'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_s() {
        System.out.println("testCommandLineWith_s");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.s = true;
        String expResult = " 'bwa'  'sampe' '-s'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_A() {
        System.out.println("testCommandLineWith_A");
        BWASampeCommandLineFunction instance = createRequiredSampeCommand();
        instance.A = true;
        String expResult = " 'bwa'  'sampe' '-A'  '-f'  'sample.sam'  'ref'"
                + "  'sample_1.sai'  'sample_2.sai'  'sample_1.fastq'"
                + "  'sample_2.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    
    private BWASampeCommandLineFunction createRequiredSampeCommand(){
        BWASampeCommandLineFunction instance = new BWASampeCommandLineFunction();
        instance.fq1 = new File("sample_1.fastq");
        instance.fq2 = new File("sample_2.fastq");
        instance.prefix = "ref";
        instance.sai1 = new File("sample_1.sai");
        instance.sai2 = new File("sample_2.sai");
        instance.sam = new File("sample.sam");
        return instance;
    }
}
