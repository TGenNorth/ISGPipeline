/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.HeaderAttribute;
import isg.matrix.HeaderSampleAttribute;
import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

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
        for(String sample: header.getGenotypeNames()){
            header = header.addAttribute(HeaderSampleAttribute.createPatternAttribute(sample));
        }
        header = header.addAttribute(HeaderAttribute.PAT_NUM);
        
        writer.writeHeader(header);

        final PatternNumGenerator patNumGen = new PatternNumGenerator(header.getGenotypeNames());
        VariantContext record = null;
        while ((record = reader.nextRecord()) != null) {
            VariantContextBuilder vcb = new VariantContextBuilder(record);
            Map<String, String> pattern = PatternBuilder.generatePattern(record, header.getGenotypeNames());
            int patNum = patNumGen.getPatternNum(pattern);
            
            GenotypesContext gcntxt = record.getGenotypes();
            List<Genotype> genotypes = new ArrayList<Genotype>();
            for(Genotype g: gcntxt){
                genotypes.add(new GenotypeBuilder(g).attribute(HeaderAttribute.PATTERN_STR, pattern.get(g.getSampleName())).make());
            }
            vcb.genotypes(genotypes);
            vcb.attribute(HeaderAttribute.PAT_NUM_STR, patNum);
            
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
