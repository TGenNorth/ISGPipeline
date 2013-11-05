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
    
    @Argument(doc = "Use anchor matches that are unique in both the reference and query", required=false)
    public Boolean mum = false;

    @Argument(doc = "Use all anchor matches regardless of their uniqueness")
    public Boolean maxmatch = false;
    
    @Argument(required=false)
    public Integer breaklen;
    
    @Argument(required=false)
    public Integer mincluster;
    
    @Argument(required=false)
    public Float diagfactor;
    
    @Argument(required=false)
    public Boolean forward = false;
    
    @Argument(required=false)
    public Integer maxgap;
    
    @Argument(required=false)
    public Integer minmatch;
    
    @Argument(required=false)
    public Boolean nooptimize = false;
    
    @Argument(required=false)
    public Boolean reverse = false;
    
    @Argument(doc = "generate coords")
    public Boolean coords = false;
    
    @Argument(doc = "Simplify alignments by removing shadowed clusters.")
    public Boolean nosimplify = false;

    @Override
    public String analysisName() {
        return "nucmer";
    }
    
    @Override
    public void freezeFieldValues() {
        deltaFile = new File(prefix + ".delta");
        if (coords) {
            coordsFile = new File(prefix + ".coords");
        }
        super.freezeFieldValues();
    }

    @Override
    public String commandLine() {
        return required(new File(mummerDir, "nucmer"))
                + conditional(mum, "--mum", true, "%s")
                + conditional(maxmatch, "--maxmatch", true, "%s")
                + optional("--breaklen", breaklen, "", true, true, "%s")
                + optional("--mincluster", mincluster, "", true, true, "%s")
                + optional("--diagfactor", diagfactor, "", true, true, "%s")
                + conditional(forward, "--forward", true, "%s")
                + optional("--maxgap", maxgap, "", true, true, "%s")
                + optional("--minmatch", minmatch, "", true, true, "%s")
                + conditional(coords, "--coords", true, "%s")
                + conditional(nooptimize, "--nooptimize", true, "%s")
                + conditional(prefix != null, prefix, true, "--prefix=%s")
                + conditional(reverse, "--reverse", true, "%s")
                + conditional(nosimplify, "--nosimplify", true, "%s")
                + required(refFasta)
                + required(qryFasta);
    }
}
