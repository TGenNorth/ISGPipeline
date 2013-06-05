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
public class DeltaFilterCommandLineFunction extends MummerCommandLineFunction {

    @Input(doc = "delta input file")
    public File inDelta;
    
    @Output(doc = "filtered delta output file")
    public File outDelta;
    
    @Argument
    public Boolean g = false;
    
    @Argument(required=false)
    public Byte i;
    
    @Argument(required=false)
    public Integer l;
    
    @Argument(doc = "Query alignment using length*identity weighted LIS.")
    public Boolean q = false;
    
    @Argument(doc = "Reference alignment using length*identity weighted LIS.")
    public Boolean r = false;
    
    @Argument(required=false)
    public Byte u;
    
    @Argument(required=false)
    public Byte o;
    
    @Override
    public String analysisName() {
        return "delta-filter";
    }
    
    @Override
    public String commandLine() {
        return required(new File(mummerDir, "delta-filter"))
                + conditional(g, "-g", true, "%s")
                + optional("-i", i, "", true, true, "%s")
                + optional("-l", l, "", true, true, "%s")
                + conditional(q, "-q", true, "%s")
                + conditional(r, "-r", true, "%s")
                + optional("-u", u, "", true, true, "%s")
                + optional("-o", o, "", true, true, "%s")
                + required(inDelta)
                + required(">", false)
                + required(outDelta);
    }
}
