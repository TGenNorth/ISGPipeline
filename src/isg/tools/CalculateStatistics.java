/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import isg.matrix.VariantContextTabReader;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import org.broadinstitute.variant.variantcontext.VariantContext;

/**
 *
 * @author jbeckstrom
 */
public class CalculateStatistics extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Calculate statistics on an ISG Matrix.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;// = new File("test/simple.tab");
    @Option(doc = "Output statistics file", optional = false)
    public File OUTPUT;// = new File("test/simple_out.stats.tab");

    @Override
    protected int doWork() {
        try {
            VariantContextTabReader reader = new VariantContextTabReader(INPUT);
            ISGMatrixStats stats = new ISGMatrixStats(reader.getHeader().getAttributeKeys());
            VariantContext record = null;
            while( (record = reader.nextRecord()) != null ){
                stats.add(record);
            }
            stats.writeToFile(OUTPUT);
        } catch (Exception ex) {
            Logger.getLogger(CalculateStatistics.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }
        return 0;
    }
    
    public static void main(String[] args) {
        System.exit(new CalculateStatistics().instanceMain(args));
    }
}
