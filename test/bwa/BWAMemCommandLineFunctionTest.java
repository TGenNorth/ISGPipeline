/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import net.sf.picard.util.FastqQualityFormat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class BWAMemCommandLineFunctionTest {
    
    public BWAMemCommandLineFunctionTest() {
    }

    @Test
    public void testCommandLineStandard() {
        System.out.println("commandLineStandard");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        String expResult = " 'bwa'  'mem'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWithMates() {
        System.out.println("commandLineStandardWithMates");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.matesFile = new File("mates.fastq");
        String expResult = " 'bwa'  'mem'  'ref'  'in.fastq' 'mates.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_t() {
        System.out.println("testCommandLineWith_t");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.t = 1;
        String expResult = " 'bwa'  'mem' '-t' '1'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_k() {
        System.out.println("testCommandLineWith_k");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.k = 19;
        String expResult = " 'bwa'  'mem' '-k' '19'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_w() {
        System.out.println("testCommandLineWith_w");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.w = 100;
        String expResult = " 'bwa'  'mem' '-w' '100'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_d() {
        System.out.println("testCommandLineWith_d");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.d = 100;
        String expResult = " 'bwa'  'mem' '-d' '100'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_r() {
        System.out.println("testCommandLineWith_r");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.r = 1.5F;
        String expResult = " 'bwa'  'mem' '-r' '1.5'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_c() {
        System.out.println("testCommandLineWith_c");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.c = 10000;
        String expResult = " 'bwa'  'mem' '-c' '10000'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_P() {
        System.out.println("testCommandLineWith_P");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.P = true;
        String expResult = " 'bwa'  'mem' '-P'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_A() {
        System.out.println("testCommandLineWith_A");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.A = 1;
        String expResult = " 'bwa'  'mem' '-A' '1'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_B() {
        System.out.println("testCommandLineWith_B");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.B = 4;
        String expResult = " 'bwa'  'mem' '-B' '4'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_O() {
        System.out.println("testCommandLineWith_O");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.O = 6;
        String expResult = " 'bwa'  'mem' '-O' '6'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_E() {
        System.out.println("testCommandLineWith_E");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.E = 1;
        String expResult = " 'bwa'  'mem' '-E' '1'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_L() {
        System.out.println("testCommandLineWith_L");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.L = 5;
        String expResult = " 'bwa'  'mem' '-L' '5'  'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_U() {
        System.out.println("testCommandLineWith_U");
        BWAMemCommandLineFunction instance = createRequiredMemCommand();
        instance.U = 9;
        String expResult = " 'bwa'  'mem' '-U' '9' 'ref'  'in.fastq'  >  'out.sam' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private BWAMemCommandLineFunction createRequiredMemCommand(){
        BWAMemCommandLineFunction instance = new BWAMemCommandLineFunction();
        instance.bwa = "bwa";
        instance.prefix = "ref";
        instance.readsFile = new File("in.fastq");
        instance.samFile = new File("out.sam");
        return instance;
    }
}
