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
public class NucmerCommandLineFunctionTest {
    
    public NucmerCommandLineFunctionTest() {
    }

    /**
     * Test of commandLine method, of class NucmerCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        String expResult = "'mummer/nucmer' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLinePrefix() {
        System.out.println("commandLinePrefix");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.prefix = "abc";
        String expResult = "'mummer/nucmer' '--prefix=abc' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineShowCoords() {
        System.out.println("commandLineShowCoords");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.showCoords = true;
        String expResult = "'mummer/nucmer' '--coords' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMaxMatch() {
        System.out.println("commandLineMaxMatch");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.maxmatch = true;
        String expResult = "'mummer/nucmer' '--maxmatch' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineNoSimplify() {
        System.out.println("commandLineNoSimplify");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.simplify = false;
        String expResult = "'mummer/nucmer' '--nosimplify' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private NucmerCommandLineFunction createRequiredNucmer(){
        NucmerCommandLineFunction instance = new NucmerCommandLineFunction();
        instance.mummerDir = new File("mummer");
        instance.refFasta = new File("ref.fasta");
        instance.qryFasta = new File("qry.fasta");
        return instance;
    }
}
