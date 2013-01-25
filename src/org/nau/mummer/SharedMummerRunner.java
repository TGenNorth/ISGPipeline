/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import java.io.File;
import java.io.FileNotFoundException;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import org.nau.mummer.findcommon.FindCommonCoords;
import org.nau.mummer.findcommon.FindCommonSnps;

/**
 * Takes a reference fasta and query fasta.
 * run mummer on ref vs. query
 * run mummer on query vs. ref
 * compare coords for shared regions
 * compare snps for ambiguous and real
 * @author jbeckstrom
 */
public class SharedMummerRunner implements Runnable {

    private File refFasta;
    private File queryFasta;
    private MummerEnv env;
    private File coords1, coords2, coordsSelf;
    private File snps1, snps2;
    private File outputVcf, intervalFile;
    private String sampleName;
    
    public SharedMummerRunner(File refFasta, File queryFasta, File outputVcf, File intervalFile, MummerEnv env){
        this.refFasta = refFasta;
        this.queryFasta = queryFasta;
        this.env = env;
        this.outputVcf = outputVcf;
        this.intervalFile = intervalFile;
        String prefix1 = MummerRunnerFactory.getAbsolutePrefix(env.getMumOutDir(), queryFasta, refFasta);
        String prefix2 = MummerRunnerFactory.getAbsolutePrefix(env.getMumOutDir(), refFasta, queryFasta);
        String prefix3 = MummerRunnerFactory.getAbsolutePrefix(env.getMumOutDir(), queryFasta, queryFasta);
        coords1 = new File(prefix1+".coords");
        coords2 = new File(prefix2+".coords");
        coordsSelf = new File(prefix3+".coords");
        snps1 = new File(prefix1+".snps");
        snps2 = new File(prefix2+".snps");
        this.sampleName = MummerRunnerFactory.getFilePrefix(queryFasta);
    }
    
    @Override
    public void run() {
        ReferenceSequenceFile refSeq = ReferenceSequenceFileFactory.getReferenceSequenceFile(refFasta);
        MummerRunnerFactory.createMummerRunner(refFasta, queryFasta, true, env).run();
        MummerRunnerFactory.createMummerRunner(queryFasta, refFasta, true, env).run();
        MummerRunnerFactory.createNucmerRunner(queryFasta, queryFasta, true, env).run();
        new MumSnpToVcf(snps1, snps2, outputVcf, refSeq.getSequenceDictionary(), sampleName).run();
        new FindCommonCoords(coords2, coords1, refSeq.getSequenceDictionary(), intervalFile).run();
    }
    
}
