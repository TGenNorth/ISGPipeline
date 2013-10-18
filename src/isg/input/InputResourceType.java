/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public enum InputResourceType {

    BAM(".bam"),
    FASTQ(".fastq", ".fastq.gz", ".fq", ".fq.gz", "sequence.txt", "sequence.txt.gz"),
    VCF(".vcf"),
    FASTA(".fasta", ".fa");

    private InputResourceType(String... extensions) {
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }
    private final String[] extensions;

    /**
     * Determine the type of file based on file extension.
     * 
     * @param f
     * @return 
     */
    public static InputResourceType determineType(File f) {
        String filename = f.getName();
        for(InputResourceType type: InputResourceType.values()){
            if(filenameEndsWith(filename, type.getExtensions())){
                return type;
            }
        }
        throw new IllegalStateException("Cannot determine type of file: "+f);
    }

    public static boolean filenameEndsWith(String filename, String[] extensions) {
        for (String extension : extensions) {
            if (filename.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
