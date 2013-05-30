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
public class CallableLociCommandLineFunction extends GATKCommandLineFunction {

    @Output(doc = "bed file")
    public File out;
    
    @Output(doc = "summary file")
    public File summary;
    
    @Argument(doc = "If the fraction of reads at a base with low mapping quality exceeds this value, the site may be poorly mapped", required=false)
    public Double frlmq;
    
    @Argument(doc = "Maximum read depth before a locus is considered poorly mapped", required=false)
    public Integer maxDepth;
    
    @Argument(doc = "Maximum value for MAPQ to be considered a problematic mapped read.", required=false)
    public Byte maxLowMAPQ;
    
    @Argument(doc = "Minimum quality of bases to count towards depth.", required=false)
    public Byte minBaseQuality;
    
    @Argument(doc = "Minimum mapping quality of reads to count towards depth.", required=false)
    public Byte minMappingQuality;
    
    @Argument(doc = "Minimum QC+ read depth before a locus is considered callable", required=false)
    public Integer minDepth;
    
    @Argument(doc = "Minimum read depth before a locus is considered a potential candidate for poorly mapped", required=false)
    public Integer minDepthForLowMAPQ;

    @Override
    public String analysisName() {
        return "CallableLoci";
    }

    @Override
    public String commandLine() {
        return super.commandLine() 
                + required("-o", out, "", true, true, "%s")
                + required("-summary", summary, "", true, true, "%s")
                + optional("-frlmq", frlmq, "", true, true, "%s")
                + optional("--maxDepth", maxDepth, "", true, true, "%s")
                + optional("--maxLowMAPQ", maxLowMAPQ, "", true, true, "%s")
                + optional("--minBaseQuality", minBaseQuality, "", true, true, "%s")
                + optional("--minMappingQuality", minMappingQuality, "", true, true, "%s")
                + optional("--minDepth", minDepth, "", true, true, "%s")
                + optional("--minDepthForLowMAPQ", minDepthForLowMAPQ, "", true, true, "%s");  
    }
    
}
