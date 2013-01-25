package org.nau.mummer;

import java.io.File;

import net.sf.picard.reference.FastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;
import org.tgen.commons.utils.FastaUtils;

public class MummerRunnerFactory {

    private MummerRunnerFactory() {
    }
    
    public static MummerRunner createMummerRunner(File rFile, File qFile, boolean maxmatch, MummerEnv env) {
        String prefix = getAbsolutePrefix(env.getMumOutDir(), qFile, rFile);
        return createMummerRunner(rFile, qFile, maxmatch, env, prefix);
    }
    
    public static MummerRunner createMummerRunner(File rFile, File qFile, boolean maxmatch, MummerEnv env, String prefix) {
        NucmerRunner nucmer = new NucmerRunner(prefix, rFile, qFile, maxmatch, env);
        DeltaFilterRunner deltaFilter = createDeltaFilterRunner(prefix, env);
        ShowSnpsRunner showSnps = createShowSnpsRunner(prefix, env, hasContigs(qFile));
        return new MummerRunner(nucmer, deltaFilter, showSnps);
    }
    
    public static ShowSnpsRunner createShowSnpsRunner(String prefix, MummerEnv env, boolean contigs) {
        File deltaFile = new File(prefix + ".filter");
        File snpsFile = new File(prefix + ".snps");
        return new ShowSnpsRunner(deltaFile, snpsFile, contigs, env);
    }
    
    public static DeltaFilterRunner createDeltaFilterRunner(String prefix, MummerEnv env) {
        File deltaFile = new File(prefix + ".delta");
        File filterFile = new File(prefix + ".filter");
        return new DeltaFilterRunner(deltaFile, filterFile, env);
    }

    public static NucmerRunner createNucmerRunner(File rFile, File qFile, boolean maxmatch, MummerEnv env) {
        String prefix = getAbsolutePrefix(env.getMumOutDir(), qFile, rFile);
        return new NucmerRunner(prefix, rFile, qFile, maxmatch, env);
    }

    public static boolean hasContigs(File fastaFile) {
        return fastaFile.getName().contains("contigs");
    }

    public static File getDeltaFile(File outDir, File qFile, File rFile) {
        return new File(getAbsolutePrefix(outDir, qFile, rFile) + ".delta");
    }

    public static File getSnpsFile(File outDir, File qFile, File rFile) {
        return new File(getAbsolutePrefix(outDir, qFile, rFile) + ".snps");
    }
    
    public static File getCoordsFile(File outDir, File qFile, File rFile) {
        return new File(getAbsolutePrefix(outDir, qFile, rFile) + ".coords");
    }

    public static String getAbsolutePrefix(File outDir, File qFile, File rFile) {
        return outDir.getAbsolutePath() + "/" + getMummerPrefix(qFile, rFile);
    }
    
    public static String getMummerPrefix(File qFile, File rFile){
        return getFilePrefix(qFile) + "_vs_" + getFilePrefix(rFile);
    }
    
    public static String getFilePrefix(File f) {
        return f.getName().substring(0, f.getName().indexOf('.'));
    }
}
