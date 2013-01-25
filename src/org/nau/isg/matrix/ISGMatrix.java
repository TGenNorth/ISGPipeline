/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nau.isg.matrix;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrix {

    private final Set<String> genotypeSampleNames = new LinkedHashSet<String>();

    private final ISGMatrixDictionary dict = new ISGMatrixDictionary();


    public ISGMatrix(Set<String> genotypeSampleNames){
        this.genotypeSampleNames.addAll(genotypeSampleNames);
    }

    public void addVariantContext(VariantContext vc){
        validate(vc);

        VariantContext vcToAdd;
        if(dict.containsVariantContext(vc)){
            VariantContext currentVC = dict.getVariantContext(vc.getChr(), vc.getStart());
            vcToAdd = mergeVariantContext(currentVC, vc);
        }else{
            vcToAdd = createVariantContext(vc);
        }
        dict.putVariantContext(vcToAdd.getChr(), vcToAdd.getStart(), vcToAdd);
    }

    public List<VariantContext> getVariants(){
        return dict.getVariants();
    }

    private VariantContext mergeVariantContext(VariantContext vc1, VariantContext vc2){
        if(!vc1.getReference().equals(vc2.getReference())){
            throw new IllegalArgumentException("Reference alleles don't match: "+vc1.getReference()+" != "+vc2.getReference());
        }
        List<Genotype> genotypes = getValidUniqueGenotypes(vc1, vc2);
        List<Allele> alleles = getUniqueAllelesFromGenotypes(genotypes);
        
        VariantContextBuilder vcBuilder = new VariantContextBuilder("isg", vc1.getChr(), vc1.getStart(), vc1.getEnd(), alleles);
        vcBuilder.genotypes(genotypes);
        return vcBuilder.make();
    }

    private VariantContext createVariantContext(VariantContext vc){
        //create a genotype for each sample
        List<Genotype> genotypes = getValidGenotypes(vc);
        List<Allele> alleles = getUniqueAllelesFromGenotypes(genotypes);
        
        VariantContextBuilder vcBuilder = new VariantContextBuilder("isg", vc.getChr(), vc.getStart(), vc.getEnd(), alleles);
        vcBuilder.genotypes(genotypes);
        return vcBuilder.make();
    }

    private List<Allele> getUniqueAllelesFromGenotypes(List<Genotype> genotypes){
        List<Allele> ret = new ArrayList<Allele>();
        for(Genotype genotype: genotypes){
            List<Allele> alleles = genotype.getAlleles();
            for(Allele allele: alleles){
                if(!ret.contains(allele)){
                    ret.add(allele);
                }
            }
        }
        return ret;
    }

    private List<Genotype> getValidGenotypes(VariantContext vc){
        List<Genotype> ret = new ArrayList<Genotype>();
        for(String sampleName: genotypeSampleNames){
            if(vc.hasGenotype(sampleName)){
                ret.add(vc.getGenotype(sampleName));
            }
        }
        return ret;
    }

    private List<Genotype> getValidUniqueGenotypes(VariantContext vc1, VariantContext vc2){
        List<Genotype> ret = new ArrayList<Genotype>();
        for(String sampleName: genotypeSampleNames){
            if(vc1.hasGenotype(sampleName)){
                ret.add(vc1.getGenotype(sampleName));
            }else if(vc2.hasGenotype(sampleName)){
                ret.add(vc2.getGenotype(sampleName));
            }
        }
        return ret;
    }
    
    private void validate(VariantContext vc){
        if(!vc.isSNP())
            throw new IllegalArgumentException("VariantContext must be a snp");
        if(!vc.hasGenotypes()){
            throw new IllegalArgumentException("No genotypes found in VariantContext.");
        }
        if(!hasValidGenotypes(vc)){
            throw new IllegalArgumentException("No valid genotypes found in VariantContext.");
        }
    }

    private boolean isValidGenotype(Genotype g){
        return genotypeSampleNames.contains(g.getSampleName());
    }
    
    private boolean hasValidGenotypes(VariantContext vc){
        for(String sampleName: genotypeSampleNames){
            if(vc.hasGenotype(sampleName)) return true;
        }
        return false;
    }

}
