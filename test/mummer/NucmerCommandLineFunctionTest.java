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
        instance.coords = true;
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
        instance.nosimplify = true;
        String expResult = "'mummer/nucmer' '--nosimplify' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMum() {
        System.out.println("commandLineMum");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.mum = true;
        String expResult = "'mummer/nucmer' '--mum' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineBreaklen() {
        System.out.println("commandLineBreaklen");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.breaklen = 10;
        String expResult = "'mummer/nucmer' '--breaklen' '10' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMincluster() {
        System.out.println("commandLineMincluster");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.mincluster = 10;
        String expResult = "'mummer/nucmer' '--mincluster' '10' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineDiagfactor() {
        System.out.println("commandLineDiagfactor");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.diagfactor = 0.12F;
        String expResult = "'mummer/nucmer' '--diagfactor' '0.12' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineForward() {
        System.out.println("commandLineForward");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.forward = true;
        String expResult = "'mummer/nucmer' '--forward' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineReverse() {
        System.out.println("commandLineReverse");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.reverse = true;
        String expResult = "'mummer/nucmer' '--reverse' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMaxgap() {
        System.out.println("commandLineMaxgap");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.maxgap = 90;
        String expResult = "'mummer/nucmer' '--maxgap' '90' 'ref.fasta' 'qry.fasta'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineMinmatch() {
        System.out.println("commandLineMinmatch");
        NucmerCommandLineFunction instance = createRequiredNucmer();
        instance.minmatch = 20;
        String expResult = "'mummer/nucmer' '--minmatch' '20' 'ref.fasta' 'qry.fasta'";
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
