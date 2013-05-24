/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer;

import java.io.File;
import org.broadinstitute.sting.commandline.Argument;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.queue.extensions.samtools.SamtoolsCommandLineFunction;
/**
 *
 * @author jbeckstrom
 */
public abstract class MummerCommandLineFunction extends SamtoolsCommandLineFunction {

    @Argument(doc = "mummer path")
    public File mummerDir;
    
}
