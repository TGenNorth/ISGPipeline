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

    @Argument(doc = "Query alignment using length*identity weighted LIS.")
    public Boolean q = false;
    
    @Argument(doc = "Reference alignment using length*identity weighted LIS.")
    public Boolean r = false;
    
    @Input(doc = "delta input file")
    public File inDelta;
    
    @Output(doc = "filtered delta output file")
    public File outDelta;

    @Override
    public String commandLine() {
        return required(new File(mummerDir, "delta-filter"))
                + conditional(r, "-r", true, "%s")
                + conditional(q, "-q", true, "%s")
                + required(inDelta)
                + required(">", false)
                + required(outDelta);
    }
}
