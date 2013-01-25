/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.nau.isg.example;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.tgen.commons.reference.ReferenceSequenceManager;
import org.tgen.commons.reference.ReferenceSequenceManagerFactory;
import org.nau.isg.matrix.ISGMatrix;

/**
 *
 * @author jbeckstrom
 */
public class MergeMumSNPFiles {

    public static void main(String[] args) throws IOException{

        Collection<File> files = FileUtils.listFiles(new File("/Volumes/isilon.tgen.org/tnorth/sbeckstr/foster/isg/mummer"),
                new String[]{"snps"}, false);
        ReferenceSequenceManager mgr = ReferenceSequenceManagerFactory.createReferenceSequenceManager(new File(""));
        
        List<File> filesToUse = new ArrayList<File>();
        Iterator<File> iter = files.iterator();
        int count = 0;
        while(iter.hasNext()){
            count++;
//            if(count>10) break;
            filesToUse.add(iter.next());
        }


        Set<String> sampleNames = new HashSet<String>();
        for(File file: filesToUse){
            sampleNames.add(getSampleName(file));
        }



        ISGMatrix matrix = new ISGMatrix(sampleNames);

//        count = 0;
//        for(File file: filesToUse){
//            count++;
//            System.out.println(count);
//            BasicFeatureSource source = BasicFeatureSource.getFeatureSource(file.getAbsolutePath(), new MumSNPCodec(), false);
//            MumSNPConverter converter = new MumSNPConverter(source, getSampleName(file), mgr);
//            while(converter.hasNext()){
//                VariantContext vc = converter.next();
//                if(vc.isSNP()) matrix.addVariantContext(vc);
//            }
//        }

//        VCFHeader header = new VCFHeader(null, sampleNames);
//        StandardVCFWriter writer = new StandardVCFWriter(new File("test.vcf"));
//        writer.writeHeader(header);
//        List<VariantContext> variants = matrix.getVariants();
//        Collections.sort(variants, new Comparator<VariantContext>(){
//
//            public int compare(VariantContext t, VariantContext t1) {
//                int ret = t.getChr().compareTo(t1.getChr());
//                if(ret==0){
//                    if(t.getStart()<t1.getStart()){
//                        ret = -1;
//                    }else if(t.getStart()>t1.getStart()){
//                        ret = 1;
//                    }else{
//                        ret = 0;
//                    }
//                }
//                return ret;
//            }
//
//        });
//        for(VariantContext vc: variants){
//            writer.add(vc);
//        }
//        writer.close();

    }

    private static String getSampleName(File file){
        int beginIndex = file.getName().indexOf(":")+1;
        int endIndex = file.getName().indexOf(".");
        String sampleName = file.getName().substring(beginIndex, endIndex);
        return sampleName;
    }

}
