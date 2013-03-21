/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import isgtools.io.ISGMatrixReader;
import isgtools.io.ISGMatrixWriter;
import isgtools.model.ISGMatrixRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;

/**
 *
 * @author jbeckstrom
 */
public class FormatForTree extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Convert ISG SNP Matrix into a format for use by a phylogenetic tree program.";
    @Option(doc = "ISG Matrix file", optional = false)
    public File INPUT;
    @Option(doc = "Output fasta", optional = false)
    public File OUTPUT;
    @Option(doc = "Output format", optional = false)
    public FORMAT format = FORMAT.fasta;
    @Option(doc = "If 'true' ambiguous base calls will be given an 'N' otherwise"
            + " the reference base is assumed", optional = false)
    public boolean AMBIGUOUS = true;
    @Option(doc = "If 'true' base calls without any coverage will be given an '.' otherwise"
            + " the reference base is assumed", optional = false)
    public boolean NO_COVERAGE = true;
    
    enum FORMAT{
        fasta
    };

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new FormatForTree().instanceMain(args));
    }

    @Override
    protected int doWork() {
        try {
            
            ISGMatrixReader reader = new ISGMatrixReader(INPUT);
            List<String> sampleNames = reader.getHeader().getSampleNames();
            Converter converter = new FastaConverter();
            
            ISGMatrixRecord record = null;

            while ((record = reader.nextRecord()) != null) {

                for(int i=0; i<record.getNStates(); i++){
                    char state = record.getState(i);
                    String sample = sampleNames.get(i);
                    if( (state == '.' && !NO_COVERAGE) || (state == 'N' && !AMBIGUOUS) ){
                        state = record.getRef();
                    }
                    converter.append(sample, state);
                }

            }
            
            converter.toFile(OUTPUT);

        
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FormatForTree.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FormatForTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return 0;
    }
    
    private interface Converter{
        
        public void append(String sample, char base);
        
        public Set<String> getSamples();
        
        public String getSequence(String sample);
        
        public void toFile(File f);
        
    }
    
    private class FastaConverter implements Converter{

        private Map<String, StringBuilder> samples = new HashMap<String, StringBuilder>();
        
        @Override
        public void append(String sample, char base) {
            StringBuilder sb = samples.get(sample);
            if(sb==null){
                sb = new StringBuilder();
                samples.put(sample, sb);
            }
            sb.append(base);
        }

        @Override
        public Set<String> getSamples() {
            return samples.keySet();
        }

        @Override
        public String getSequence(String sample) {
            StringBuilder sb = samples.get(sample);
            if(sb==null){
                throw new IllegalArgumentException("Could not find sample: "+sample);
            }
            return sb.toString();
        }

        @Override
        public void toFile(File f){
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new FileWriter(f));
                for(String sample: getSamples()){
                    pw.println(">"+sample);
                    pw.println(getSequence(sample));
                }
            } catch (IOException ex) {
                Logger.getLogger(FormatForTree.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                pw.close();
            }
        }
        
    }
}
