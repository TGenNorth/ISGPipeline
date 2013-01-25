/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.isgpipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.sam.CreateSequenceDictionary;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.sting.utils.codecs.vcf.VCFHeader;
import org.nau.coverage.bam.FindCoverageRunner;
import org.nau.finddups.FindDupsRunner;
import org.nau.isg2.ISG2;
import org.nau.mummer.MummerEnv;
import org.nau.mummer.MummerRunnerFactory;
import org.nau.mummer.NucmerRunner;
import org.nau.mummer.SharedMummerRunner;
import org.tgen.commons.gatk.UnifiedGenotyperRunner;
import org.tgen.commons.gatk.UnifiedGenotyperRunner.OutMode;
import org.tgen.commons.reference.CreateFastaIndex;
import org.tgen.commons.utils.VariantContextMergingIterator;
import org.tgen.commons.vcf.VCFReader;

/**
 *
 * @author jbeckstrom
 */
public class ISGPipeline extends CommandLineProgram {

    @Usage(programVersion = "0.13")
    public String USAGE = "Combines SNPs into a matrix for genotyping. "
            + "Functionality includes identification of ambiguous SNPs, "
            + "identification of regions of no coverage, and annotation of "
            + "SNPs using genbank.";
    @Option(doc = "ISG root directory. If directory does not exist it will be "
    + "created", optional = false)
    public File ISG;
    @Option(doc = "Path to MUMmer.", optional = true)
    public File MUMMER;
    @Option(doc = "Path to GATK 2.1 or later.", optional = true)
    public File GATK;
    @Option(doc = "Path to SolSNP options file. To get a list of all available options "
    + "refer to http://sourceforge.net/projects/solsnp/files/SolSNP-1.1/", optional = true)
    public File SOLSNP_OPTIONS_FILE;
    @Option(doc = "Minimum amount of reads to be considered covered.", optional = true)
    public int MIN_COVERAGE = 3;
    @Option(doc = "Minimum amount of reads to call a snp", optional = true)
    public int MIN_SNP_COVERAGE = 3;
    @Option(doc = "Minimum genotype quality to be considered a snp", optional = true)
    public Integer MIN_QUAL = 4;
    @Option(doc = "Solsnp's minimum confidence score allowed for calls.", optional = true)
    public Double FILTER = .85;
    @Option(doc = "Number of threads", optional = true)
    public Integer NUM_THREADS = 1;
    @Option(doc = "Overwrite existing files", optional = true)
    public Boolean overwrite = true;

    @Override
    protected String[] customCommandLineValidation() {
        List<String> errors = new ArrayList<String>();

        if (MIN_SNP_COVERAGE < MIN_COVERAGE) {
            errors.add("MIN_SNP_COVERAGE must be greater than or equal to MIN_COVERAGE");
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return errors.toArray(new String[errors.size()]);
        }
    }

    private void runUnifiedGenotyper(ISGEnv isg) throws InterruptedException {
        System.out.println("running UnifiedGenotyper...");
        ExecutorService es = Executors.newFixedThreadPool(NUM_THREADS);
        //run solsnp on bams
        final Collection<File> bamFiles = isg.getBams();
        for (File bamFile : bamFiles) {
            final File out = new File(isg.getVcfDir(), getSampleName(bamFile) + ".vcf");
            final UnifiedGenotyperRunner runner = new UnifiedGenotyperRunner(GATK.getAbsolutePath(), out, bamFile, isg.getRef(), -1, 0, OutMode.EMIT_VARIANTS_ONLY, 2);
            es.submit(runner);
        }
        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    private void runSolSNP(ISGEnv isg) throws InterruptedException {
        System.out.println("running solsnp...");
        ExecutorService es = Executors.newFixedThreadPool(NUM_THREADS);
        //run solsnp on bams
        Collection<File> bamFiles = isg.getBams();
        for (File bamFile : bamFiles) {
            final File out = new File(isg.getVcfDir(), getSampleName(bamFile) + ".vcf");
            org.nau.solsnp.SolSNPRunner runner = null;
            if (SOLSNP_OPTIONS_FILE == null) {
                runner = new org.nau.solsnp.SolSNPRunner(bamFile, out, isg.getRef(), FILTER);
            } else {
                runner = new org.nau.solsnp.SolSNPRunner(bamFile, out, isg.getRef(), SOLSNP_OPTIONS_FILE);
            }
            es.submit(runner);
        }
        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    private void runMummer(ISGEnv isg, MummerEnv mummerEnv) throws InterruptedException {
        System.out.println("running mummer...");
        ExecutorService es = Executors.newFixedThreadPool(NUM_THREADS);
        //run mummer on fastas
        Collection<File> fastaFiles = isg.getFastas();
        for (File fastaFile : fastaFiles) {
            String sampleName = MummerRunnerFactory.getFilePrefix(fastaFile);
            Runnable runner = new SharedMummerRunner(isg.getRef(),
                    fastaFile,
                    new File(isg.getVcfDir(), sampleName + ".vcf"),
                    new File(isg.getCovDir(), sampleName + ".interval_list"),
                    mummerEnv);
            es.submit(runner);
        }
        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    private void runBamCoverage(ISGEnv isg) throws InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(NUM_THREADS);
        System.out.println("running bam coverage...");
        Collection<File> bamFiles = isg.getBams();
        for (File bamFile : bamFiles) {
            final File out = new File(isg.getCovDir(), getSampleName(bamFile) + ".interval_list");
            FindCoverageRunner runner = new FindCoverageRunner(bamFile, out, MIN_COVERAGE);
            es.submit(runner);
        }
        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
    }

    private void runISG(ISGEnv isg) {
        List<String> command = new ArrayList<String>();
        for(String sample: getSamples(isg)){
            command.add("SAMPLE="+sample);
        }
        command.add("COV_DIR="+isg.getCovDir().getAbsolutePath());
        command.add("REF="+isg.getRef());
        command.add("GBK_DIR="+isg.getGenBankDir());
        command.add("VCF_DIR="+isg.getVcfDir().getAbsolutePath());
        command.add("OUTPUT="+isg.getIsgOutFile().getAbsolutePath());
        command.add("MIN_QUAL=0");
        command.add("MIN_GQ="+MIN_QUAL);
        command.add("MIN_DP="+MIN_SNP_COVERAGE);
        ISG2 isg2 = new ISG2();
        isg2.instanceMain(command.toArray(new String[0]));
    }

    private void indexFastas(ISGEnv isg) {

        File fai = new File(isg.getRef().getAbsolutePath() + ".fai");
        if (!fai.exists()) {
            CreateFastaIndex cfi = new CreateFastaIndex();
            String[] argv = {"REFERENCE_SEQUENCE=" + isg.getRef().getAbsolutePath()};
            cfi.instanceMain(argv);
        }

    }

    private void createDictionary(ISGEnv isg) {
        File dict = isg.getRefDict();
        if (!dict.exists()) {
            CreateSequenceDictionary csd = new CreateSequenceDictionary();
            String[] argv = {"REFERENCE=" + isg.getRef().getAbsolutePath(),
                "OUTPUT=" + dict.getAbsolutePath(),};
            csd.instanceMain(argv);
        }
    }

    private List<String> getSamples(ISGEnv isg) {
        final List<String> ret = new ArrayList<String>();
        for (File bamFile : isg.getBams()) {
            ret.add(getSampleName(bamFile));
        }
        for (File fastaFile : isg.getFastas()) {
            String sampleName = MummerRunnerFactory.getFilePrefix(fastaFile);
            ret.add(sampleName);
        }
        return ret;
    }

    public static String getSampleName(File bam) {
        final SAMFileReader in = new SAMFileReader(bam);
        final SAMFileHeader header = in.getFileHeader();
        if (header.getReadGroups().isEmpty()) {
            return bam.getName().substring(0, bam.getName().lastIndexOf('.'));
        } else {
            final SAMReadGroupRecord rg = header.getReadGroups().get(0);
            return rg.getSample();
        }
    }

    @Override
    protected int doWork() {
        try {

            ISGEnv isg = new ISGEnv(ISG);

            if (!isg.exists()) {
                System.out.println("Creating ISG directory structure...");
                isg.create();
                System.out.println("Finished.");
                return 0;
            }

            isg.validate();
            MummerEnv mummerEnv = new MummerEnv(MUMMER, isg.getMummerDir());

            indexFastas(isg);
            createDictionary(isg);

            if (GATK == null) {
                runSolSNP(isg);
            } else {
                runUnifiedGenotyper(isg);
            }

            runMummer(isg, mummerEnv);

            //create dups
            System.out.println("creating dups...");
            String prefix = mummerEnv.getMumOutDir().getAbsolutePath() + "/ref";
            NucmerRunner nucmer = new NucmerRunner(prefix, isg.getRef(), isg.getRef(), false, mummerEnv);
            nucmer.run();
            File dups = new File(isg.getDupsDir().getAbsolutePath() + "/ref.interval_list");
            FindDupsRunner dupsRunner = new FindDupsRunner(isg.getCoordsDups(), dups, isg.getRef());
            dupsRunner.run();

            //find coverage
            System.out.println("finding coverage...");
            runBamCoverage(isg);

            //run isg
            System.out.println("running isg...");
            runISG(isg);

        } catch (Exception ex) {
            Logger.getLogger(ISGPipeline.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.exit(new ISGPipeline().instanceMain(args));
    }
}
