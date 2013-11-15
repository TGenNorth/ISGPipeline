/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.tools;

import util.TranslationTable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.samtools.util.StringUtil;

/**
 *
 * @author jbeckstrom
 */
public class RenameChr extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Renames the CHROM column of a vcf";
    @Option(doc = "Input VCF file", optional = false)
    public File INPUT;
    @Option(doc = "Output VCF file", optional = false)
    public File OUTPUT;
    @Option(doc = "Chromosome name translation table", optional = false)
    public File TRANSLATION_TABLE;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.exit(new RenameChr().instanceMain(args));
    }

    @Override
    protected int doWork() {
        PrintWriter pw = null;
        try {
            final TranslationTable chrNameTranslationTable = TranslationTable.load(TRANSLATION_TABLE);
            pw = new PrintWriter(new FileWriter(OUTPUT));
            BufferedReader reader = new BufferedReader(new FileReader(INPUT));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if(line.startsWith("#")){
                    pw.println(line);
                    continue;
                }
                String[] split = line.split("\t");
                if(split.length<8){
                    continue;
                }
                final String chr = split[0];
                final String newChr = chrNameTranslationTable.translate(chr);

                split[0] = newChr;
                pw.println(StringUtil.join("\t", split));
            }
            return 0;
        } catch (IOException ex) {
            throw new RuntimeException("An IO error occurred", ex);
        } finally {
            pw.close();
        }
    }
}
