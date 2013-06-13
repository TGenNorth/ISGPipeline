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
import java.util.Collection;
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
        if (numSamples == -1) {
            throw new IllegalStateException(String.format("Malformed Header: '%s' field could not be found", VariantContextTabHeader.NUM_SAMPLES));
        }
        final List<String> genotypes = new ArrayList<String>();
        final List<HeaderAttribute> attributes = new ArrayList<HeaderAttribute>();
        String[] split = line.split("\t");
        int i = 0;
        assertFieldExists(VariantContextTabHeader.CHROM, split[i++]);
        assertFieldExists(VariantContextTabHeader.POS, split[i++]);
        assertFieldExists(VariantContextTabHeader.REF, split[i++]);

        //parse samples
        int lastSampleIndex = numSamples + i;
        for (; i < lastSampleIndex; i++) {
            genotypes.add(split[i]);
        }

        //parse additional info
        for (; i < split.length; i++) {
            String attr = split[i];
            attributes.add(parseHeaderAttribute(attr));
        }

        return new VariantContextTabHeader(attributes, genotypes);
    }

    private void assertFieldExists(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new IllegalStateException(String.format("Malformed Header: '%s' field could not be found", expected));
        }
    }

    private HeaderAttribute parseHeaderAttribute(String str) {
        if (str.contains(":")) {
            return new HeaderSampleAttribute(str);
        }
        return new HeaderAttributeImpl(str);
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
        int index = 0;
        String[] split = line.split("\t");
        String chr = split[index++];
        int pos = Integer.parseInt(split[index++]);
        Allele ref = Allele.create(split[index++], true);

        vcBldr.chr(chr).start(pos).stop(pos+ref.length()-1);

        Set<Allele> alleles = new HashSet<Allele>();
        Map<String, GenotypeBuilder> genotypes = new HashMap<String, GenotypeBuilder>();

        alleles.add(ref);

        //parse samples
        Set<String> samples = header.getGenotypeNames();
        for (String sample : samples) {
            Allele allele = Allele.create(split[index++]);
            if (ref.basesMatch(allele)) {
                allele = ref;
            }
            GenotypeBuilder g = new GenotypeBuilder(sample, Arrays.asList(allele));
            if (!allele.isNoCall()) {
                alleles.add(allele);
            }

            genotypes.put(sample, g);
        }

        //parse addtional info
        Map<String, Object> attributes = new HashMap<String, Object>();
        for (HeaderAttribute key : header.getAttributeKeys()) {
            if (index >= split.length) {
                break;
            }
            if (key instanceof HeaderSampleAttribute) {
                GenotypeBuilder g = genotypes.get(((HeaderSampleAttribute) key).getSampleName());
                g.attribute(key.getName(), split[index++]);
            } else {
                attributes.put(key.getName(), split[index++]);
            }

        }
        vcBldr.attributes(attributes);
        vcBldr.alleles(alleles);
        vcBldr.genotypes(buildGenotypes(genotypes.values()));
        return vcBldr.make();
    }

    private List<Genotype> buildGenotypes(Collection<GenotypeBuilder> gbs) {
        final List<Genotype> ret = new ArrayList<Genotype>();
        for (GenotypeBuilder gb : gbs) {
            ret.add(gb.make());
        }
        return ret;
    }

    public VariantContextTabHeader getHeader() {
        return header;
    }
}
