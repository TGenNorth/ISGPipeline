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
public class FastqInputResourceFactoryTest {
    
    private final SequenceFilePairPatterns patterns = new SequenceFilePairPatterns();
    
    public FastqInputResourceFactoryTest() {
        SequenceFilePairPattern pattern = new SequenceFilePairPattern("(.*)_([12])\\..*");
        patterns.addPattern(pattern);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of addFile method, of class FASTQInputResources.
     */
    @Test
    public void testCreatePair() throws Exception {
        System.out.println("createPair");
        FastqInputResourceFactory instance = new FastqInputResourceFactory(patterns);
        InputResource<?> result = instance.create(new File("A_1.fastq"));
        assertNull(result);
        InputResource<?> result2 = instance.create(new File("A_2.fastq"));
        InputResource<Pair<File, File>> expected = new FastqPairInputResource("A", new Pair<File, File>(new File("A_1.fastq"), new File("A_2.fastq")));
        assertEquals(expected, result2);
    }
    
    @Test
    public void testCreateSingle() throws Exception {
        System.out.println("createSingle");
        FastqInputResourceFactory instance = new FastqInputResourceFactory(patterns);
        InputResource<?> result = instance.create(new File("ABC.fastq"));
        InputResource<File> expected = new FastqInputResource("ABC", new File("ABC.fastq"));
        assertEquals(expected, result);
    }

}
