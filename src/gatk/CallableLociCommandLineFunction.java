/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import java.io.File;
import org.broadinstitute.sting.commandline.Output;
/**
 *
 * @author jbeckstrom
 */
public class CallableLociCommandLineFunction extends GATKCommandLineFunction {

    @Output(doc = "bed file")
    public File out;
    
    @Output(doc = "summary file")
    public File summary;

    @Override
    public String analysisName() {
        return "CallableLoci";
    }

    @Override
    public String commandLine() {
        return super.commandLine() 
                + required("-o", out, "", true, true, "%s")
                + required("-summary", summary, "", true, true, "%s");
                
    }
    
}
