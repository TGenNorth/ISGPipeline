package isg.tools.genbank;

import isg.matrix.VariantContextTabHeader;
import java.io.*;
import java.util.*;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import org.apache.commons.io.FileUtils;
import org.biojava.bio.Annotation;
import org.biojavax.CrossRef;
import org.biojavax.RankedCrossRef;
import org.biojavax.bio.seq.RichFeature;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.variantcontext.VariantContextBuilder;

public class GenBankAnnotator {

    enum GbkAttr {
        locusTag,
        product,
        geneName,
        GeneID,
        GI,
        geneStart,
        geneEnd,
        refCodon,
        derivedCodon,
        refAA,
        derivedAA,
        genePos,
        geneStrand,
        transTable,
        codonStart,
        snpType;
    };
    
    private Map<String, GenBank> gbks = new HashMap<String, GenBank>();
    private ReferenceSequenceFile refSeq;

    public GenBankAnnotator(File gbkDir, File ref) {
        Collection<File> gbkFiles = FileUtils.listFiles(gbkDir, new String[]{"gbk", "gb"}, false);
        for (File f : gbkFiles) {
            addGenbank(new GenBank(f));
        }
        refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(ref);
    }

    private void addGenbank(GenBank gbk) {
        gbks.put(gbk.getName(), gbk);
    }
    
    public VariantContextTabHeader annotate(VariantContextTabHeader header){
        for(final GbkAttr attrName: GbkAttr.values()){
            header = header.addAttribute(attrName.name());
        }
        return header;
    }

    public VariantContext annotate(VariantContext vc) {

        if (gbks.isEmpty()) {
            return vc;
        }

        GenBank gbk = gbks.get(vc.getChr());
        if (gbk == null) {
            throw new NullPointerException("Could not find a GenBank for " + vc.getChr()
                    + ". Make sure there is a file named '" + vc.getChr() + ".gbk' in GENBANK_DIR.");
        }

        Collection<RichFeature> features = gbk.getOverlappingFeatures(vc.getStart());

        Map<String, Object> attribs = new HashMap(vc.getAttributes());
        char rState = vc.getReference().getBaseString().charAt(0);
        Iterator<Allele> iter = vc.getAlternateAlleles().iterator();
        Allele qAllele = null;
        while (iter.hasNext()) {
            Allele allele = iter.next();
            char base = allele.getBaseString().charAt(0);
            if (base == 'A' || base == 'T' || base == 'C' || base == 'G') {
                qAllele = allele;
                break;
            }
        }
//        Allele qAllele = snpMatrixRecord.getAlternateAllele();

        RichFeature gene = getKnownFeature(features);

        if (gene != null) {

            final String locusTag = getValue("locus_tag", gene);
            final String product = getValue("product", gene);
            final String geneName = getValue("gene", gene);
            final String geneId = getAccession("GeneID", gene);
            final String gi = getAccession("GI", gene);
            final int max = gene.getLocation().getMax();
            final int min = gene.getLocation().getMin();

            attribs.put(GbkAttr.locusTag.name(), locusTag);
            attribs.put(GbkAttr.product.name(), product);
            attribs.put(GbkAttr.geneName.name(), geneName);
            attribs.put(GbkAttr.GeneID.name(), geneId);
            attribs.put(GbkAttr.GI.name(), gi);
            attribs.put(GbkAttr.geneStart.name(), min);
            attribs.put(GbkAttr.geneEnd.name(), max);

            if (qAllele == null) {
            } else if (gene.getType().equalsIgnoreCase("CDS")) {

                ReferenceSequence subSeq = refSeq.getSubsequenceAt(vc.getChr(), min, max);
                CDS cds = CDS.create(gene, new String(subSeq.getBases()));

                char altState = qAllele.getBaseString().charAt(0);
                if (cds.shouldCompliment(vc.getStart(), rState)) {
                    altState = SnpClassifierUtils.compliment(altState);
                }

                Codon refCodon = cds.getCodon(vc.getStart());
                if (refCodon == null) {
                    return vc;
                }

                String refAA = refCodon.getAminoAcid(cds.getTranslationTable());//SnpClassifierUtils.getTranslation(refCodon);

                Codon derivedCodon = refCodon.getDerivedCodon(altState);
                if (derivedCodon == null) {
                    return vc;
                }

                String derivedAA = derivedCodon.getAminoAcid(cds.getTranslationTable());

                attribs.put(GbkAttr.refCodon.name(), refCodon.getDna());
                attribs.put(GbkAttr.derivedCodon.name(), derivedCodon.getDna());
                attribs.put(GbkAttr.refAA.name(), refAA);
                attribs.put(GbkAttr.derivedAA.name(), derivedAA);
                attribs.put(GbkAttr.genePos.name(), cds.convertRefToCDSPos(vc.getStart()));
                attribs.put(GbkAttr.geneStrand.name(), cds.getStrand());
                attribs.put(GbkAttr.transTable.name(), cds.getTranslationTable());
                attribs.put(GbkAttr.codonStart.name(), cds.getCodonStart());

                if (refAA.equals(derivedAA)) {
                    attribs.put(GbkAttr.snpType.name(), "sSNP");
                } else {
                    attribs.put(GbkAttr.snpType.name(), "nSNP");
                }

            } else if (gene.getType().equalsIgnoreCase("tRNA")) {
                attribs.put(GbkAttr.snpType.name(), "tSNP");
            } else if (gene.getType().equalsIgnoreCase("rRNA")) {
                attribs.put(GbkAttr.snpType.name(), "rSNP");
            } else if (gene.getType().equalsIgnoreCase("pseudo_gene")) {
                attribs.put(GbkAttr.snpType.name(), "pSNP");
            } else {
                attribs.put(GbkAttr.snpType.name(), gene.getType() + "SNP");
            }

        } else {
            attribs.put(GbkAttr.snpType.name(), "iSNP");
        }

        VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
        vcBuilder.attributes(attribs);
        return vcBuilder.make();
    }

    private String getValue(String key, RichFeature feature) {
        final Annotation annot = feature.getAnnotation();
        if (annot.containsProperty(key)) {
            return (String) annot.getProperty(key);
        } else {
            return "";
        }
    }

    private String getAccession(String dbName, RichFeature feature) {
        for (Object obj : feature.getRankedCrossRefs()) {
            CrossRef crossRef = ((RankedCrossRef) obj).getCrossRef();
            if(crossRef.getDbname().equalsIgnoreCase(dbName)){
                return crossRef.getAccession();
            }
        }
        return "";
    }

    private RichFeature getKnownFeature(Collection<RichFeature> featureList) {
        for (RichFeature rf : featureList) {
            if (rf.getType().equalsIgnoreCase("CDS")) {
                return rf;
            } else if (rf.getType().equalsIgnoreCase("tRNA")) {
                return rf;
            } else if (rf.getType().equalsIgnoreCase("rRNA")) {
                return rf;
            } else if (rf.getType().equalsIgnoreCase("pseudo_gene")) {
                return rf;
            }
        }
        return null;
    }
}
