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
public class ShowSnpsCommandLineFunctionTest {
    
    public ShowSnpsCommandLineFunctionTest() {
    }

    /**
     * Test of commandLine method, of class ShowSnpsCommandLineFunction.
     */
    @Test
    public void testCommandLine() {
        System.out.println("commandLine");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        String expResult = "'mummer/show-snps' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineFlankLength() {
        System.out.println("commandLinFlankLength");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        instance.flankLen = 10;
        String expResult = "'mummer/show-snps' '-x 10' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLinePrintHeader() {
        System.out.println("commandLinPrintHeader");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        instance.printHeader = true;
        String expResult = "'mummer/show-snps' '-H' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineHideAmbiguous() {
        System.out.println("commandLineHideAmbiguous");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        instance.showAmbiguous = false;
        String expResult = "'mummer/show-snps' '-C' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineHideIndels() {
        System.out.println("commandLineHideIndels");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        instance.showIndels = false;
        String expResult = "'mummer/show-snps' '-I' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineShowSeqLen() {
        System.out.println("commandLineShowSeqLen");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        instance.showSeqLen = true;
        String expResult = "'mummer/show-snps' '-l' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    @Test
    public void testCommandLineTabDelimit() {
        System.out.println("commandLineTabDelimit");
        ShowSnpsCommandLineFunction instance = createRequiredCommand();
        instance.tabDelimit = true;
        String expResult = "'mummer/show-snps' '-T' '-r' 'in.delta' > 'out.snps'";
        String result = instance.commandLine();
        assertArrayEquals(expResult.trim().split("\\s+"), result.trim().split("\\s+"));
    }
    
    private ShowSnpsCommandLineFunction createRequiredCommand(){
        ShowSnpsCommandLineFunction instance = new ShowSnpsCommandLineFunction();
        instance.mummerDir = new File("mummer");
        instance.deltaFile = new File("in.delta");
        instance.snpsFile = new File("out.snps");
        instance.flankLen = 0;
        instance.printHeader = false;
        instance.showAmbiguous = true;
        instance.showIndels = true;
        instance.showSeqLen = false;
        instance.tabDelimit = false;
        return instance;
    }
}
