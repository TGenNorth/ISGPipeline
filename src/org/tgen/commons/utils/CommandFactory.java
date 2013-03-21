package org.tgen.commons.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CommandFactory {

    private CommandFactory() {
    }

    public static String[] generateSolSnpCommand(File solsnp, File input, File output, File referenceSequence) {
        return new String[]{
                    "java",
                    "-jar",
                    solsnp.getAbsolutePath(),
                    "INPUT=" + input.getAbsolutePath(),
                    "OUTPUT=" + output.getAbsolutePath(),
                    "REFERENCE_SEQUENCE=" + referenceSequence.getAbsolutePath()};
    }

    public static String[] generateNucmerCommand(File nucmer, String prefix, File fasta1, File fasta2) {
        return new String[]{
                    nucmer.getAbsolutePath(),
                    "-o",
                    "-prefix=" + prefix,
                    fasta1.getAbsolutePath(),
                    fasta2.getAbsolutePath()};
    }

    public static String[] generateMaxmatchNucmerCommand(File nucmer, String prefix, File fasta1, File fasta2) {
        return new String[]{
                    nucmer.getAbsolutePath(),
                    "--maxmatch",
                    "--delta",
                    "--coords",
                    "-p",
                    prefix,
                    fasta1.getAbsolutePath(),
                    fasta2.getAbsolutePath()};
    }
    
    public static String[] generateVelvetHCommand(File velveth, String directory, String fileFormat, String readType, String filePath) {
        ///tnorth/sbeckstr/bin/velvet_1.2.03/velveth k57_mappedOnly 57  -shortPaired -fastq CbP26_unmappedMates_mappedOnly.fastq
        return new String[]{
                    velveth.getAbsolutePath(),
                    directory,
                    readType,
                    fileFormat,
                    filePath};
    }
    
    public static String[] generateVelvetGCommand(File velvetg, String directory, String minContigLength, String insLength, String insLengthSd) {
        //tnorth/sbeckstr/bin/velvet_1.2.03/velvetg k57_mappedOnly/ -cov_cutoff auto -min_contig_lgth 100 -ins_length 480 -ins_length_sd 25% -read_trkg yes -amos_file yes -exp_cov auto -scaffolding yes
return new String[]{
                    velvetg.getAbsolutePath(),
                    "-cov_cutoff",
                    "auto",
                    "-min_contig_lgth",
                    minContigLength,
                    "-ins_length",
                    insLength,
                    "-ins_length_sd",
                    insLengthSd,
                    "read_trkg",
                    "yes",
                    "-exp_cov",
                    "auto"
                    };
    }

    public static String[] generateShowSnpsCommand(File showsnps, File delta, boolean contigs) {
        return new String[]{
                    showsnps.getAbsolutePath(),
                    contigs ? "-lrTH" : "-ClrTH",
                    "-x",
                    "100",
                    delta.getAbsolutePath()};
    }
    
    public static String[] generateDeltaFilterCommand(File deltafilter, File delta) {
        return new String[]{
                    deltafilter.getAbsolutePath(),
                    "-r",
                    "-q",
                    delta.getAbsolutePath()};
    }

    public static List<String> createSff2FastqCommand(File sff2fastq, File sff, File out) {
        List<String> ret = new ArrayList<String>();
        ret.add(sff2fastq.getAbsolutePath());
        ret.add("-o");
        ret.add(out.getAbsolutePath());
        ret.add(sff.getAbsolutePath());
        return ret;
    }
}
