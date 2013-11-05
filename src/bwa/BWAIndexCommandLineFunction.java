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
public class BWAIndexCommandLineFunction extends BWACommandLineFunction {

    @Input(doc = "fasta file to index")
    public File fastaFile;
    
    @Output(doc = "bwt file to output", required=false)
    public File bwt;
    
    @Output(doc = "amb file to output", required=false)
    public File amb;
    
    @Output(doc = "ann file to output", required=false)
    public File ann;
    
    @Output(doc = "pac file to output", required=false)
    public File pac;
    
    @Output(doc = "sa file to output", required=false)
    public File sa;
    

    @Override
    public void freezeFieldValues() {
        bwt = new File(fastaFile.getPath() + ".bwt");
        amb = new File(fastaFile.getPath() + ".amb");
        ann = new File(fastaFile.getPath() + ".ann");
        pac = new File(fastaFile.getPath() + ".pac");
        sa = new File(fastaFile.getPath() + ".sa");
        super.freezeFieldValues();
    }
    
    @Override
    public String commandLine() {
        return required(bwa)
                + required("index")
                + required(fastaFile);
    }
}
