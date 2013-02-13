/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.bwa;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.util.Log;
import net.sf.picard.util.ProcessExecutor;
import org.nau.picard.AddOrReplaceReadGroupsRunner;
import org.nau.picard.BuildBamIndexRunner;
import org.nau.picard.CleanSamRunner;
import org.nau.picard.MarkDuplicatesRunner;
import org.nau.util.FileUtils;
import org.tgen.commons.pipeline.CommandUtils;
import org.tgen.commons.utils.BWACommandFactory;
import org.tgen.commons.utils.PicardCommandFactory;

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
    private final File sam;
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
        sam = new File(outDir, sampleName + ".sam");
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

            File rgBam = new File(outDir, sampleName + ".rg.bam");
            File cleanBam = new File(outDir, sampleName + ".rg.clean.bam");
            File metrics = new File(outDir, sampleName + ".metrics");

            new AddOrReplaceReadGroupsRunner(sam, rgBam, sampleName).run();
            new CleanSamRunner(rgBam, cleanBam).run();
            new MarkDuplicatesRunner(cleanBam, bam, metrics).run();
            new BuildBamIndexRunner(bam, bai).run();

            sam.delete();
            rgBam.delete();
            cleanBam.delete();
        } catch (Throwable ex) {
            log.error(ex, "");
        }
    }

    private void runSampe() {
        if (FileUtils.exists(sam)) {
            return;
        }
        final File sai1 = new File(outDir, sampleName + "_1.sai");
        final File sai2 = new File(outDir, sampleName + "_2.sai");

        new AlnRunner(bwa, refPrefix, read1, sai1).run();
        new AlnRunner(bwa, refPrefix, read2, sai2).run();
        new SampeRunner(bwa, refPrefix, read1, read2, sai1, sai2, sam).run();

        sai1.delete();
        sai2.delete();
    }

    private void runSamse() {
        if (FileUtils.exists(sam)) {
            return;
        }
        final File sai = new File(outDir, sampleName + ".sai");

        new AlnRunner(bwa, refPrefix, read1, sai).run();
        new SamseRunner(bwa, refPrefix, read1, sai, sam).run();

        sai.delete();
    }
}
