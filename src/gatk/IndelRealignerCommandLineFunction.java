/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
/**
 *
 * @author jbeckstrom
 */
public class IndelRealignerCommandLineFunction extends GATKCommandLineFunction {

    @Output(doc = "output bam file")
    public File out;
    
    @Input(doc = "target intervals input file")
    public File targetIntervals;
    
    @Argument(required=false)
    public Double LODThresholdForCleaning;
    
    @Argument(required=false)
    public Double entropyThreshold;
    
    @Argument(required=false)
    public Integer maxConsensuses;
    
    @Argument(required=false)
    public Integer maxIsizeForMovement;
    
    @Argument(required=false)
    public Integer maxPositionalMoveAllowed;
    
    @Argument(required=false)
    public Integer maxReadsForConsensuses;
    
    @Argument(required=false)
    public Integer maxReadsForRealignment;
    
    @Argument(required=false)
    public Integer maxReadsInMemory;
    
    @Argument(required=false)
    public Boolean noOriginalAlignmentTags;

    @Override
    public String analysisName() {
        return "IndelRealigner";
    }

    @Override
    public String commandLine() {
        return super.commandLine() 
                + required("--out", out, "", true, true, "%s")
                + required("--targetIntervals", targetIntervals, "", true, true, "%s")
                + optional("--LODThresholdForCleaning", LODThresholdForCleaning, "", true, true, "%s")
                + optional("--entropyThreshold", entropyThreshold, "", true, true, "%s")
                + optional("--maxConsensuses", maxConsensuses, "", true, true, "%s")
                + optional("--maxIsizeForMovement", maxIsizeForMovement, "", true, true, "%s")
                + optional("--maxPositionalMoveAllowed", maxPositionalMoveAllowed, "", true, true, "%s")
                + optional("--maxReadsForConsensuses", maxReadsForConsensuses, "", true, true, "%s")
                + optional("--maxReadsForRealignment", maxReadsForRealignment, "", true, true, "%s")
                + optional("--maxReadsInMemory", maxReadsInMemory, "", true, true, "%s")
                + optional("--noOriginalAlignmentTags", noOriginalAlignmentTags, "", true, true, "%s");  
    }
    
}
