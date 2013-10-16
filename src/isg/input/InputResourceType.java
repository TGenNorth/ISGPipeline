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
    BAM,
    FASTQ,
    VCF,
    FASTA;

    private InputResourceType() {
    }

    /**
     * Determine the type of file based on file extension.
     * 
     * @param f
     * @return 
     */
    public static InputResourceType determineType(File f) {
        final String filename = f.getName();
        if (filename.endsWith(".bam")) {
            return InputResourceType.BAM;
        } else if (filename.endsWith(".fastq")
                || filename.endsWith(".fastq.gz")
                || filename.endsWith("sequence.txt")
                || filename.endsWith("sequence.txt.gz")) {
            return InputResourceType.FASTQ;
        } else if (filename.endsWith(".vcf")) {
            return InputResourceType.VCF;
        } else if (filename.endsWith(".fasta")
                || filename.endsWith(".fa")){
            return InputResourceType.FASTA;
        } else {
            throw new IllegalStateException("Unsupported file type: "+filename);
        }
    }

}
