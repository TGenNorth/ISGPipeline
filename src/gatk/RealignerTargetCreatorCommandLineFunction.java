/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Output;
/**
 *
 * @author jbeckstrom
 */
public class RealignerTargetCreatorCommandLineFunction extends GATKCommandLineFunction {

    @Output(doc = "target intervals output file")
    public File out;
    
    @Argument(required=false)
    public Integer maxIntervalSize;
    
    @Argument(required=false)
    public Integer minReadsAtLocus;
    
    @Argument(required=false)
    public Double mismatchFraction;
    
    @Argument(required=false)
    public Integer windowSize;

    @Override
    public String analysisName() {
        return "RealignerTargetCreator";
    }

    @Override
    public String commandLine() {
        return super.commandLine() 
                + required("-o", out, "", true, true, "%s")
                + optional("--maxIntervalSize", maxIntervalSize, "", true, true, "%s")
                + optional("--minReadsAtLocus", minReadsAtLocus, "", true, true, "%s")
                + optional("--mismatchFraction", mismatchFraction, "", true, true, "%s")
                + optional("--windowSize", windowSize, "", true, true, "%s");  
    }
    
}
