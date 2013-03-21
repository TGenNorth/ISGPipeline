/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.VariantContextTabHeader;
import org.nau.isg.matrix.VariantContextTabReader;
import org.nau.isg.matrix.VariantContextTabWriter;
import org.nau.isg.matrix.ISGMatrixHeader;
import isgtools.util.PatternBuilder;
import isgtools.util.PatternNumGenerator;
import java.io.File;
import java.util.Map;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
public class CalculatePattern extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Calculate the pattern and pattern number for each sample";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/simple.removed.2.txt");
    @Option(doc = "Output matrix file", optional = false)
    public File OUTPUT;// = new File("test/simple.removed.annotated.tab");

    private static final String PAT_FORMAT = "%s:pat";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new CalculatePattern().instanceMain(args));
    }

    @Override
    protected int doWork() {
        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsWritable(OUTPUT);

        VariantContextTabReader reader = openMatrixForReading(INPUT);
        VariantContextTabWriter writer = openMatrixForWriting(OUTPUT);
        

        VariantContextTabHeader header = reader.getHeader();
        header = header.addAttribute(String.format(PAT_FORMAT, ISGMatrixHeader.REF));
        for(String sample: header.getGenotypeNames()){
            String attr = String.format(PAT_FORMAT, sample);
            header = header.addAttribute(attr);
        }
        header = header.addAttribute(ISGMatrixHeader.PATTERN_NUM);
        
        writer.writeHeader(header);

        final PatternNumGenerator patNumGen = new PatternNumGenerator(header.getGenotypeNames());
        VariantContext record = null;
        while ((record = reader.nextRecord()) != null) {

            VariantContextBuilder vcb = new VariantContextBuilder(record);
            Map<String, String> pattern = PatternBuilder.generatePattern(record, header.getGenotypeNames());
            int patNum = patNumGen.getPatternNum(pattern);
            
            vcb.attribute(String.format(PAT_FORMAT, ISGMatrixHeader.REF), pattern.get(ISGMatrixHeader.REF));
            for(String sample: header.getGenotypeNames()){
                vcb.attribute(String.format(PAT_FORMAT, sample), pattern.get(sample));
            }
            vcb.attribute(ISGMatrixHeader.PATTERN_NUM, patNum);
            
            writer.add(vcb.make());
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
}
