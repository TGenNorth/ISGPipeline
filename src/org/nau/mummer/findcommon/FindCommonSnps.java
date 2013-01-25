/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer.findcommon;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;
import org.broadinstitute.sting.utils.variantcontext.writer.VariantContextWriter;
import org.broadinstitute.sting.utils.variantcontext.writer.VariantContextWriterFactory;
import org.tgen.commons.mummer.snp.MumSNPFeature;

/**
 *
 * Find the common snps between two mummer snp files
 * @author jbeckstrom
 */
public class FindCommonSnps implements Runnable{

    private File refSnps; 
    private File querySnps;
    private File realVcf; 
    private File ambiguousVcf;
    private String sampleName;
    
    public FindCommonSnps(File refSnps, File querySnps, File realVcf, File ambiguousVcf, String sampleName){
        this.refSnps = refSnps;
        this.querySnps = querySnps;
        this.realVcf = realVcf;
        this.ambiguousVcf = ambiguousVcf;
        this.sampleName = sampleName;
    }
    
    private boolean exists(){
        return (realVcf.exists() && realVcf.length()>0 &&
                ambiguousVcf.exists() && ambiguousVcf.length()>0);
    }
    
    @Override
    public void run() {
        if(exists()){
            System.out.println(realVcf.getAbsolutePath()+" and/or "+ambiguousVcf.getAbsolutePath()+" already exists");
            return;
        }
        
        //read first snps file into map
        //iterate over second snps file. For each pass look for matching snp and remove from map

        List<VariantContext> ambiguous = new ArrayList<VariantContext>();
        List<VariantContext> real = new ArrayList<VariantContext>();

        Map<String, MumSNPFeature> snpMap = new HashMap<String, MumSNPFeature>();
        Iterator<MumSNPFeature> iter = new MumSnpIterator(refSnps);
        while (iter.hasNext()) {
            MumSNPFeature snp = iter.next();
            String key = snp.getChr() + "_" + snp.getStart() + "_" + snp.getqFastaID() + "_" + snp.getqPos();
            snpMap.put(key, snp);
        }


        Iterator<MumSNPFeature> iter2 = new MumSnpIterator(querySnps);
        while (iter2.hasNext()) {
            MumSNPFeature snp = iter2.next();
            String key = snp.getqFastaID() + "_" + snp.getqPos() + "_" + snp.getChr() + "_" + snp.getStart();
            MumSNPFeature value = snpMap.remove(key);
            if (value != null) {
                real.add(parseSNPByRef(value, sampleName));
            } else {
                ambiguous.add(parseSNPByQuery(snp, sampleName));
            }
        }

        for(MumSNPFeature f: snpMap.values()){
            ambiguous.add(parseSNPByRef(f, sampleName));
        }
        
        Collections.sort(real, new Comparator<VariantContext>(){

            @Override
            public int compare(VariantContext t, VariantContext t1) {
                int cmp = t.getChr().compareTo(t1.getChr());
                if(cmp==0){
                    if(t.getStart()<t1.getStart()){
                        return -1;
                    }else if(t.getStart()>t1.getStart()){
                        return 1;
                    }else{
                        return 0;
                    }
                }
                return cmp;
            }
        });
        
        Collections.sort(ambiguous, new Comparator<VariantContext>(){

            @Override
            public int compare(VariantContext t, VariantContext t1) {
                int cmp = t.getChr().compareTo(t1.getChr());
                if(cmp==0){
                    if(t.getStart()<t1.getStart()){
                        return -1;
                    }else if(t.getStart()>t1.getStart()){
                        return 1;
                    }else{
                        return 0;
                    }
                }
                return cmp;
            }
        });

        
        writeToFile(real, sampleName, realVcf);
        writeToFile(ambiguous, sampleName, ambiguousVcf);
    }
    
    public static void main(String[] args) throws IOException {
        
    }

    public static void writeToFile(List<VariantContext> mumSnps, String sampleName, File file) {

        VCFHeader header = new VCFHeader(null, new HashSet(Arrays.asList(sampleName)));
        VariantContextWriter writer = VariantContextWriterFactory.create(file, new SAMSequenceDictionary());
        writer.writeHeader(header);

        for (VariantContext vc : mumSnps) {
            writer.add(vc);
        }

        writer.close();

    }

    public static VariantContext parseSNPByRef(MumSNPFeature mumSnp, String sampleName) {
        String chr = mumSnp.getChr();
        int start = mumSnp.getStart();
        int end = mumSnp.getEnd();

        Allele refAllele = Allele.create(mumSnp.getrBase(), true);
        Allele altAllele = Allele.create(mumSnp.getqBase());

        Genotype g = new GenotypeBuilder(sampleName, Arrays.asList(altAllele)).make();
        VariantContextBuilder vcBuilder = new VariantContextBuilder("source", chr, start, end, Arrays.asList(refAllele, altAllele));
        vcBuilder.genotypes(g);
        return vcBuilder.make();
    }
    
    public static VariantContext parseSNPByQuery(MumSNPFeature mumSnp, String sampleName) {
        String chr = mumSnp.getqFastaID();
        int start = mumSnp.getqPos();
        int end = mumSnp.getqPos();

        Allele refAllele = Allele.create(mumSnp.getqBase(), true);
        Allele altAllele = Allele.create(mumSnp.getrBase());

        Genotype g = new GenotypeBuilder(sampleName, Arrays.asList(altAllele)).make();
        VariantContextBuilder vcBuilder = new VariantContextBuilder("source", chr, start, end, Arrays.asList(refAllele, altAllele));
        vcBuilder.genotypes(g);
        return vcBuilder.make();
    }

    
    
}
