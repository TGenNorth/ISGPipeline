/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import org.broadinstitute.sting.utils.collections.Pair;
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
public class InputResourceFactoryTest {

    private static File fq1 = new File("A_1.fastq");
    private static File fq2 = new File("A_2.fastq");
    private final SequenceFilePairPatterns patterns = new SequenceFilePairPatterns();

    public InputResourceFactoryTest() {
        SequenceFilePairPattern pattern = new SequenceFilePairPattern("(.*)_([12])\\..*");
        patterns.addPattern(pattern);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        fq1.createNewFile();
        fq2.createNewFile();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        fq1.delete();
        fq2.delete();
    }

    /**
     * Test of addFile method, of class FASTQInputResources.
     */
    @Test
    public void testCreatePair() throws Exception {
        System.out.println("createPair");
        InputResourceFactoryImpl instance = new InputResourceFactoryImpl(patterns);
        InputResource<?> result = instance.create(fq1);
        InputResource<Pair<File, File>> expected = new FastqPairInputResource("A", new Pair<File, File>(new File("A_1.fastq"), new File("A_2.fastq")));
        assertEquals(expected, result);
        result = instance.create(fq2);
        assertNull(result);
    }

    @Test
    public void testCreateSingle() throws Exception {
        System.out.println("createSingle");
        InputResourceFactoryImpl instance = new InputResourceFactoryImpl(patterns);
        InputResource<?> result = instance.create(new File("ABC.fastq"));
        InputResource<File> expected = new FastqInputResource("ABC", new File("ABC.fastq"));
        assertEquals(expected, result);
    }
}
