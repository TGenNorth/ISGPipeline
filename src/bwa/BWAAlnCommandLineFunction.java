/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import net.sf.picard.fastq.FastqReader;
import net.sf.picard.util.FastqQualityFormat;
import net.sf.picard.util.QualityEncodingDetector;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.queue.extensions.samtools.SamtoolsCommandLineFunction;

/**
 *
 * @author jbeckstrom
 */
public class BWAAlnCommandLineFunction extends BWACommandLineFunction {
    
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

    @Input(doc="FASTQ file to align")
    public File fastqFile;
    
    @Output(doc="sai file to output")
    public File saiFile;
    
    @Argument(required=false)
    public FastqQualityFormat qualFormat = null;
    
    @Argument(required=false)
    public Double n;
    
    @Argument(required=false)
    public Integer o;
    
    @Argument(required=false)
    public Integer e;
    
    @Argument(required=false)
    public Integer i;
    
    @Argument(required=false)
    public Integer d;
    
    @Argument(required=false)
    public Integer l;
    
    @Argument(required=false)
    public Integer k;
    
    @Argument(required=false)
    public Integer m;
    
    @Argument(required=false)
    public Integer t;
    
    @Argument(required=false)
    public Integer M;
    
    @Argument(required=false)
    public Integer O;
    
    @Argument(required=false)
    public Integer E;
    
    @Argument(required=false)
    public Integer R;
    
    @Argument(required=false)
    public Integer q;
    
    @Argument(required=false)
    public Integer B;
    
    @Argument(required=false)
    public Boolean L = false;
    
    @Argument(required=false)
    public Boolean N = false;

    @Argument(required=false)
    public Boolean Y = false;
    
    @Override
    public String analysisName() {
        return "bwa.aln";
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
    
    /**
     * Test for Illumina 1.3+ quality score format
     * 
     * @return true if Illumina 1.3+, false otherwise
     */
    private boolean isIllumina(){
        if(qualFormat==null){
            qualFormat = QualityEncodingDetector.detect(
                new FastqReader(fastqFile));
        }
        return qualFormat==FastqQualityFormat.Illumina;
    }
    
    @Override
    public String commandLine() {
        return required(bwa)
                + required("aln")
                + conditional(isIllumina(), "-I", true, "%s")
                + optional("-n", n, "", true, true, "%s")
                + optional("-o", o, "", true, true, "%s")
                + optional("-e", e, "", true, true, "%s")
                + optional("-i", i, "", true, true, "%s")
                + optional("-d", d, "", true, true, "%s")
                + optional("-l", l, "", true, true, "%s")
                + optional("-k", k, "", true, true, "%s")
                + optional("-m", m, "", true, true, "%s")
                + optional("-t", t, "", true, true, "%s")
                + optional("-M", M, "", true, true, "%s")
                + optional("-O", O, "", true, true, "%s")
                + optional("-E", E, "", true, true, "%s")
                + optional("-R", R, "", true, true, "%s")
                + optional("-q", q, "", true, true, "%s")
                + optional("-B", B, "", true, true, "%s")
                + conditional(L, "-L", true, "%s")
                + conditional(N, "-N", true, "%s")
                + conditional(Y, "-Y", true, "%s")
                + optional("-f")
                + optional(saiFile)
                + required(prefix)
                + required(fastqFile);
    }
}
