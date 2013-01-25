/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.snpclassifier;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.sting.utils.variantcontext.writer.VariantContextWriterFactory;
import org.tgen.commons.vcf.VCFReader;

/**
 *
 * @author jbeckstrom
 */
public class SnpClassifier extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "";
    @Option(doc = "merged vcf file")
    public File IN = new File("resources/paralogs.vcf");
    @Option(doc = "out vcf file")
    public File OUT = new File("resources/out.vcf");
    @Option(doc = "Directory containing genbank files. Files must follow "
    + "naming convention: [sequence_name].gbk", optional = true)
    public File GENBANK_DIRECTORY = new File("resources/");
    @Option(doc = "reference fasta file")
    public File REF = new File("resources/CburRSA493.fasta");

    public static void main(String[] args) {
        System.exit(new SnpClassifier().instanceMain(args));
    }

    @Override
    protected int doWork() {
        try {
            
            final GenBankAnnotator annotator = new GenBankAnnotator(GENBANK_DIRECTORY, REF);
            final VCFReader reader = new VCFReader(IN);
            
            final VariantContextWriter writer = VariantContextWriterFactory.create(OUT, new SAMSequenceDictionary());
            
            VariantContext vc = null;
            writer.writeHeader(reader.getHeader());
            
            while( (vc = reader.next()) != null ){
                try{
                   vc = annotator.annotate(vc); 
                }catch(Exception e){
                    e.printStackTrace();
                }
                
                System.out.println(vc);
                writer.add(vc);
            }
            
            writer.close();
            
        } catch (IOException ex) {
            Logger.getLogger(SnpClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(SnpClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SnpClassifier.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SnpClassifier.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;

    }
}
