/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class DeltaFilterCommandLineFunctionTest {
    
    public DeltaFilterCommandLineFunctionTest() {
    }

    /**
     * Test of commandLine method, of class DeltaFilterCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        DeltaFilterCommandLineFunction instance = new DeltaFilterCommandLineFunction();
        instance.mummerDir = new File("mummer");
        instance.inDelta = new File("in.delta");
        instance.outDelta = new File("out.delta");
        String expResult = "'mummer/delta-filter' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    /**
     * Test of commandLine method, of class DeltaFilterCommandLineFunction.
     */
    @Test
    public void testCommandLineWithOptions() {
        System.out.println("commandLineWithOptions");
        DeltaFilterCommandLineFunction instance = new DeltaFilterCommandLineFunction();
        instance.mummerDir = new File("mummer");
        instance.inDelta = new File("in.delta");
        instance.outDelta = new File("out.delta");
        instance.r = true;
        instance.q = true;
        String expResult = "'mummer/delta-filter' '-r' '-q' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
}
