/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.HeaderAttribute;
import isg.matrix.HeaderAttributeImpl;
import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import org.broad.tribble.FeatureReader;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;
import org.broadinstitute.variant.vcf.VCFInfoHeaderLine;

/**
 *
 * @author jbeckstrom
 */
public class VcfToTab extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Convert VCF file to a tabular matrix file.";
    @Option(doc = "Input VCF file", optional = false)
    public File INPUT = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline/tmp/all.gatk.eff.vcf");
    @Option(doc = "Output matrix file", optional = false)
    public File OUTPUT = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline/tmp/all.tab");

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new VcfToTab().instanceMain(args));
    }

    @Override
    protected int doWork() {

        FeatureReader<VariantContext> reader = openVcfForReading(INPUT);
        Iterator<VariantContext> iter = getIteratorQuietly(reader);
        
        VariantContextTabWriter writer = openMatrixForWriting(OUTPUT);
        VariantContextTabHeader header = createHeader((VCFHeader) reader.getHeader());
        writer.writeHeader(header);
        
        while (iter.hasNext()) {
            VariantContext vc = iter.next();
            writer.add(vc);
        }

        writer.close();


        return 0;
    }
    
    private VariantContextTabHeader createHeader(VCFHeader vcfHeader){
        List<HeaderAttribute> attributes = createAttributes(vcfHeader);
        return new VariantContextTabHeader(attributes, vcfHeader.getGenotypeSamples());
    }
    
    private List<HeaderAttribute> createAttributes(VCFHeader vcfHeader){
        List<HeaderAttribute> ret = new ArrayList<HeaderAttribute>();
        for(VCFInfoHeaderLine info: vcfHeader.getInfoHeaderLines()){
            if(info.getID().startsWith("SNPEFF")){
                ret.add(new HeaderAttributeImpl(info.getID()));
            }
        }
        return ret;
    }
    
    private Iterator<VariantContext> getIteratorQuietly(FeatureReader<VariantContext> vcfReader) {
        try {
            return vcfReader.iterator();
        } catch (IOException ex) {
            throw new RuntimeException("An error occured trying to create iterator", ex);
        }
    }

    private FeatureReader<VariantContext> openVcfForReading(File f) {
        try {
            return new TribbleIndexedFeatureReader<VariantContext>(f.getAbsolutePath(), new VCFCodec(), false);
        } catch (IOException ex) {
            throw new RuntimeException("An error occured trying to read file: " + f.getAbsolutePath(), ex);
        }
    }
    
    public VariantContextTabWriter openMatrixForWriting(File file) {
        try {
            return new VariantContextTabWriter(file);
        } catch (Exception ex) {
            throw new RuntimeException("Failed when opening file: " + file.getName(), ex);
        }
    }

}
