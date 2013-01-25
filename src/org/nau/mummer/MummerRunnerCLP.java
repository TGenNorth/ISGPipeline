package org.nau.mummer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.reference.FastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;


public class MummerRunnerCLP extends CommandLineProgram {

    // Usage and parameters
    @Usage(programVersion = "1.0")
    public String USAGE = "Program that runs mummer alignments on "
            + "multiple queries against a reference.";
    @Option(doc = "The reference FASTA file.", optional = true)
    public List<File> REF_FASTA_FILE = null;
    @Option(doc = "The query FASTA file.", minElements = 1)
    public List<File> QUERY_FASTA_FILE;
    @Option(doc = "Path to mummer.")
    public File MUMMER_DIR;
    @Option(doc = "Out directory.")
    public File OUT_DIR;
    @Option(doc = "Use the maxmatch option when running mummer.")
    public Boolean MAX_MATCH = false;
    @Option(doc = "Number of mummer processes to run in parallel.", optional = true)
    public int NUM_THREADS = 5;
    private MummerEnv env;

    @Override
    protected int doWork() {
        try {
            env = new MummerEnv(MUMMER_DIR, OUT_DIR);

            ExecutorService queue = Executors.newFixedThreadPool(NUM_THREADS);

            if (REF_FASTA_FILE == null || REF_FASTA_FILE.size()==0) {
                createPairwiseQueue(queue);
            } else {
                createReferenceQueue(queue);
            }

            queue.shutdown();
            queue.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

        } catch (InterruptedException ex) {
            Logger.getLogger(MummerRunnerCLP.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MummerRunnerCLP.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        
        return 0;
    }

    public void createPairwiseQueue(ExecutorService queue) {
        for (File q1 : QUERY_FASTA_FILE) {
            for (File q2 : QUERY_FASTA_FILE) {
                if(!q1.equals(q2)){
                    queue.submit(MummerRunnerFactory.createMummerRunner(q1, q2, MAX_MATCH, env));
                }
            }
        }
    }

    public void createReferenceQueue(ExecutorService queue) {
        for (File rFile : REF_FASTA_FILE) {
            for (File qFile : QUERY_FASTA_FILE) {
                queue.submit(MummerRunnerFactory.createMummerRunner(rFile, qFile, MAX_MATCH, env));
            }
        }
    }

    public static void main(String[] args) {
        System.exit(new MummerRunnerCLP().instanceMain(args));
    }
}
