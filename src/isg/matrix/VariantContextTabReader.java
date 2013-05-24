/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.matrix;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextTabReader {

    private BufferedReader reader;
    private VariantContextTabHeader header;

    public VariantContextTabReader(File file) throws FileNotFoundException, IOException {
        this.reader = new BufferedReader(new FileReader(file));
        this.header = readHeader();
    }

    private VariantContextTabHeader readHeader() throws IOException {
        String line = nextLine();
        int numSamples = -1;
        while (line.startsWith("#")) {
            if (line.length() > 1) {
                String[] split = line.substring(1).split("=");
                if (split.length == 2) {
                    if (split[0].trim().equals(VariantContextTabHeader.NUM_SAMPLES)) {
                        numSamples = Integer.parseInt(split[1].trim());
                    }
                }
            }
            line = nextLine();
        }
        final List<String> genotypes = new ArrayList<String>();
        final List<String> attributes = new ArrayList<String>();
        String[] split = line.split("\t");

        //parse samples
        int i = 3;
        int lastSampleIndex = (numSamples == -1 ? split.length : numSamples + i);
        for (; i < lastSampleIndex; i++) {
            if (split[i].equalsIgnoreCase(VariantContextTabHeader.PATTERN)) {
                break;
            }
            genotypes.add(split[i]);
        }

        //parse additional info
        for (; i < split.length; i++) {
            String attr = split[i];
            attributes.add(attr);
        }
        
        return new VariantContextTabHeader(attributes, genotypes);
    }

    public String nextLine() {
        String ret = null;
        try {
            ret = reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(VariantContextTabReader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    public VariantContext nextRecord() {
        String line = nextLine();
        if (line == null) {
            return null;
        }
        VariantContextBuilder vcBldr = new VariantContextBuilder();
        String[] split = line.split("\t");
        String chr = split[0];
        int pos = Integer.parseInt(split[1]);
        Allele ref = Allele.create(split[2], true);

        vcBldr.chr(chr).start(pos).stop(pos);

        Set<Allele> alleles = new HashSet<Allele>();
        List<Genotype> genotypes = new ArrayList<Genotype>();

        alleles.add(ref);

        //parse samples
        int index = 3;
        Set<String> samples = header.getGenotypeNames();
        for (String sample: samples) {
            Allele allele = Allele.create(split[index++]);
            if (ref.basesMatch(allele)) {
                allele = ref;
            }
            Genotype g = GenotypeBuilder.create(sample, Arrays.asList(allele));
            if (!allele.isNoCall()) {
                alleles.add(allele);
            }
            
            genotypes.add(g);
        }

        //parse addtional info
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (String key : header.getAttributeKeys()) {
            if (index >= split.length) {
                break;
            }
//            System.out.printf("%d %d %s = %s\n", split.length, index, key, split[index]);
            attributes.put(key, split[index++]);
        }
        vcBldr.attributes(attributes);
        vcBldr.alleles(alleles);
        vcBldr.genotypes(genotypes);
        return vcBldr.make();
    }

    public VariantContextTabHeader getHeader() {
        return header;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        VariantContextTabReader r = new VariantContextTabReader(
                new File("test/test.tab"));
//        VariantContextTabWriter w = new VariantContextTabWriter(
//                new File("test/test.tab"));
        VariantContext vc = null;
//        w.writeHeader(r.getHeader());
        while ((vc = r.nextRecord()) != null) {
            if(!vc.isSNP()){
                System.out.println(vc);
            }
//            w.add(vc);
        }
//        w.close();
    }
}
