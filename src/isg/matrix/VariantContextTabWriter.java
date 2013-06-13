/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.matrix;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.broadinstitute.variant.variantcontext.Allele;
import org.broadinstitute.variant.variantcontext.Genotype;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextTabWriter {
    
    private PrintWriter pw;
    
    private VariantContextTabHeader header;
    
    public VariantContextTabWriter(File file) throws IOException{
        pw = new PrintWriter(new FileWriter(file));
    }
    
    public void writeHeader(VariantContextTabHeader header){
        this.header = header;
        pw.printf("#%s=%d\n", "numSamples", header.numSamples());
        pw.print("Chrom");
        pw.print("\t");
        pw.print("Pos");
        pw.print("\t");
        pw.print("Ref");
        pw.print("\t");
        for(String n: header.getGenotypeNames()){
            pw.print(n);
            pw.print("\t");
        }
        for(HeaderAttribute k: header.getAttributeKeys()){
            pw.print(k);
            pw.print("\t");
        }
        pw.println();
    }
    
    public void add(VariantContext vc){
        pw.print(vc.getChr());
        pw.print("\t");
        pw.print(vc.getStart());
        pw.print("\t");
        pw.print(vc.getReference().getBaseString());
        pw.print("\t");
        
        for(String n: header.getGenotypeNames()){
            Genotype g = vc.getGenotype(n);
            pw.print(getAllelesAsString(g));
            pw.print("\t");
        }
        
        for(HeaderAttribute k: header.getAttributeKeys()){
            Object obj = getAttribute(k, vc);
            if(obj!=null){
                pw.print(obj.toString());
            }
            pw.print("\t");
        }
        pw.println();
    }
    
    public void close(){
        pw.close();
    }
    
    private Object getAttribute(HeaderAttribute attr, VariantContext vc){
        if(attr instanceof HeaderSampleAttribute){
            final String sampleName = ((HeaderSampleAttribute)attr).getSampleName();
            Genotype g = vc.getGenotype(sampleName);
            if(g==null){
                throw new IllegalArgumentException("Could not find genotype for sample: "+sampleName);
            }
            return g.getAnyAttribute(attr.getName());
        }
        return vc.getAttribute(attr.getName());
    }
    
    private String getAllelesAsString(Genotype g){
        String ret = "";
        for(Allele allele: g.getAlleles()){
            if(allele.isNoCall()){
                ret += ".";
            }else{
                ret += allele.getBaseString();
            }
        }
        return ret;
    }
    
}
