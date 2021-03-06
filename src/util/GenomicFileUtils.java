/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import isg.input.InputResourceValidationExceptions.MissingReadGroupException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMSequenceRecord;
import org.broad.tribble.FeatureReader;
import org.broad.tribble.TribbleIndexedFeatureReader;
import org.broadinstitute.sting.utils.exceptions.UserException;
import org.broadinstitute.variant.variantcontext.VariantContext;
import org.broadinstitute.variant.vcf.VCFCodec;
import org.broadinstitute.variant.vcf.VCFHeader;

/**
 *
 * @author jbeckstrom
 */
public class GenomicFileUtils {
    
    public static List<String> extractSequenceNames(File fasta) throws IOException{
        List<String> ret = new ArrayList<String>();
        ReferenceSequenceFile refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(fasta);
        ReferenceSequence seq = null;
        while((seq = refSeq.nextSequence()) != null){
            final String seqName = seq.getName();
            ret.add(seqName);
        }
        return ret;
    }
    
    public static String extractFirstSampleName(File f) throws IOException{
        List<String> ret = extractSampleNames(f);
        if(ret.isEmpty()){
            throw new UserException("no sample names defined in file: "+f);
        }
        return ret.get(0);
    }
    
    public static List<String> extractSampleNames(File f) throws IOException{
        final String filename = f.getName();
        if(filename.endsWith(".vcf")){
            return extractSampleNamesFromVCF(f);
        }else if(filename.endsWith(".bam") || filename.endsWith(".sam")){
            return extractSampleNamesFromBAM(f);
        }else{
            return Arrays.asList(FileUtils.stripExtension(filename));
        }
    }
    
    public static List<String> extractSampleNamesFromVCF(File f) throws IOException{
        FeatureReader<VariantContext> reader = new TribbleIndexedFeatureReader<VariantContext>(f.getAbsolutePath(), new VCFCodec(), false);
        final VCFHeader header = (VCFHeader) reader.getHeader();
        return header.getGenotypeSamples();
    }
    
    public static List<String> extractSampleNamesFromBAM(File f){
        final List<String> ret = new ArrayList<String>();
        final SAMFileReader in = new SAMFileReader(f);
        final List<SAMReadGroupRecord> rgs = in.getFileHeader().getReadGroups();
        if(rgs==null){
            throw new MissingReadGroupException(f);
        }
        for(SAMReadGroupRecord rg: rgs){
            ret.add(rg.getSample());
        }
        return ret;
    }
    
}
