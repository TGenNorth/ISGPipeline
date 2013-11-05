/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bwa;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.queue.extensions.samtools.SamtoolsCommandLineFunction;

/**
 *
 * @author jbeckstrom
 */
public class BWASampeCommandLineFunction extends BWACommandLineFunction {

    @Argument(doc = "reference prefix")
    public String prefix;
    
    @Input(doc = "input R1 sai")
    public File sai1;
    
    @Input(doc = "input R2 sai")
    public File sai2;
    
    @Input(doc = "input R1 fastq")
    public File fq1;
    
    @Input(doc = "input R2 fastq")
    public File fq2;
    
    @Output(doc = "output sam file")
    public File sam;
    
    @Argument(required=false)
    public Integer a;
    
    @Argument(required=false)
    public Integer o;
    
    @Argument(required=false)
    public Integer n;
    
    @Argument(required=false)
    public Integer N;
    
    @Argument(required=false)
    public Float c;
    
    @Argument(required=false)
    public Boolean P = false;
    
    @Argument(required=false)
    public Boolean s = false;
    
    @Argument(required=false)
    public Boolean A = false;
    
    @Override
    public String analysisName() {
        return "bwa.sampe";
    }

    @Override
    public String commandLine() {
        return required(bwa)
                + required("sampe")
                + optional("-a", a, "", true, true, "%s")
                + optional("-o", o, "", true, true, "%s")
                + optional("-n", n, "", true, true, "%s")
                + optional("-N", N, "", true, true, "%s")
                + optional("-c", c, "", true, true, "%s")
                + conditional(P, "-P", true, "%s")
                + conditional(s, "-s", true, "%s")
                + conditional(A, "-A", true, "%s")
                + optional("-f")
                + optional(sam)
                + required(prefix)
                + required(sai1)
                + required(sai2)
                + required(fq1)
                + required(fq2);
    }
}
