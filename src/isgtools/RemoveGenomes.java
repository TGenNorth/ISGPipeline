/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.VariantContextTabHeader;
import org.nau.isg.matrix.VariantContextTabReader;
import org.nau.isg.matrix.VariantContextTabWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import org.apache.commons.io.FileUtils;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
public class RemoveGenomes extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Remove genomes from ISG Matrix file. Any records that "
            + "don't have a SNP after the genomes are removed will be excluded "
            + "from the output matrix file.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/simple.removed.txt");
    @Option(doc = "Output matrix with specified genomes excluded.", optional = false)
    public File OUTPUT;// = new File("test/simple.removed.2.txt");
    @Option(doc = "Sample name to split on.", optional = true)
    public List<String> SAMPLE_NAME = new ArrayList<String>();//Arrays.asList("S2");
    @Option(doc = "Sample name regular expression to split on", optional = true)
    public String REGEX;
    @Option(doc = "File with list of sample names to split on", optional = true)
    public File SAMPLES_FILE;

    @Override
    protected int doWork() {

        VariantContextTabReader reader = openMatrixForReading(INPUT);
        VariantContextTabWriter writer = openMatrixForWriting(OUTPUT);
        Set<String> samplesToRemove = getSamplesToRemove(reader.getHeader());

        VariantContextTabHeader header = reader.getHeader().removeSamples(samplesToRemove);
        writer.writeHeader(header);
        VariantContext record = null;
        int count = 0;
        int removeCount = 0;
        while ((record = reader.nextRecord()) != null) {
            
            final VariantContextBuilder vcb = new VariantContextBuilder();
            vcb.chr(record.getChr());
            vcb.start(record.getStart());
            vcb.stop(record.getEnd());
            
            Set<Allele> alleles = new HashSet<Allele>();
            alleles.add(record.getReference());
            
            List<Genotype> genotypes = new ArrayList<Genotype>();
            for(String sample: record.getSampleNames()){
                if(samplesToRemove.contains(sample)){
                    continue;
                }
                Genotype g = record.getGenotype(sample);
                genotypes.add(g);
                for(Allele allele: g.getAlleles()){
                    if(!allele.equals(Allele.NO_CALL)){
                        alleles.add(allele);
                    }
                }
            }
            
            vcb.genotypes(genotypes);
            vcb.alleles(alleles);
            vcb.attributes(record.getAttributes());
            
            VariantContext vc = vcb.make();
            
            if (isSNP(vc)) {
                writer.add(vc);
            } else {
                removeCount++;
            }
            
            count++;
            if (count % 100000 == 0) {
                System.out.printf("%d lines processed\n", count);
            }
        }

        writer.close();
        System.out.printf("Removed %d records!\n", removeCount);

        return 0;
    }
    
    public static boolean isSNP(VariantContext vc) {
        for (Allele a : vc.getAlternateAlleles()) {
            if (!a.basesMatch("N") && !a.basesMatch(Allele.NO_CALL_STRING)) {
                return true;
            }
        }
        return false;
    }

    private Set<String> getSamplesToRemove(VariantContextTabHeader header) {
        Set<String> ret = new HashSet<String>();
        if (SAMPLE_NAME != null) {
            ret.addAll(getSamplesToRemove(header, SAMPLE_NAME));
        }
        if (REGEX != null) {
            Pattern p = Pattern.compile(REGEX);
            for (String sample : header.getGenotypeNames()) {
                if (p.matcher(sample).matches()) {
                    ret.add(sample);
                }
            }
        }
        if (SAMPLES_FILE != null) {
            List<String> sampleNames = readLinesQuietly(SAMPLES_FILE);
            ret.addAll(getSamplesToRemove(header, sampleNames));
        }
        return ret;
    }

    private List<String> getSamplesToRemove(VariantContextTabHeader header, List<String> samplesToRemove) {
        List<String> ret = new ArrayList<String>();
        for (String s1 : samplesToRemove) {
            for (String s2 : header.getGenotypeNames()) {
                if (s1.equalsIgnoreCase(s2)) {
                    ret.add(s2);
                }
            }
        }
        return ret;
    }
    
    private List<String> readLinesQuietly(File f){
        try {
            return FileUtils.readLines(f);
        } catch (IOException ex) {
            throw new PicardException("Failed when reading file: " + f.getName(), ex);
        }
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
        System.exit(new RemoveGenomes().instanceMain(args));
    }
}