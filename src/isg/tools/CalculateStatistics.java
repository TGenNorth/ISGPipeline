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
 * A tool to calculates statistics on a SNP matrix file. These statistics are 
 * calculated per sample and include counts and percentages of four possible 
 * calls found in a matrix. Namely, SNP, no coverage, ambiguous, or reference. 
 * The percentage is calculated by taking the count divided by the total number 
 * of records in the matrix. The statistics can be useful in assessing overall 
 * quality of an ISG run and pinpointing which genomes should be removed from the 
 * analysis. For instance, a high percentage of ambiguous calls can indicate a 
 * bad genome that should be removed. Similarly, a high 
 * percentage of no coverage calls can indicate a genome that should be removed 
 * due to inadequate coverage.
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
            ISGMatrixStats stats = new ISGMatrixStats(reader.getHeader().getGenotypeNames());
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
