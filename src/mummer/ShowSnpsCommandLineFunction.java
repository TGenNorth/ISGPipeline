/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;

/**
 *
 * @author jbeckstrom
 */
public class ShowSnpsCommandLineFunction extends MummerCommandLineFunction {

    @Argument(doc = "report SNPs from alignments with an ambiguous mapping")
    public Boolean showAmbiguous = false;
    
    @Argument(doc = "report indels")
    public Boolean showIndels = false;
    
    @Argument(doc = "print the output header")
    public Boolean printHeader = true;
    
    @Argument(doc = "Switch to tab-delimited format")
    public Boolean tabDelimit = true;
    
    @Argument(doc = "Include sequence length information in the output")
    public Boolean showSeqLen = true;
    
    @Argument(doc = "Sort output lines by query IDs and SNP positions")
    public Boolean sortByQuery = false;
    
    @Argument(doc = "Include x characters of surrounding SNP context in the output")
    public Integer flankLen = 10;
    
    @Input(doc = "delta input file")
    public File deltaFile;
    
    @Output(doc = "snps output file")
    public File snpsFile;

    @Override
    public String commandLine() {
        return required(new File(mummerDir, "show-snps"))
                + conditional(!showAmbiguous, "-C", true, "%s")
                + conditional(!showIndels, "-I", true, "%s")
                + conditional(printHeader, "-H", true, "%s")
                + conditional(tabDelimit, "-T", true, "%s")
                + conditional(showSeqLen, "-l", true, "%s")
                + conditional(flankLen > 0, flankLen, true, "-x %d")
                + optional((sortByQuery ? "-q" : "-r"))
                + required(deltaFile)
                + required(">", false)
                + required(snpsFile);
    }
    
}
