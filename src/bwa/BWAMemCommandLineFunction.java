/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import util.InterleavedFastqDetector;

/**
 *
 * @author jbeckstrom
 */
public class BWAMemCommandLineFunction extends BWACommandLineFunction {
    
    @Argument(doc="reference prefix")
    public String prefix;
    
    @Input(doc = "bwt file to output", required=false)
    public File bwt;
    
    @Input(doc = "amb file to output", required=false)
    public File amb;
    
    @Input(doc = "ann file to output", required=false)
    public File ann;
    
    @Input(doc = "pac file to output", required=false)
    public File pac;
    
    @Input(doc = "sa file to output", required=false)
    public File sa;

    @Input(required=true)
    public File readsFile;
    
    @Input(required=false)
    public File matesFile;
    
    @Output(required=true)
    public File samFile;
    
    @Argument
    public Integer t = null;
    
    @Argument
    public Integer k = null;
    
    @Argument
    public Integer w = null;
    
    @Argument
    public Integer d = null;
    
    @Argument
    public Float r = null;
    
    @Argument
    public Integer c = null;
    
    @Argument
    public Boolean P = false;
    
    @Argument
    public Integer A = null;
    
    @Argument
    public Integer B = null;
    
    @Argument
    public Integer O = null;
    
    @Argument
    public Integer E = null;
    
    @Argument
    public Integer L = null;
    
    @Argument
    public Integer U = null;
    
    @Argument
    public Integer T = null;
    
    @Argument
    public Boolean a = false;
    
    @Argument
    public Boolean H = false;
    
    @Argument
    public Boolean M = false;
    
    @Override
    public String analysisName() {
        return "bwa.mem";
    }
    
    @Override
    public void freezeFieldValues() {
        bwt = new File(prefix + ".bwt");
        amb = new File(prefix + ".amb");
        ann = new File(prefix + ".ann");
        pac = new File(prefix + ".pac");
        sa = new File(prefix + ".sa");
        super.freezeFieldValues();
    }
    
    private boolean isInterleaved(){
        if(matesFile!=null){
            return false;
        }
        return InterleavedFastqDetector.isInterleaved(readsFile);
    }
    
    @Override
    public String commandLine() {
        return required(bwa)
                + required("mem")
                + optional("-t", t, "", true, true, "%s")
                + optional("-k", k, "", true, true, "%s")
                + optional("-w", w, "", true, true, "%s")
                + optional("-d", d, "", true, true, "%s")
                + optional("-r", r, "", true, true, "%s")
                + optional("-c", c, "", true, true, "%s")
                + conditional(P, "-P", true, "%s")
                + optional("-A", A, "", true, true, "%s")
                + optional("-B", B, "", true, true, "%s")
                + optional("-O", O, "", true, true, "%s")
                + optional("-E", E, "", true, true, "%s")
                + optional("-L", L, "", true, true, "%s")
                + optional("-U", U, "", true, true, "%s")
                + optional("-T", T, "", true, true, "%s")
                + conditional(isInterleaved(), "-p", true, "%s")
                + conditional(a, "-a", true, "%s")
                + conditional(H, "-H", true, "%s")
                + conditional(M, "-M", true, "%s")
                + required(prefix)
                + required(readsFile)
                + optional(matesFile)
                + required(">", false)
                + required(samFile);
    }
}
