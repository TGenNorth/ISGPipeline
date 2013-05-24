/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class BWAIndexCommandLineFunctionTest {
    
    public BWAIndexCommandLineFunctionTest() {
    }

    /**
     * Test of commandLine method, of class BWAIndexCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        BWAIndexCommandLineFunction instance = new BWAIndexCommandLineFunction();
        instance.fastaFile = new File("ref.fasta");
        String expResult = " 'bwa'  'index'  'ref.fasta' ";
        String result = instance.commandLine();
        assertEquals(expResult, result);
    }
}
