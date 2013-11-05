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
public class BWAAlnCommandLineFunctionTest {
    
    public BWAAlnCommandLineFunctionTest() {
    }

    /**
     * Test of commandLine method, of class BWAAlnCommandLineFunction.
     */
    @Test
    public void testCommandLineIllumina() {
        System.out.println("commandLineIllumina");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.qualFormat = FastqQualityFormat.Illumina;
        String expResult = "'bwa' 'aln' '-I'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineStandard() {
        System.out.println("commandLineStandard");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        String expResult = " 'bwa'  'aln'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_n() {
        System.out.println("testCommandLineWith_n");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.n = 0.1;
        String expResult = " 'bwa'  'aln' '-n' '0.1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_o() {
        System.out.println("testCommandLineWith_o");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.o = 1;
        String expResult = " 'bwa'  'aln' '-o' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_e() {
        System.out.println("testCommandLineWith_e");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.e = 1;
        String expResult = " 'bwa'  'aln' '-e' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_i() {
        System.out.println("testCommandLineWith_i");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.i = 1;
        String expResult = " 'bwa'  'aln' '-i' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_d() {
        System.out.println("testCommandLineWith_d");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.d = 1;
        String expResult = " 'bwa'  'aln' '-d' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_l() {
        System.out.println("testCommandLineWith_l");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.l = 1;
        String expResult = " 'bwa'  'aln' '-l' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_k() {
        System.out.println("testCommandLineWith_k");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.k = 1;
        String expResult = " 'bwa'  'aln' '-k' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_m() {
        System.out.println("testCommandLineWith_m");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.m = 1;
        String expResult = " 'bwa'  'aln' '-m' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_t() {
        System.out.println("testCommandLineWith_t");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.t = 1;
        String expResult = " 'bwa'  'aln' '-t' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_M() {
        System.out.println("testCommandLineWith_M");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.M = 1;
        String expResult = " 'bwa'  'aln' '-M' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_O() {
        System.out.println("testCommandLineWith_O");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.O = 1;
        String expResult = " 'bwa'  'aln' '-O' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_E() {
        System.out.println("testCommandLineWith_E");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.E = 1;
        String expResult = " 'bwa'  'aln' '-E' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_R() {
        System.out.println("testCommandLineWith_R");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.R = 1;
        String expResult = " 'bwa'  'aln' '-R' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_q() {
        System.out.println("testCommandLineWith_q");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.q = 1;
        String expResult = " 'bwa'  'aln' '-q' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_B() {
        System.out.println("testCommandLineWith_B");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.B = 1;
        String expResult = " 'bwa'  'aln' '-B' '1'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_L() {
        System.out.println("testCommandLineWith_L");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.L = true;
        String expResult = " 'bwa'  'aln' '-L'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_N() {
        System.out.println("testCommandLineWith_N");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.N = true;
        String expResult = " 'bwa'  'aln' '-N'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_Y() {
        System.out.println("testCommandLineWith_Y");
        BWAAlnCommandLineFunction instance = createRequiredAlnCommand();
        instance.Y = true;
        String expResult = " 'bwa'  'aln' '-Y'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private BWAAlnCommandLineFunction createRequiredAlnCommand(){
        BWAAlnCommandLineFunction instance = new BWAAlnCommandLineFunction();
        instance.bwa = "bwa";
        instance.fastqFile = new File("in.fastq");
        instance.prefix = "ref";
        instance.qualFormat = FastqQualityFormat.Standard;
        instance.saiFile = new File("out.sai");
        return instance;
    }
}
