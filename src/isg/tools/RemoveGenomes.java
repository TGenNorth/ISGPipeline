/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabHeader;
import isg.matrix.VariantContextTabReader;
import isg.matrix.VariantContextTabWriter;
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
import net.sf.picard.util.CollectionUtil;
import org.apache.commons.io.FileUtils;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypesContext;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

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

            VariantContext vc = removeGenotypesFromVariantContext(samplesToRemove, record);

            if (isSNP(vc)) {
                writer.add(vc);
            } else {
                removeCount++;
            }

            if (++count % 100000 == 0) {
                System.out.printf("%d lines processed\n", count);
            }
        }

        writer.close();
        System.out.printf("Removed %d records!\n", removeCount);

        return 0;
    }

    public static VariantContext removeGenotypesFromVariantContext(Set<String> genotypesToRemove, VariantContext vc) {
        final VariantContextBuilder vcb = new VariantContextBuilder(vc);
        GenotypesContext gc = vc.getGenotypes();
        List<Genotype> genotypesToAdd = new ArrayList<Genotype>();
        for (Genotype g : gc) {
            if (!genotypesToRemove.contains(g.getSampleName())) {
                genotypesToAdd.add(g);
            }
        }
        vcb.genotypes(genotypesToAdd);
        vcb.alleles(getUniqueCallableAlleles(vc.getReference(), genotypesToAdd));
        return vcb.make();
    }
    
    public static Set<Allele> getUniqueCallableAlleles(Allele ref, List<Genotype> genotypes){
        Set<Allele> alleles = CollectionUtil.makeSet(ref);
        for(Genotype g: genotypes){
            for(Allele a: g.getAlleles()){
                if(a.isCalled()){
                    alleles.add(a);
                }
            }
        }
        return alleles;
    }
    
    public static boolean isSNP(VariantContext vc) {
        for (Allele a : vc.getAlternateAlleles()) {
            if (!a.basesMatch("N")) {
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

    private List<String> readLinesQuietly(File f) {
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