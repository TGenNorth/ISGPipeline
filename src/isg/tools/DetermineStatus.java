/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.HeaderAttribute;
import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.util.EnumSet;
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
 * Determines the status of a record in a SNP matrix and appends the status info 
 * in another column. 
 * 
 * @author jbeckstrom
 */
public class DetermineStatus extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Annotates a matrix with status";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/simple.txt");
    @Option(doc = "Output matrix file", optional = false)
    public File OUTPUT;// = new File("test/simple.annotated.tab");

    enum Status{
        clean, missing, ambiguous;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new DetermineStatus().instanceMain(args));
    }

    @Override
    protected int doWork() {
        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsWritable(OUTPUT);

        VariantContextTabReader reader = openMatrixForReading(INPUT);
        VariantContextTabWriter writer = openMatrixForWriting(OUTPUT);
        

        VariantContextTabHeader header = reader.getHeader();
        header = header.addAttribute(HeaderAttribute.STATUS);
        
        writer.writeHeader(header);

        VariantContext record = null;
        while ((record = reader.nextRecord()) != null) {

            Set<Status> snpStatus = EnumSet.noneOf(Status.class);
            
            for(Genotype g: record.getGenotypesOrderedByName()){
                for(Allele allele: g.getAlleles()){
                    if(allele.basesMatch(Allele.NO_CALL)){
                        snpStatus.add(Status.missing);
                    }else if(allele.basesMatch("N")){
                        snpStatus.add(Status.ambiguous);
                    }
                }
            }
            
            if(snpStatus.isEmpty()){
                snpStatus.add(Status.clean);
            }
            
            VariantContextBuilder vcb = new VariantContextBuilder(record);
            vcb.attribute(HeaderAttribute.STATUS.getName(), CollectionUtil.join(snpStatus, ","));
            
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
