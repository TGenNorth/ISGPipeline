 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import isg.tools.genbank.GenBankAnnotator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 * Classifies SNPs of a ISG Matrix file.
 * @author jbeckstrom
 */
public class ClassifyMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Classifies SNPs of a ISG Matrix file.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/data/isg_out.tab");
    @Option(doc = "Output classified ISG Matrix file", optional = false)
    public File OUTPUT;// = new File("test/data/isg_out.translated.tab");
    @Option(doc = "reference fasta file")
    public File REF;
    @Option(doc = "directory of genbank files", optional = false)
    public File GBK_DIR;// = new File("test/data/ref_MSHR1043.filter");

    @Override
    protected int doWork() {
        VariantContextTabReader reader = createReader(INPUT);
        VariantContextTabWriter writer = createWriter(OUTPUT);

        final GenBankAnnotator annotator = new GenBankAnnotator(GBK_DIR, REF);

        VariantContextTabHeader header = reader.getHeader();
        header = annotator.annotate(header);
        writer.writeHeader(header);

        VariantContext record = null;
        long count = 0;
        while ((record = reader.nextRecord()) != null) {
            try {
                writer.add(annotator.annotate(record));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (++count % 100000 == 0) {
                System.out.println(count + " records written");
            }
        }
        writer.close();

        return 0;
    }

    private VariantContextTabReader createReader(File f) {
        try {
            return new VariantContextTabReader(f);
        } catch (FileNotFoundException ex) {
            throw new PicardException("Could not find file: " + f.getAbsolutePath(), ex);
        } catch (IOException ex) {
            throw new PicardException("Error reading matrix file: " + f.getAbsolutePath(), ex);
        }
    }

    private VariantContextTabWriter createWriter(File f) {
        try {
            return new VariantContextTabWriter(f);
        } catch (IOException ex) {
            throw new PicardException("Error writting matrix file: " + f.getAbsolutePath(), ex);
        }
    }

    public static void main(String[] args) {
        System.exit(new ClassifyMatrix().instanceMain(args));
    }
}
