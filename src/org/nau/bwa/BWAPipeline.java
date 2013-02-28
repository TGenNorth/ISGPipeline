/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.bwa;

import java.io.File;
import net.sf.picard.util.Log;
import org.nau.picard.AddOrReplaceReadGroupsRunner;
import org.nau.picard.BuildBamIndexRunner;
import org.nau.picard.CleanSamRunner;
import org.nau.picard.MarkDuplicatesRunner;
import org.nau.util.FileUtils;

/**
 *
 * @author jbeckstrom
 */
public class BWAPipeline implements Runnable {

    private static final Log log = Log.getInstance(BWAPipeline.class);
    private final String bwa;
    private final String refPrefix;
    private final String sampleName;
    private final File read1;
    private final File read2;
    private final File outDir;
    private final File tmpBam;
    private final File bam;
    private final File bai;

    public BWAPipeline(String bwa, String refPrefix, File read1, String sampleName, File outDir) {
        this(bwa, refPrefix, read1, null, sampleName, outDir);
    }

    public BWAPipeline(String bwa, String refPrefix, File read1, File read2, String sampleName, File outDir) {
        this.bwa = bwa;
        this.refPrefix = refPrefix;
        this.read1 = read1;
        this.read2 = read2;
        this.sampleName = sampleName;
        this.outDir = outDir;
        tmpBam = new File(outDir, sampleName + ".tmp.bam");
        bam = new File(outDir, sampleName + ".bam");
        bai = new File(outDir, sampleName + ".bai");
    }

    @Override
    public void run() {
        try {
            if (FileUtils.exists(bam) && FileUtils.exists(bai)) {
                System.out.println(bam.getAbsolutePath() + " already exists.");
                return;
            }

            if (read2 == null) {
                runSamse();
            } else {
                runSampe();
            }

            File metrics = new File(outDir, sampleName + ".metrics");

            new MarkDuplicatesRunner(tmpBam, bam, metrics).run();
            new BuildBamIndexRunner(bam, bai).run();

            tmpBam.delete();
        } catch (Throwable ex) {
            log.error(ex, "");
        }
    }

    private void runSampe() {
        if (FileUtils.exists(tmpBam)) {
            return;
        }
        final File sai1 = new File(outDir, sampleName + "_1.sai");
        final File sai2 = new File(outDir, sampleName + "_2.sai");

        new AlnRunner(bwa, refPrefix, read1, sai1).run();
        new AlnRunner(bwa, refPrefix, read2, sai2).run();
        new SampeRunner(bwa, refPrefix, read1, read2, sai1, sai2, tmpBam, sampleName).run();

        sai1.delete();
        sai2.delete();
    }

    private void runSamse() {
        if (FileUtils.exists(tmpBam)) {
            return;
        }
        final File sai = new File(outDir, sampleName + ".sai");

        new AlnRunner(bwa, refPrefix, read1, sai).run();
        new SamseRunner(bwa, refPrefix, read1, sai, tmpBam, sampleName).run();

        sai.delete();
    }
    
    public static void main(String[] args){
        File wd = new File("/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test/test/");
        System.setProperty("java.io.tmpdir", wd.getAbsolutePath());
        
        new IndexRunner("/usr/local/bin/bwa-0.6.2/bwa",
                new File(wd, "ref.fasta"),
                "/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test/test/ref").run();
        
        BWAPipeline pipe = new BWAPipeline("/usr/local/bin/bwa-0.6.2/bwa", 
                "/Users/jbeckstrom/NetBeansProjects/ISGPipeline_test/test/ref", 
                new File(wd, "reads/Burkholderia_pseudomallei-MSHR6891_TAAGTTCG_L006_R1_001.fastq.gz"), 
                new File(wd, "reads/Burkholderia_pseudomallei-MSHR6891_TAAGTTCG_L006_R2_001.fastq.gz"),
                "MSHR6891", wd);
        pipe.run();
    }
}
