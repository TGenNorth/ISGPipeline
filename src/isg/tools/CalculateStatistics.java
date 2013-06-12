/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabReader;
import java.io.File;
import org.broadinstitute.sting.commandline.CommandLineProgram;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.utils.help.ApplicationDetails;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class CalculateStatistics extends CommandLineProgram {

    @Input(fullName = "input_file", shortName = "I", doc = "ISG Matrix file", required = true)
    public File input;

    @Output(fullName = "output_file", shortName = "O", doc = "Statistics file", required = true)
    public File output;// = new File("test/simple_out.stats.tab");

    public static void main(String[] args) {
        try {
            CalculateStatistics instance = new CalculateStatistics();
            start(instance, args);
            System.exit(CommandLineProgram.result); // todo -- this is a painful hack
        } catch (Throwable t) {
            exitSystemWithError(t);
        }
    }

    @Override
    protected int execute() throws Exception {
        
        final VariantContextTabReader reader = new VariantContextTabReader(input);
        final ISGMatrixStats stats = new ISGMatrixStats(reader.getHeader().getGenotypeNames());
        VariantContext record = null;
        while ((record = reader.nextRecord()) != null) {
            stats.add(record);
        }
        stats.writeToFile(output);
        
        return 0;
    }

    @Override
    protected ApplicationDetails getApplicationDetails() {
        return super.getApplicationDetails();
    }
    
    
}
