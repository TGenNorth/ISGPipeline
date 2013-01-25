package org.nau.snpclassifier;

import java.io.*;
import java.util.*;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import org.apache.commons.io.FileUtils;
import org.biojavax.bio.seq.RichFeature;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;

import org.tgen.commons.genbank.GenBank;

public class GenBankAnnotator {

//    private GenBankIterator gbkIter;
//    private GenBank gbk;
//
//    public SNPClassifier(GenBankIterator gbkIter) {
//        this.gbkIter = gbkIter;
//        this.gbk = gbkIter.next();
//    }
    private Map<String, GenBank> gbks = new HashMap<String, GenBank>();
    private ReferenceSequenceFile refSeq;

    public GenBankAnnotator(File gbkDir, File ref) {
        Collection<File> gbkFiles = FileUtils.listFiles(gbkDir, new String[]{"gbk", "gb"}, false);
        for (File f : gbkFiles) {
            addGenbank(new GenBank(f));
        }
        refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(ref);
    }

    public void addGenbank(GenBank gbk) {
        gbks.put(gbk.getName(), gbk);
    }

    public VariantContext annotate(VariantContext vc) {

        if(gbks.isEmpty()){
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

            String locusTag = gbk.getValue("locus_tag", gene);
            int max = gene.getLocation().getMax();
            int min = gene.getLocation().getMin();

            attribs.put("geneStart", min);
            attribs.put("geneEnd", max);

            if (qAllele == null) {
            } else if (gene.getType().equalsIgnoreCase("CDS")) {

                ReferenceSequence subSeq = refSeq.getSubsequenceAt(vc.getChr(), min, max);
                CDS cds = CDS.create(gene, new String(subSeq.getBases()));

                char altState = qAllele.getBaseString().charAt(0);
                if (cds.shouldCompliment(vc.getStart(), rState)) {
                    altState = SnpClassifierUtils.compliment(altState);
                }

                Codon refCodon = cds.getCodon(vc.getStart());
                if(refCodon==null) return vc;
                
                String refAA = refCodon.getAminoAcid(cds.getTranslationTable());//SnpClassifierUtils.getTranslation(refCodon);

                Codon derivedCodon = refCodon.getDerivedCodon(altState);
                if(derivedCodon==null) return vc;
                
                String derivedAA = derivedCodon.getAminoAcid(cds.getTranslationTable());

                attribs.put("refCodon", refCodon.getDna());
                attribs.put("derivedCodon", derivedCodon.getDna());
                attribs.put("refAA", refAA);
                attribs.put("derivedAA", derivedAA);
                attribs.put("genePos", cds.convertRefToCDSPos(vc.getStart()));
                attribs.put("geneStrand", cds.getStrand());
                attribs.put("transTable", cds.getTranslationTable());
                attribs.put("codonStart", cds.getCodonStart());
                
                if (refAA.equals(derivedAA)) {
                    attribs.put("snpType", "sSNP");
                } else {
                    attribs.put("snpType", "nSNP");
                }

            } else if (gene.getType().equalsIgnoreCase("tRNA")) {
                attribs.put("snpType", "tSNP");
            } else if (gene.getType().equalsIgnoreCase("rRNA")) {
                attribs.put("snpType", "rSNP");
            } else if (gene.getType().equalsIgnoreCase("pseudo_gene")) {
                attribs.put("snpType", "pSNP");
            } else {
                attribs.put("snpType", gene.getType() + "SNP");
            }

            attribs.put("locusTag", locusTag);

        } else {
            attribs.put("snpType", "iSNP");
        }

        VariantContextBuilder vcBuilder = new VariantContextBuilder(vc);
        vcBuilder.attributes(attribs);
        return vcBuilder.make();
    }

    private RichFeature getFeatureByType(String type, List<RichFeature> featureList) {
        for (RichFeature rf : featureList) {
            if (rf.getType().equalsIgnoreCase(type)) {
                return rf;
            }
        }
        return null;
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
