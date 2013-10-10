/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class FormatForTree extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Convert ISG SNP Matrix into a format for use by a phylogenetic tree program.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;
    @Option(doc = "Output fasta", optional = false)
    public File OUTPUT;
    @Option(doc = "Output format", optional = false)
    public FORMAT format = FORMAT.fasta;
    @Option(doc = "If 'true' ambiguous base calls will be given an 'N' otherwise"
    + " the reference base is assumed", optional = false)
    public boolean AMBIGUOUS = true;
    @Option(doc = "If 'true' base calls without any coverage will be given an '.' otherwise"
    + " the reference base is assumed", optional = false)
    public boolean NO_COVERAGE = true;

    enum FORMAT {

        fasta
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new FormatForTree().instanceMain(args));
    }

    @Override
    protected int doWork() {

        VariantContextTabReader reader = openMatrixForReading(INPUT);
        Converter converter = new FastaConverter(NO_COVERAGE, AMBIGUOUS);

        VariantContext record = null;

        while ((record = reader.nextRecord()) != null) {
            converter.append(record);
        }

        converter.toFile(OUTPUT);


        return 0;
    }

    public static void assertHaploid(Genotype g) {
        if (g.getPloidy() != 1) {
            throw new IllegalStateException("Only haploid genotypes are supported. found genotype: " + g.toString());
        }
    }

    public static void assertSingleBaseAllele(Allele a) {
        if (a.length() != 1) {
            throw new IllegalStateException("Only single base alleles are supported. found allele: " + a.toString());
        }
    }

    public VariantContextTabReader openMatrixForReading(File file) {
        try {
            return new VariantContextTabReader(file);
        } catch (Exception ex) {
            throw new RuntimeException("Failed when opening file: " + file.getName(), ex);
        }
    }

    public interface Converter {

        public void append(VariantContext vc);

        public void append(String sample, String base);

        public Set<String> getSamples();

        public String getSequence(String sample);

        public void toFile(File f);
    }

    public static class FastaConverter implements Converter {

        private Map<String, StringBuilder> samples = new HashMap<String, StringBuilder>();
        private boolean noCoverage;
        private boolean ambiguous;
       
        public FastaConverter(boolean noCoverage, boolean ambiguous){
            this.noCoverage = noCoverage;
            this.ambiguous = ambiguous;
        }
        
        @Override
        public void append(String sample, String base) {
            StringBuilder sb = samples.get(sample);
            if (sb == null) {
                sb = new StringBuilder();
                samples.put(sample, sb);
            }
            sb.append(base);
        }
        
        @Override
        public void append(VariantContext vc) {
            for (Genotype g : vc.getGenotypes()) {
                assertHaploid(g);
                Allele allele = g.getAllele(0);
                assertSingleBaseAllele(allele);
                if ((allele.isNoCall() && !noCoverage) || (allele.basesMatch("N") && !ambiguous)) {
                    allele = vc.getReference();
                }
                append(g.getSampleName(), allele.getBaseString());
            }
        }

        @Override
        public Set<String> getSamples() {
            return samples.keySet();
        }

        @Override
        public String getSequence(String sample) {
            StringBuilder sb = samples.get(sample);
            if (sb == null) {
                throw new IllegalArgumentException("Could not find sample: " + sample);
            }
            return sb.toString();
        }

        @Override
        public void toFile(File f) {
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new FileWriter(f));
                for (String sample : getSamples()) {
                    pw.println(">" + sample);
                    pw.println(getSequence(sample));
                }
            } catch (IOException ex) {
                Logger.getLogger(FormatForTree.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                pw.close();
            }
        }

    }
}
