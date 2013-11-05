/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.StandardOptionDefinitions;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.FastLineReader;
import net.sf.picard.io.IoUtil;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.util.CloserUtil;
import net.sf.samtools.util.StringUtil;


/**
 *
 * @author jbeckstrom
 */
public class CreateFastaIndex extends CommandLineProgram {

    @Usage
    public final String USAGE = "Creates a fasta index for the specified sequence file. The index"
            + "file will be created using the input sequence filename appended with .fai";
    @Option(shortName = StandardOptionDefinitions.REFERENCE_SHORT_NAME, doc = "Reference sequence file.")
    public File REFERENCE_SEQUENCE = new File("test.fasta");
    private final static int BUFFER_SIZE = 5000;
    private final byte[] basesBuffer = new byte[BUFFER_SIZE];
    private int numBytes = 0;

    public static void main(final String[] args) {
        new CreateFastaIndex().instanceMainWithExit(args);
    }

    @Override
    protected int doWork() {
        IoUtil.assertFileIsReadable(REFERENCE_SEQUENCE);

        PrintWriter pw = null;
        FastLineReader reader = null;
        try {
            reader = new FastLineReader(new FileInputStream(REFERENCE_SEQUENCE));
            pw = new PrintWriter(new FileWriter(new File(REFERENCE_SEQUENCE.getAbsolutePath() + ".fai")));
            while (!reader.eof()) {
                String name = readSequenceName(reader);
                int location = numBytes;
                SequenceStats seqStats = getSequenceStats(reader);
                pw.println(name + "\t" + seqStats.getSize() + "\t" + location + "\t" + seqStats.getBasesPerLine() + "\t" + seqStats.getBytesPerLine());
            }
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CreateFastaIndex.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CreateFastaIndex.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            CloserUtil.close(reader);
            CloserUtil.close(pw);
        }
        return 0;
    }

    private String readSequenceName(final FastLineReader in) {
        skipNewLines(in);
        if (in.eof()) {
            return null;
        }
        final byte[] nameBuffer = readToEoln(in);
        if (nameBuffer[0] != '>') {
            throw new PicardException("Format exception reading FASTA " + REFERENCE_SEQUENCE + ".  Expected > but saw chr("
                    + nameBuffer[0] + ") at start of sequence");
        }
        if (nameBuffer.length <= 1) {
            throw new PicardException("Missing sequence name in FASTA " + REFERENCE_SEQUENCE);
        }
        String name = StringUtil.bytesToString(nameBuffer, 1, nameBuffer.length - 1).trim();
        return SAMSequenceRecord.truncateSequenceName(name);
    }

    private SequenceStats getSequenceStats(final FastLineReader in) {
        long totalSeqLen = 0;
        int bytesPerLine = 0;
        int basesPerLine = 0;
        boolean diffLineLen = false;
        while (!in.eof()) {
            skipNewLines(in);
            if (in.eof() || in.peekByte() == '>') {
                break;
            }
            if(diffLineLen){
                throw new IllegalStateException("different line length");
            }
            byte[] bases = readToEoln(in);
            int bytesOnLine = bases.length+1;
            int basesOnLine = bases.length;
            //don't count whitespaces in bases count
            while (basesOnLine > 0 && Character.isWhitespace(StringUtil.byteToChar(bases[basesOnLine - 1]))) {
                --basesOnLine;
            }
            
            if (bytesPerLine==0) {
                bytesPerLine = bytesOnLine;
            }else if(bytesPerLine!=bytesOnLine){
                diffLineLen = true;
            }
            if(basesPerLine==0){
                basesPerLine = basesOnLine;
            }
            totalSeqLen += basesOnLine;
        }
        return new SequenceStats(totalSeqLen, basesPerLine, bytesPerLine);
    }

    private byte[] readToEoln(final FastLineReader in) {
        byte[] line = basesBuffer;

        int lineLength = 0;
        while (!in.eof()) {
            final boolean sawEoln = skipNewLines(in);
            if (in.eof() || sawEoln) {
                break;
            }
            lineLength += in.readToEndOfOutputBufferOrEoln(line, lineLength);
            if (lineLength == line.length) {
                final byte[] tmp = new byte[line.length * 2];
                System.arraycopy(line, 0, tmp, 0, lineLength);
                line = tmp;
            }
        }
        numBytes += lineLength;
        // And lastly resize the array down to the right size
        if (lineLength != line.length) {
            final byte[] tmp = new byte[lineLength];
            System.arraycopy(line, 0, tmp, 0, lineLength);
            line = tmp;
        }
        return line;
    }

    private boolean skipNewLines(final FastLineReader in) {
        boolean ret = false;
        while (!in.eof() && in.atEoln()) {
            numBytes++;
            in.getByte();
            ret = true;
        }
        return ret;
    }
}
class SequenceStats {

    private final long size;
    private final int basesPerLine;
    private final int bytesPerLine;

    public SequenceStats(long size,
            int basesPerLine,
            int bytesPerLine) {
        this.size = size;
        this.basesPerLine = basesPerLine;
        this.bytesPerLine = bytesPerLine;
    }

    public int getBasesPerLine() {
        return basesPerLine;
    }

    public int getBytesPerLine() {
        return bytesPerLine;
    }

    public long getSize() {
        return size;
    }
}
