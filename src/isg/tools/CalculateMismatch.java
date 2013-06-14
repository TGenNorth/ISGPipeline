/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import com.google.common.collect.AbstractIterator;
import isg.matrix.HeaderAttribute;
import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.util.Iterator;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.util.PeekableIterator;
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
        header = header.addAttribute(HeaderAttribute.MISMATCH);
        writer.writeHeader(header);

        Iterator<VariantContext> iter = new CalculateMismatchIterator(toIter(reader));
        while(iter.hasNext()){
            writer.add(iter.next());
        }

        writer.close();

        return 0;
    }
    
    private Iterator<VariantContext> toIter(final VariantContextTabReader reader){
        return new AbstractIterator<VariantContext>(){

            @Override
            protected VariantContext computeNext() {
                VariantContext ret = reader.nextRecord();
                return (ret!=null) ? ret : endOfData();
            }
            
        };
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
    
    public static final class CalculateMismatchIterator extends AbstractIterator<VariantContext>{

        private final PeekableIterator<VariantContext> iter;
        private VariantContext last;
        
        public CalculateMismatchIterator(Iterator<VariantContext> iter){
            this.iter = new PeekableIterator<VariantContext>(iter);
        }

        @Override
        protected VariantContext computeNext() {
            return iter.hasNext() ? annotateMismatch(iter.next()) : endOfData();
        }
        
        private VariantContext annotateMismatch(VariantContext cur){
            
            VariantContext next = iter.peek();
            
            int m1 = calculateMismatch(last, cur);
            int m2 = calculateMismatch(cur, next);
            
            VariantContextBuilder vcb = new VariantContextBuilder(cur);
            if (m1 == -1) { //beginning of file
                vcb.attribute(HeaderAttribute.MISMATCH.getName(), m2);
            } else if (m2 == -1) { //beginning of chr
                vcb.attribute(HeaderAttribute.MISMATCH.getName(), m1);
            } else {
                vcb.attribute(HeaderAttribute.MISMATCH.getName(), (m1 < m2) ? m1 : m2);
            }
            
            last = cur;
            return vcb.make();
        }
        
        private int calculateMismatch(VariantContext first, VariantContext second){
            if (first!=null && second!=null && first.getChr().equals(second.getChr())) {
                return Math.abs(second.getStart() - first.getEnd());
            }
            return -1;
        }
    }

    public static void main(String[] args) {
        System.exit(new CalculateMismatch().instanceMain(args));
    }
}
