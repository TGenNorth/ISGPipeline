/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.HeaderAttribute;
import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import isg.util.Filter;
import isg.util.FilteringIterator;
import java.io.File;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.util.CollectionUtil;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

/**
 * Filters a SNP matrix by removing all records that include an ambiguous or 
 * missing allele. Cleaning a matrix prior to using a phylogenetic tree program 
 * may be required if the program doesn't support alleles with a '.' or 'N'. 
 * Furthermore, a clean matrix may produce a better phylogenetic tree because 
 * any ambiguity has been filtered out. However, before running clean matrix it 
 * is advised that you investigate the statistics of an ISG run to determine if 
 * certain genomes should be removed. If a genome has a high percentage of ambiguous 
 * or missing calls and is included in the matrix you may find that very few 
 * records are remaining after running CleanMatrix.
 * 
 * @author jbeckstrom
 */
public class CleanMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Removes all records from a matrix file with an "
            + "ambiguous or missing allele.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/simple.txt");
    @Option(doc = "Output matrix file", optional = false)
    public File OUTPUT;// = new File("test/simple.annotated.tab");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new CleanMatrix().instanceMain(args));
    }

    @Override
    protected int doWork() {
        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsWritable(OUTPUT);

        VariantContextTabReader reader = openMatrixForReading(INPUT);
        VariantContextTabWriter writer = openMatrixForWriting(OUTPUT);

        writer.writeHeader(reader.getHeader());

        Iterator<VariantContext> cleanVariantContextIter =
                new FilteringIterator(reader.iterator(), new CleanVariantContextFilter());

        while (cleanVariantContextIter.hasNext()) {
            writer.add(cleanVariantContextIter.next());
        }

        writer.close();

        return 0;
    }

    public VariantContextTabReader openMatrixForReading(File file) {
        try {
            return new VariantContextTabReader(file);
        } catch (Exception ex) {
            throw new PicardException("Failed when opening file: " + file.getName(), ex);
        }
    }

    public VariantContextTabWriter openMatrixForWriting(File file) {
        try {
            return new VariantContextTabWriter(file);
        } catch (Exception ex) {
            throw new PicardException("Failed when opening file: " + file.getName(), ex);
        }
    }

    public static final class CleanVariantContextFilter implements Filter<VariantContext> {

        @Override
        public boolean pass(VariantContext t) {
            for (Genotype g : t.getGenotypes()) {
                for (Allele allele : g.getAlleles()) {
                    if (allele.basesMatch(Allele.NO_CALL) || allele.basesMatch("N")) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
