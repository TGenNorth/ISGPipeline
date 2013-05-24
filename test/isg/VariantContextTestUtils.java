/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.GenotypeBuilder;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;
import static org.junit.Assert.*;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextTestUtils {

    public static VariantContext createSNP(final int pos, final String chr, final String sample, final String refStr, final String altStr) {
        return createSNP(pos, chr, sample, Allele.create(refStr, true), Arrays.asList(Allele.create(altStr)), Collections.EMPTY_MAP, VariantContext.NO_LOG10_PERROR);
    }

    public static VariantContext createSNP(final Allele ref, final List<Allele> alts) {
        return createSNP(1, "chr1", "s1", ref, alts, Collections.EMPTY_MAP, VariantContext.NO_LOG10_PERROR);
    }

    public static VariantContext createSNP(final String refStr, final List<Allele> alts) {
        return createSNP(1, "chr1", "s1", Allele.create(refStr, true), alts, Collections.EMPTY_MAP, VariantContext.NO_LOG10_PERROR);
    }

    public static VariantContext createSNP(final String refStr, final String altStr) {
        return createSNP(1, "chr1", "s1", Allele.create(refStr, true), Arrays.asList(Allele.create(altStr)), Collections.EMPTY_MAP, VariantContext.NO_LOG10_PERROR);
    }

    public static VariantContext createSNP(final String refStr, final String altStr, final Map<String, Object> attributes) {
        return createSNP(1, "chr1", "s1", Allele.create(refStr, true), Arrays.asList(Allele.create(altStr)), attributes, VariantContext.NO_LOG10_PERROR);
    }

    public static VariantContext createSNP(final String refStr, final String altStr, final Map<String, Object> attributes, final double log10PError) {
        return createSNP(1, "chr1", "s1", Allele.create(refStr, true), Arrays.asList(Allele.create(altStr)), attributes, log10PError);
    }

    public static VariantContext createSNP(final int pos, final String chr, final String sample, final Allele ref, final List<Allele> alt, final Map<String, Object> attributes, final double log10PError) {
        Set<Allele> alleles = new HashSet<Allele>(alt);
        alleles.add(ref);
        List<Genotype> genotypes = Arrays.asList(
                new GenotypeBuilder(sample, alt).make());

        return new VariantContextBuilder().alleles(alleles).genotypes(genotypes).chr(chr).start(pos).stop(pos).attributes(attributes).log10PError(log10PError).make();

    }

    public static void assertVariantContextEquals(VariantContext vc1, VariantContext vc2) {
        assertVariantContextEquals(vc1, vc2, false);
    }

    public static void assertVariantContextEquals(VariantContext vc1, VariantContext vc2, boolean ignoreAttributes) {
        assertEquals(vc1.getChr(), vc2.getChr());
        assertEquals(vc1.getStart(), vc2.getStart());
        assertEquals(vc1.getEnd(), vc2.getEnd());
        assertEquals(vc1.getAlleles(), vc2.getAlleles());
        assertEquals(vc1.getNSamples(), vc2.getNSamples());
        assertEquals(vc1.getLog10PError(), vc2.getLog10PError(), 0);
        if (!ignoreAttributes) {
            assertEquals(vc1.getAttributes(), vc2.getAttributes());
        }
        for (final String sample: vc1.getSampleNames()) {
            Genotype g1 = vc1.getGenotype(sample);
            Genotype g2 = vc2.getGenotype(sample);
            assertTrue(String.format("%s != %s", g1, g2), g1.sameGenotype(g2));
        }
    }
}
