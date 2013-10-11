/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

/**
 *
 * @author jbeckstrom
 */
public enum InputResourceType {
    BAM(".bam"),
    FASTQ(".fastq", ".fastq.gz", "sequence.txt", "sequence.txt.gz"),
    VCF(".vcf"),
    FASTA(".fasta", ".fa"),
    GENBANK(".gb", ".gbk");
    
    private InputResourceType(String... extensions){
        this.extensions = extensions;
    }

    public String[] getExtensions() {
        return extensions;
    }
    
    private String[] extensions;
}
