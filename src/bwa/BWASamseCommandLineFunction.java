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
public class BWASamseCommandLineFunction extends BWACommandLineFunction {

    @Argument(doc = "reference prefix")
    public String prefix;
    
    @Input(doc = "input sai")
    public File sai;
    
    @Input(doc = "input fastq")
    public File fq;
    
    @Output(doc = "output sam file")
    public File sam;
    

    @Override
    public String commandLine() {
        return required(bwa)
                + required("samse")
                + optional("-f")
                + optional(sam)
                + required(prefix)
                + required(sai)
                + required(fq);
    }
}
