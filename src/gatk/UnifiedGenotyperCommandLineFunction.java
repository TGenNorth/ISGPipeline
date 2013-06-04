/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gatk;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Output;
import util.TypedProperties;
/**
 *
 * @author jbeckstrom
 */
public class UnifiedGenotyperCommandLineFunction extends GATKCommandLineFunction {
 
    public static final String COMMAND_PREFIX = "ug.";
    
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
    
    public enum HmmImplementation {
        EXACT,
        ORIGINAL,
        LOGLESS_CACHING;
    }
    
    @Output(doc = "vcf file")
    public File out;
    
    @Argument(doc = "out mode", required=false)
    public OutMode outMode;
    
    @Argument(doc = "Genotype likelihoods calculation model to employ -- SNP is the default option, while INDEL is also available for calling indels and BOTH is available for calling both together", required=false)
    public GenotypeLikelihoodsModel genotype_likelihoods_model;
    
    @Argument(doc = "Fraction of contamination in sequencing data (for all samples) to aggressively remove", required=false)
    public Double contamination;
    
    @Argument(doc = "Heterozygosity value used to compute prior likelihoods for any locus", required=false)
    public Double heterozygosity;
    
    @Argument(doc = "Heterozygosity for indel calling", required=false)
    public Double indelHeterozygosity;
    
    @Argument(doc = "Maximum fraction of reads with deletions spanning this locus for it to be callable [to disable, set to < 0 or > 1; default:0.05]", required=false)
    public Double maxDeletionFraction;
    
    @Argument(doc = "Minimum number of consensus indels required to trigger genotyping run", required=false)
    public Integer minIndelCnt;
    
    @Argument(doc = "Minimum fraction of all reads at a locus that must contain an indel (of any allele) for that sample to contribute to the indel count for alleles", required=false)
    public Double minIndelFrac;
    
    @Argument(doc = "The PairHMM implementation to use for -glm INDEL genotype likelihood calculations", required=false)
    public HmmImplementation pairHmmImpl;
    
    @Argument(doc = "The PCR error rate to be used for computing fragment-based likelihoods", required=false)
    public Double pcrErrorRate;
    
    @Argument(doc = "Indel gap continuation penalty, as Phred-scaled probability. I.e., 30 => 10^-30/10", required=false)
    public Byte indelGapContinuationPenalty;
    
    @Argument(doc = "Indel gap open penalty, as Phred-scaled probability. I.e., 30 => 10^-30/10", required=false)
    public Byte indelGapOpenPenalty;
    
    @Argument(doc = "out mode", required=false)
    public Integer stand_call_conf;
    
    @Argument(doc = "out mode", required=false)
    public Integer stand_emit_conf;
    
    @Argument(doc = "out mode", required=false)
    public Integer ploidy;
    
    @Argument(doc = "out mode", required=false)
    public Integer min_base_quality;

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
                + optional("--standard_min_confidence_threshold_for_calling", stand_call_conf, "", true, true, "%s")
                + optional("--standard_min_confidence_threshold_for_emitting", stand_emit_conf, "", true, true, "%s")
                + optional("--min_base_quality_score", min_base_quality, "", true, true, "%s")
                + optional("-ploidy", ploidy, "", true, true, "%s")
                + optional("-contamination", contamination, "", true, true, "%s")
                + optional("--heterozygosity", heterozygosity, "", true, true, "%s")
                + optional("--indel_heterozygosity", indelHeterozygosity, "", true, true, "%s")
                + optional("--max_deletion_fraction", maxDeletionFraction, "", true, true, "%s")
                + optional("-minIndelCnt", minIndelCnt, "", true, true, "%s")
                + optional("-minIndelFrac", minIndelFrac, "", true, true, "%s")
                + optional("--pair_hmm_implementation", pairHmmImpl, "", true, true, "%s")
                + optional("--pcr_error_rate", pcrErrorRate, "", true, true, "%s")
                + optional("--indelGapContinuationPenalty", indelGapContinuationPenalty, "", true, true, "%s")
                + optional("--indelGapOpenPenalty", indelGapOpenPenalty, "", true, true, "%s");
    }
    
}
