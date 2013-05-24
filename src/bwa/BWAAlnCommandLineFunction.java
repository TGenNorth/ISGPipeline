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
    
    @Override
    public void freezeFieldValues() {
        super.freezeFieldValues();
        bwt = new File(prefix + ".bwt");
        amb = new File(prefix + ".amb");
        ann = new File(prefix + ".ann");
        pac = new File(prefix + ".pac");
        sa = new File(prefix + ".sa");
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
                + optional("-f")
                + optional(saiFile)
                + required(prefix)
                + required(fastqFile);
    }
}
