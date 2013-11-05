/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class CallableLociCommandLineFunctionTest {
    
    public CallableLociCommandLineFunctionTest() {
    }

    /**
     * Test of analysisName method, of class CallableLociCommandLineFunction.
     */
    @Test
    public void testAnalysisName() {
        System.out.println("analysisName");
        CallableLociCommandLineFunction instance = new CallableLociCommandLineFunction();
        String expResult = "CallableLoci";
        String result = instance.analysisName();
        assertEquals(expResult, result);
    }

    /**
     * Test of commandLine method, of class CallableLociCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        CallableLociCommandLineFunction instance = createStandardInstance();
        String expResult = createStandardResult();
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineFrlmq() {
        System.out.println("commandLineFrlmq");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.maxFractionOfReadsWithLowMAPQ = 0.1;
        String expResult = createStandardResult();
        expResult += " '-frlmq' '0.1'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMaxDepth() {
        System.out.println("commandLineMaxDepth");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.maxDepth = -1;
        String expResult = createStandardResult();
        expResult += " '--maxDepth' '-1'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMaxLowMAPQ() {
        System.out.println("commandLineMaxLowMAPQ");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.maxLowMAPQ = 1;
        String expResult = createStandardResult();
        expResult += " '--maxLowMAPQ' '1'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinBaseQuality() {
        System.out.println("commandLineMinBaseQuality");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.minBaseQuality = 20;
        String expResult = createStandardResult();
        expResult += " '--minBaseQuality' '20'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinMappingQuality() {
        System.out.println("commandLineMinMappingQuality");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.minMappingQuality = 10;
        String expResult = createStandardResult();
        expResult += " '--minMappingQuality' '10'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinDepth() {
        System.out.println("commandLineMinDepth");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.minDepth = 4;
        String expResult = createStandardResult();
        expResult += " '--minDepth' '4'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinDepthForLowMAPQ() {
        System.out.println("commandLineMinDepthForLowMAPQ");
        CallableLociCommandLineFunction instance = createStandardInstance();
        instance.minDepthForLowMAPQ = 10;
        String expResult = createStandardResult();
        expResult += " '--minDepthForLowMAPQ' '10'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private CallableLociCommandLineFunction createStandardInstance(){
        CallableLociCommandLineFunction instance = new CallableLociCommandLineFunction();
        instance.jarFile = new File("GATK.jar");
        instance.inputFile = new File("in.bam");
        instance.referenceFile = new File("ref.fasta");
        instance.out = new File("out.bed");
        instance.summary = new File("out.summary");
        return instance;
    }
    
    private String createStandardResult(){
        return "'java'    '-jar' 'GATK.jar'  '-T'  'CallableLoci'"
                + "  '-R'  'ref.fasta'  '-I'  'in.bam'  '-o'  'out.bed'"
                + "  '-summary'  'out.summary'";
    }
}
