/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isgpipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class ISGEnv {

    private File isg;
    private File vcfDir;
    private File mummerDir;
    private File outDir;
    private File bamDir;
    private File covDir;
    private File genBankDir;
    private File fastaDir;
    private File dupsDir;
    private File readsDir;
    private File ref;
    private File refDict;
    private File mergeVcfFile;
    private File isgOutFile;

    public ISGEnv(File isg) throws IOException {
        this.isg = isg;
        init();
    }

    public boolean exists() {
        if (!isg.exists()) {
            return false;
        }
        if (!vcfDir.isDirectory()) {
            return false;
        }
        if (!mummerDir.isDirectory()) {
            return false;
        }
        if (!bamDir.isDirectory()) {
            return false;
        }
        if (!covDir.isDirectory()) {
            return false;
        }
        if (!genBankDir.isDirectory()) {
            return false;
        }
        if (!fastaDir.isDirectory()) {
            return false;
        }
        if (!outDir.isDirectory()) {
            return false;
        }
        if (!dupsDir.isDirectory()) {
            return false;
        }
        if (!readsDir.isDirectory()) {
            return false;
        }
        return true;
    }

    public void create() {
        if (!isg.isDirectory()) {
            isg.mkdir();
        }
        if (!vcfDir.isDirectory()) {
            vcfDir.mkdir();
        }
        if (!mummerDir.isDirectory()) {
            mummerDir.mkdir();
        }
        if (!bamDir.isDirectory()) {
            bamDir.mkdir();
        }
        if (!covDir.isDirectory()) {
            covDir.mkdir();
        }
        if (!genBankDir.isDirectory()) {
            genBankDir.mkdir();
        }
        if (!fastaDir.isDirectory()) {
            fastaDir.mkdir();
        }
        if (!outDir.isDirectory()) {
            outDir.mkdir();
        }
        if (!dupsDir.isDirectory()) {
            dupsDir.mkdir();
        }
        if (!readsDir.isDirectory()) {
            readsDir.mkdir();
        }
    }

    public void validate() throws FileNotFoundException {
        if (!ref.exists()) {
            throw new FileNotFoundException("Could not find reference: " + ref.getAbsolutePath());
        }
    }

    private void init() {
        ref = new File(isg.getAbsolutePath() + "/ref.fasta");
        refDict = new File(isg.getAbsolutePath() + "/ref.dict");
        vcfDir = new File(isg.getAbsolutePath() + "/vcf");
        mummerDir = new File(isg.getAbsolutePath() + "/mummer");
        bamDir = new File(isg.getAbsolutePath() + "/bams");
        covDir = new File(isg.getAbsolutePath() + "/coverage");
        genBankDir = new File(isg.getAbsolutePath() + "/genBank");
        fastaDir = new File(isg.getAbsolutePath() + "/fastas");
        dupsDir = new File(isg.getAbsolutePath() + "/dups");
        outDir = new File(isg.getAbsolutePath() + "/out");
        mergeVcfFile = new File(outDir.getAbsolutePath() + "/merged.vcf");
        isgOutFile = new File(outDir.getAbsolutePath() + "/isg_out.tab");
        readsDir = new File(isg, "reads");
    }

    public File getVcfDir() {
        return vcfDir;
    }

    public File getMummerDir() {
        return mummerDir;
    }

    public File getOutDir() {
        return outDir;
    }

    public File getCovDir() {
        return covDir;
    }
    
    public File getDupsDir(){
        return dupsDir;
    }
    
    public File getReadsDir(){
        return readsDir;
    }

    public File getRef() {
        return ref;
    }

    public File getIsgOutFile() {
        return isgOutFile;
    }

    public File getMergeVcfFile() {
        return mergeVcfFile;
    }

    public File getGenBankDir() {
        return genBankDir;
    }
    
    public File getRefDict(){
        return refDict;
    }

    public File getBamDir() {
        return bamDir;
    }
    
    public File getCoordsDups() {
        return new File(mummerDir.getAbsolutePath() + "/ref.coords");
    }

    public Collection<File> getMumSnps() {
        return FileUtils.listFiles(mummerDir, new String[]{"snps"}, false);
    }

    public Collection<File> getVCFs() {
        return FileUtils.listFiles(vcfDir, new String[]{"vcf"}, false);
    }

    public Collection<File> getBams() {
        return FileUtils.listFiles(bamDir, new String[]{"bam"}, false);
    }

    public Collection<File> getCoords() {
        return FileUtils.listFiles(mummerDir, new String[]{"coords"}, false);
    }

    public Collection<File> getFastas() {
        return FileUtils.listFiles(fastaDir, new String[]{"fasta", "fal"}, false);
    }
}
