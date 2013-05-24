/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import gatk.UnifiedGenotyperCommandLineFunction.OutMode;
import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class UnifiedGenotyperCommandLineFunctionTest {
    
    public UnifiedGenotyperCommandLineFunctionTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of analysisName method, of class UnifiedGenotyperCommandLineFunction.
     */
    @Test
    public void testAnalysisName() {
        System.out.println("analysisName");
        UnifiedGenotyperCommandLineFunction instance = new UnifiedGenotyperCommandLineFunction();
        String expResult = "UnifiedGenotyper";
        String result = instance.analysisName();
        assertEquals(expResult, result);
    }

    /**
     * Test of commandLine method, of class UnifiedGenotyperCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        String expResult = createStandardResult();
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    /**
     * Test of commandLine method, of class UnifiedGenotyperCommandLineFunction.
     */
    @Test
    public void testCommandLineOutMode() {
        System.out.println("commandLineOutMode");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        instance.outMode = OutMode.EMIT_ALL_SITES;
        String expResult = createStandardResult() + 
                "  '--output_mode' 'EMIT_ALL_SITES'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineOutGenotypesLikelihoodsModel() {
        System.out.println("commandLineOutGenotypesLikelihoodsModel");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        instance.genotype_likelihoods_model = UnifiedGenotyperCommandLineFunction.GenotypeLikelihoodsModel.BOTH;
        String expResult = createStandardResult()
                + " '--genotype_likelihoods_model' 'BOTH'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    /**
     * Test of commandLine method, of class UnifiedGenotyperCommandLineFunction.
     */
    @Test
    public void testCommandLineMinBaseQuality() {
        System.out.println("commandLineOutMode");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        instance.min_base_quality = 0;
        String expResult = createStandardResult()
                + " '--min_base_quality_score' '0'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }

    @Test
    public void testCommandLinePloidy() {
        System.out.println("commandLinePloidy");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        instance.ploidy = 2;
        String expResult = createStandardResult() + 
                "  '-ploidy' '2'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinQualThreshold() {
        System.out.println("commandLineMinQualThreshold");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        instance.stand_call_conf = 30;
        String expResult = createStandardResult() + 
                "  '--standard_min_confidence_threshold_for_calling' '30'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinQualThresholdEmit() {
        System.out.println("commandLineMinQualThresholdEmi");
        UnifiedGenotyperCommandLineFunction instance = createStandardInstance();
        instance.stand_emit_conf = 0;
        String expResult = createStandardResult() + 
                "  '--standard_min_confidence_threshold_for_emitting' '0'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private UnifiedGenotyperCommandLineFunction createStandardInstance(){
        UnifiedGenotyperCommandLineFunction instance = new UnifiedGenotyperCommandLineFunction();
        instance.jarFile = new File("GATK.jar");
        instance.inputFile = new File("in.bam");
        instance.out = new File("out.vcf");
        instance.referenceFile = new File("ref.fasta");
        return instance;
    }
    
    private String createStandardResult(){
        return "'java'    '-jar' 'GATK.jar'  '-T'  'UnifiedGenotyper'"
                + "  '-R'  'ref.fasta'  '-I'  'in.bam'  '-o'  'out.vcf'";
    }
}
