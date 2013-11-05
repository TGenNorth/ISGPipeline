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
public abstract class GATKCommandLineFunction extends AbstractJavaCommandLineFunction {

    @Argument(doc = "gatkJarFile", required = true)
    public File jarFile;
    
    @Input(doc = "input bam file")
    public File inputFile;
    
    @Input(doc = "reference file")
    public File referenceFile;
    
    @Argument(doc = "allowPotentiallyMisencodedQuals", required = false)
    public boolean allowPotentiallyMisencodedQuals = false;

    @Override
    public File jarFile() {
        return jarFile;
    }

    @Override
    public String commandLine() {
        return super.commandLine()
                + required("-T")
                + required(analysisName())
                + required("-R")
                + required(referenceFile)
                + required("-I")
                + required(inputFile)
                + conditional(allowPotentiallyMisencodedQuals, "-allowPotentiallyMisencodedQuals", true, "%s");
    }
}
