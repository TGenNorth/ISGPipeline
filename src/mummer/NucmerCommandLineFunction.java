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
public class NucmerCommandLineFunction extends MummerCommandLineFunction {

    @Input(doc = "reference fasta file")
    public File refFasta;
    
    @Input(doc = "query fasta file")
    public File qryFasta;
    
    @Output(doc = "delta output file")
    public File deltaFile;
    
    @Output(doc = "coords output file", required=false)
    public File coordsFile;
    
    @Argument(doc = "reference prefix")
    public String prefix;
    
    @Argument(doc = "generate coords")
    public Boolean showCoords = true;
    
    @Argument(doc = "Use all anchor matches regardless of their uniqueness")
    public Boolean maxmatch = false;
    
    @Argument(doc = "Simplify alignments by removing shadowed clusters.")
    public Boolean simplify = true;

    @Override
    public String analysisName() {
        return "nucmer";
    }
    
    @Override
    public void freezeFieldValues() {
        deltaFile = new File(prefix + ".delta");
        if (showCoords) {
            coordsFile = new File(prefix + ".coords");
        }
        super.freezeFieldValues();
    }

    @Override
    public String commandLine() {
        return required(new File(mummerDir, "nucmer"))
                + conditional(showCoords, "--coords", true, "%s")
                + conditional(maxmatch, "--maxmatch", true, "%s")
                + conditional(!simplify, "--nosimplify", true, "%s")
                + conditional(prefix != null, prefix, true, "--prefix=%s")
                + required(refFasta)
                + required(qryFasta);
    }
}
