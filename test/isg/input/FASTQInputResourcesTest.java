/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.IOException;
import java.util.Arrays;
import isg.util.SequenceFilePairPatterns;
import isg.util.SequenceFilePairPattern;
import java.io.File;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class FASTQInputResourcesTest {
    
//    private final SequenceFilePairPatterns patterns = new SequenceFilePairPatterns();
//    
//    public FASTQInputResourcesTest() {
//        SequenceFilePairPattern pattern = new SequenceFilePairPattern("(.*)_([12])\\..*");
//        patterns.addPattern(pattern);
//    }
//
//    @BeforeClass
//    public static void setUpClass() throws Exception {
//    }
//
//    @AfterClass
//    public static void tearDownClass() throws Exception {
//    }
//
//    /**
//     * Test of addFile method, of class FASTQInputResources.
//     */
//    @Test
//    public void testAddFile() throws Exception {
//        System.out.println("addFile");
//        FASTQInputResources instance = new FASTQInputResources(patterns);
//        instance.addFile(new File("A_1.fastq"));
//        instance.addFile(new File("A_2.fastq"));
//        instance.addFile(new File("B_2.fastq"));
//        instance.addFile(new File("B_1.fastq"));
//        
//        List<FASTQInputResource> expResult = Arrays.asList(
//                new FASTQInputResource("A", new File("A_1.fastq"), new File("A_2.fastq")),
//                new FASTQInputResource("B", new File("B_1.fastq"), new File("B_2.fastq")));
//        List<FASTQInputResource> result = instance.getResources();
//        assertEquals(expResult, result);
//    }
//
//    /**
//     * Test of isPending method, of class FASTQInputResources.
//     */
//    @Test
//    public void testIsPending() throws IOException {
//        System.out.println("isPending");
//        FASTQInputResources instance = new FASTQInputResources(patterns);
//        instance.addFile(new File("A_1.fastq"));
//        assertTrue(instance.isPending());
//        instance.addFile(new File("A_2.fastq"));
//        assertFalse(instance.isPending());
//    }
}
