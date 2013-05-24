/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import mummer.*;
import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.queue.extensions.samtools.SamtoolsCommandLineFunction;
import org.broadinstitute.sting.queue.function.AbstractJavaCommandLineFunction;
/**
 *
 * @author jbeckstrom
 */
public class UnifiedGenotyperCommandLineFunction extends GATKCommandLineFunction {

    public enum OutMode {
        EMIT_VARIANTS_ONLY,
        EMIT_ALL_CONFIDENT_SITES,
        EMIT_ALL_SITES;
    }
    
    public enum GenotypeLikelihoodsModel {
        SNP,
        INDEL,
        GENERALPLOIDYSNP,
        GENERALPLOIDYINDEL,
        BOTH;
    }
    
    @Output(doc = "vcf file")
    public File out;
    
    @Argument(doc = "out mode", required=false)
    public OutMode outMode;
    
    @Argument(doc = "out mode", required=false)
    public GenotypeLikelihoodsModel genotype_likelihoods_model;
    
    @Argument(doc = "out mode", required=false)
    public int stand_call_conf = -1;
    
    @Argument(doc = "out mode", required=false)
    public int stand_emit_conf = -1;
    
    @Argument(doc = "out mode", required=false)
    public int ploidy;
    
    @Argument(doc = "out mode", required=false)
    public int min_base_quality = -1;

    @Override
    public String analysisName() {
        return "UnifiedGenotyper";
    }

    @Override
    public String commandLine() {
        return super.commandLine() 
                + required("-o", out, "", true, true, "%s")
                + optional("--output_mode", outMode, "", true, true, "%s")
                + optional("--genotype_likelihoods_model", genotype_likelihoods_model, "", true, true, "%s")
                + optional("--standard_min_confidence_threshold_for_calling", (stand_call_conf >= 0 ? stand_call_conf : null), "", true, true, "%s")
                + optional("--standard_min_confidence_threshold_for_emitting", (stand_emit_conf >= 0 ? stand_emit_conf : null), "", true, true, "%s")
                + optional("--min_base_quality_score", (min_base_quality >= 0 ? min_base_quality : null), "", true, true, "%s")
                + optional("-ploidy", (ploidy > 0 ? ploidy : null), "", true, true, "%s");
    }
    
}
