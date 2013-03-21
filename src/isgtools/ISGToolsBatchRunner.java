/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.StandardOptionDefinitions;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.util.CollectionUtil;

/**
 *
 * @author jbeckstrom
 */
public class ISGToolsBatchRunner extends CommandLineProgram {

    public static enum Program {

        CalculateMismatch {

            @Override
            public CommandLineProgram makeInstance(final File input, final File output) {
                final CalculateMismatch program = new CalculateMismatch();
                program.INPUT = input;
                program.OUTPUT = output;
                return program;
            }
        },
        DetermineStatus {

            @Override
            public CommandLineProgram makeInstance(final File input, final File output) {
                final DetermineStatus program = new DetermineStatus();
                program.INPUT = input;
                program.OUTPUT = output;
                return program;
            }
        },
        CalculatePattern {

            @Override
            public CommandLineProgram makeInstance(final File input, final File output) {
                final CalculatePattern program = new CalculatePattern();
                program.INPUT = input;
                program.OUTPUT = output;
                return program;
            }
        };

        public abstract CommandLineProgram makeInstance(final File input, final File output);
    }
    @Usage
    public final String USAGE = "Takes an input matrix file and runs one or more ISGTools "
            + "modules sequentially for convenience.";
    @Option(shortName = StandardOptionDefinitions.INPUT_SHORT_NAME, doc = "Input matrix file.")
    public File INPUT = new File("test/simple.txt");
    @Option(shortName = StandardOptionDefinitions.OUTPUT_SHORT_NAME, doc = "Output matrix file.")
    public File OUTPUT = new File("test/simple.2.txt");;
    @Option
    public List<Program> PROGRAM = CollectionUtil.makeList(Program.values());

    // Stock main method
    public static void main(final String[] args) {
        new ISGToolsBatchRunner().instanceMainWithExit(args);
    }

    @Override
    protected int doWork() {
        
        File input = INPUT;
        File output = new File(UUID.randomUUID().toString());
        int count = 0;
        Set<Program> programs = new HashSet<Program>(PROGRAM);
        for (Program program : programs) {
            count++;
            final CommandLineProgram instance = program.makeInstance(input, output);

            //use reflection to access protected doWork method
            invokeDoWork(instance);

            if(count>1){
                input.delete();
            }
            
            input = output;
            output = createTempFile("isg");
            
            System.out.println(count+" "+programs.size());
            if(count>=programs.size()-1){
                output = OUTPUT;
            }
        }
        
        final CalculateStatistics program = new CalculateStatistics();
        program.INPUT = input;
        program.OUTPUT = new File(output.getAbsolutePath()+".stats");
        invokeDoWork(program);

        return 0;
    }

    private void invokeDoWork(final CommandLineProgram instance) {
        try {
            Method doWorkMethod = instance.getClass().getDeclaredMethod("doWork", null);
            doWorkMethod.setAccessible(true);
            doWorkMethod.invoke(instance, null);
        } catch (Exception ex) {
            throw new PicardException("Failed to invoke doWork method on instance of CommandLineProgram", ex);
        }
    }
    
    private File createTempFile(String prefix){
        try {
            return File.createTempFile(prefix, null, OUTPUT.getParentFile());
        } catch (IOException ex) {
            throw new IllegalStateException("An error occured creating temp file", ex);
        }
    }
}
