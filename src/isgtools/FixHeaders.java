/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools;

import isgtools.io.ISGMatrixReader;
import isgtools.io.ISGMatrixWriter;
import isgtools.model.ISGMatrixHeader;
import isgtools.model.ISGMatrixRecord;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;

/**
 *
 * @author jbeckstrom
 */
public class FixHeaders extends CommandLineProgram {

    @Usage(programVersion = "0.1")
    public String USAGE = "Fix headers produced by ISGPipeline. Currently, only removes the '.bam' extension from header names";
    @Option(doc = "ISG matrix file", optional = false)
    public File INPUT;
    @Option(doc = "Output matrix file with fixed headers.", optional = false)
    public File OUTPUT;

    @Override
    protected int doWork() {

        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsWritable(OUTPUT);

        ISGMatrixReader reader = openMatrixForReading(INPUT);
        ISGMatrixWriter writer = openMatrixForWriting(OUTPUT);

        ISGMatrixHeader header = reader.getHeader();
        ISGMatrixHeader fixedHeader = fixHeader(header);

        writer.writerHeader(fixedHeader);
        writeRecords(reader, writer);

        return 0;
    }

    private void writeRecords(ISGMatrixReader reader, ISGMatrixWriter writer) {
        ISGMatrixRecord record = null;
        while ((record = reader.nextRecord()) != null) {
            writer.addRecord(record);
        }
        writer.close();
    }

    private ISGMatrixHeader fixHeader(ISGMatrixHeader header) {
        List<String> fixedSampleNames = new ArrayList<String>();
        for (String sampleName : header.getSampleNames()) {
            fixedSampleNames.add(fixName(sampleName));
        }
        return new ISGMatrixHeader(fixedSampleNames, header.getAdditionalInfo());
    }

    private String fixName(String sampleName) {
        int index = sampleName.lastIndexOf(".bam");
        if (index != -1) {
            return sampleName.substring(0, index);
            
        }
        return sampleName;
    }

    private ISGMatrixWriter openMatrixForWriting(File file) {
        try {
            return new ISGMatrixWriter(file);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private ISGMatrixReader openMatrixForReading(File file) {
        try {
            return new ISGMatrixReader(file);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static void main(String[] args) {
        System.exit(new FixHeaders().instanceMain(args));
    }
}
