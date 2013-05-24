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
        CallableLociCommandLineFunction instance = new CallableLociCommandLineFunction();
        instance.jarFile = new File("GATK.jar");
        instance.inputFile = new File("in.bam");
        instance.referenceFile = new File("ref.fasta");
        instance.out = new File("out.bed");
        instance.summary = new File("out.summary");
        String expResult = "'java'    '-jar' 'GATK.jar'  '-T'  'CallableLoci'"
                + "  '-R'  'ref.fasta'  '-I'  'in.bam'  '-o'  'out.bed'"
                + "  '-summary'  'out.summary'";
        String result = instance.commandLine();
//        for(int i=0; i<Math.min(result.length(), expResult.length()); i++){
//            System.out.printf("%c = %c\n", result.charAt(i), expResult.charAt(i));
//        }
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
}
