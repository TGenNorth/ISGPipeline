/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
public class CalculateMismatch extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Calculate the smallest mismatch distance between snps";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/simple.txt");
    @Option(doc = "Output matrix with specified genomes excluded.", optional = false)
    public File OUTPUT;// = new File("test/simple.mismatch.txt");

    @Override
    protected int doWork() {
        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsWritable(OUTPUT);

        VariantContextTabReader reader = openMatrixForReading(INPUT);
        VariantContextTabWriter writer = openMatrixForWriting(OUTPUT);

        VariantContextTabHeader header = reader.getHeader();
        header = header.addAttribute("mismatch");
        writer.writeHeader(header);

        VariantContext cur = null;
        VariantContext last = reader.nextRecord();
        int m1 = -1;
        while ((cur = reader.nextRecord()) != null) {

            int m2 = -1;
            if (last.getChr().equals(cur.getChr())) {
                m2 = Math.abs(cur.getStart() - last.getEnd());
            }

            VariantContextBuilder vcb = new VariantContextBuilder(last);
            if (m1 == -1) { //beginning of file
                vcb.attribute("mismatch", m2);
            } else if (m2 == -1) { //beginning of chr
                vcb.attribute("mismatch", m1);
            } else {
                vcb.attribute("mismatch", (m1 < m2) ? m1 : m2);
            }
            writer.add(vcb.make());

            last = cur;
            m1 = m2;
        }

        VariantContextBuilder vcb = new VariantContextBuilder(last);
        vcb.attribute("mismatch", m1);
        writer.add(vcb.make());
        writer.close();

        return 0;
    }

    public VariantContext annotateMismatch(int m1, int m2, VariantContext vc) {
        VariantContextBuilder vcb = new VariantContextBuilder(vc);
        if (m1 == -1) { //beginning of file
            vcb.attribute("mismatch", m2);
        } else if (m2 == -1) { //beginning of chr
            vcb.attribute("mismatch", m2);
        } else {
            vcb.attribute("mismatch", (m1 < m2) ? m1 : m2);
        }
        return vcb.make();
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

    public static void main(String[] args) {
        System.exit(new CalculateMismatch().instanceMain(args));
    }
}
