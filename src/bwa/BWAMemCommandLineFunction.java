/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;

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
    
    @Override
    public String commandLine() {
        return required(bwa)
                + required("mem")
                + conditional(M, "-M", true, "%s")
                + required(prefix)
                + required(readsFile)
                + optional(matesFile)
                + required(">", false)
                + required(samFile);
    }
}
