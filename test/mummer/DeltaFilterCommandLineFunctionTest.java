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
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        String expResult = "'mummer/delta-filter' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_g() {
        System.out.println("testCommandLineWith_g");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.g = true;
        String expResult = "'mummer/delta-filter' '-g' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_i() {
        System.out.println("testCommandLineWith_i");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.i = 0;
        String expResult = "'mummer/delta-filter' '-i' '0' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_l() {
        System.out.println("testCommandLineWith_l");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.l = 0;
        String expResult = "'mummer/delta-filter' '-l' '0' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_q() {
        System.out.println("testCommandLineWith_q");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.q = true;
        String expResult = "'mummer/delta-filter' '-q' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    /**
     * Test of commandLine method, of class DeltaFilterCommandLineFunction.
     */
    @Test
    public void testCommandLineWith_r() {
        System.out.println("commandLineWith_r");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.r = true;
        String expResult = "'mummer/delta-filter' '-r' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_u() {
        System.out.println("testCommandLineWith_u");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.u = 0;
        String expResult = "'mummer/delta-filter' '-u' '0' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineWith_o() {
        System.out.println("testCommandLineWith_o");
        DeltaFilterCommandLineFunction instance = createRequiredDeltaFilter();
        instance.o = 75;
        String expResult = "'mummer/delta-filter' '-o' '75' 'in.delta' > 'out.delta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private DeltaFilterCommandLineFunction createRequiredDeltaFilter(){
        DeltaFilterCommandLineFunction instance = new DeltaFilterCommandLineFunction();
        instance.mummerDir = new File("mummer");
        instance.inDelta = new File("in.delta");
        instance.outDelta = new File("out.delta");
        return instance;
    }
}
