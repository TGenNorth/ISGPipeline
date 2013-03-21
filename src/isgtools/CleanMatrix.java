/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import org.nau.isg.matrix.ISGMatrixReader;
import org.nau.isg.matrix.ISGMatrixWriter;
import org.nau.isg.matrix.ISGMatrixRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;

/**
 *
 * @author jbeckstrom
 */
public class CleanMatrix extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Clean ISG Matrix file.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;
    @Option(doc = "Output prefix", optional = false)
    public String OUTPUT_PREFIX;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new CleanMatrix().instanceMain(args));
    }

    @Override
    protected int doWork() {
        try {
            
            ISGMatrixReader reader = new ISGMatrixReader(INPUT);
            ISGMatrixWriter cleanWriter = new ISGMatrixWriter(new File(OUTPUT_PREFIX + ".clean.tab"));
            ISGMatrixWriter uncleanWriter = new ISGMatrixWriter(new File(OUTPUT_PREFIX + ".unclean.tab"));
            
            cleanWriter.writerHeader(reader.getHeader());
            uncleanWriter.writerHeader(reader.getHeader());
            
            ISGMatrixRecord record = null;

            while ((record = reader.nextRecord()) != null) {

                boolean clean = true;
                for(Character state: record.getStates()){
                    if(state.charValue() == '.' || state.charValue() == 'N'){
                        clean = false;
                        break;
                    }
                }

                if(clean){
                    cleanWriter.addRecord(record);
                }else{
                    uncleanWriter.addRecord(record);
                }
            }

            cleanWriter.close();
            uncleanWriter.close();

        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CleanMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CleanMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
}
