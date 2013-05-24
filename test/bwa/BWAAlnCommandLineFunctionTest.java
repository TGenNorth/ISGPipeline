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
        BWAAlnCommandLineFunction instance = new BWAAlnCommandLineFunction();
        instance.bwa = "bwa";
        instance.fastqFile = new File("in.fastq");
        instance.prefix = "ref";
        instance.qualFormat = FastqQualityFormat.Illumina;
        instance.saiFile = new File("out.sai");
        String expResult = " 'bwa'  'aln'  '-I'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCommandLineStandard() {
        System.out.println("commandLineStandard");
        BWAAlnCommandLineFunction instance = new BWAAlnCommandLineFunction();
        instance.bwa = "bwa";
        instance.fastqFile = new File("in.fastq");
        instance.prefix = "ref";
        instance.qualFormat = FastqQualityFormat.Standard;
        instance.saiFile = new File("out.sai");
        String expResult = " 'bwa'  'aln'  '-f'  'out.sai'  'ref'  'in.fastq' ";
        String result = instance.commandLine();
        assertEquals(expResult, result);
    }
}
