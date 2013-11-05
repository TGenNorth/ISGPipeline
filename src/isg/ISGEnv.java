/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import java.io.File;
import java.util.Collection;
import net.sf.picard.io.IoUtil;
import org.apache.commons.io.FileUtils;

/**
 * Manages the directory structure of ISG.
 * 
 * @author jbeckstrom
 */
public class ISGEnv {

    /* directory names */
    private static final String FASTA = "fastas";
    private static final String MUMMER = "mummer";
    private static final String READS = "reads";
    private static final String BAMS = "bams";
    private static final String VCF = "vcf";
    private static final String COV = "cov";
    private static final String OUT = "out";
    private static final String GBK = "genbank";
    private static final String DUPS = "dups";
    private static final String OUT_FILE = "isg_out.tab";
    
    private final File root;
    private final File fastaDir ;
    private final File mummerDir ;
    private final File readsDir ;
    private final File bamsDir ;
    private final File vcfDir ;
    private final File covDir ;
    private final File outDir ;
    private final File gbkDir ;
    private final File dupsDir ;

    public ISGEnv(final File root) {
        this.root = createDirIfNotExists(root);
        fastaDir = createDirIfNotExists(root, FASTA);
        mummerDir = createDirIfNotExists(root, MUMMER);
        readsDir = createDirIfNotExists(root, READS);
        bamsDir = createDirIfNotExists(root, BAMS);
        vcfDir = createDirIfNotExists(root, VCF);
        covDir = createDirIfNotExists(root, COV);
        outDir = createDirIfNotExists(root, OUT);
        gbkDir = createDirIfNotExists(root, GBK);
        dupsDir = createDirIfNotExists(root, DUPS);
    }
    
    private File createDirIfNotExists(File dir, String name){
        return createDirIfNotExists(new File(dir, name));
    }

    private File createDirIfNotExists(File dir){
        if (dir.exists()) {
            IoUtil.assertDirectoryIsWritable(dir);
        } else {
            if (!dir.mkdir()) {
                throw new IllegalStateException("Could not create directory: " + dir.getAbsolutePath());
            }
        }
        return dir;
    }
    
    public File getReference(){
        Collection<File> files = FileUtils.listFiles(root, new String[]{"fasta"}, false);
        if(files.size()>1){
            throw new IllegalStateException("Found more than one reference file: "+files);
        }else if(files.size()==1){
            return files.iterator().next();
        }else{
            return null;
        }
    }
    
    public Collection<File> getBams(){
        return FileUtils.listFiles(bamsDir, new String[]{"bam"}, false);
    }
    
    public Collection<File> getFastas(){
        return FileUtils.listFiles(fastaDir, new String[]{"fasta"}, false);
    }
    
    public Collection<File> getVcfs(){
        return FileUtils.listFiles(vcfDir, new String[]{"vcf"}, false);
    }
    
    public File getOutFile(){
        return new File(outDir, OUT_FILE);
    }
    
    public File getOutDoneFile(){
        return new File(outDir, "."+OUT_FILE+".done");
    }

    public File getBamsDir() {
        return bamsDir;
    }

    public File getCovDir() {
        return covDir;
    }

    public File getDupsDir() {
        return dupsDir;
    }

    public File getFastaDir() {
        return fastaDir;
    }

    public File getGbkDir() {
        return gbkDir;
    }

    public File getMummerDir() {
        return mummerDir;
    }

    public File getOutDir() {
        return outDir;
    }

    public File getReadsDir() {
        return readsDir;
    }

    public File getVcfDir() {
        return vcfDir;
    }
}
