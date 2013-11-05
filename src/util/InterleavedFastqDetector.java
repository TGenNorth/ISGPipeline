/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import fastq.SequenceIdentifier;
import fastq.SequenceIdentifierParser;
import fastq.SequenceIdentifierUtil;
import java.io.File;
import java.util.Iterator;
import net.sf.picard.fastq.FastqReader;
import net.sf.picard.fastq.FastqRecord;

/**
 *
 * @author jbeckstrom
 */
public class InterleavedFastqDetector {
    
    public static boolean isInterleaved(File fastqFile) {
        return isInterleaved(new FastqReader(fastqFile));
    }

    /**
     * Reads first two records from FastqReader to determine if underlying file 
     * is interleaved. Closes the reader when done.
     * 
     * @param reader
     * @return 
     */
    public static boolean isInterleaved(FastqReader reader) {
        boolean ret = isInterleaved(reader.iterator());
        reader.close();
        return ret;
    }
    
    public static boolean isInterleaved(Iterator<FastqRecord> iter) {
        SequenceIdentifierParser parser = null;
        int firstPairNumber = -1;
        int secondPairNumber = -1;
        
        if(iter.hasNext()){
            FastqRecord record = iter.next();
            parser = SequenceIdentifierUtil.determineParser(record.getReadHeader());
            firstPairNumber = parser.parse(record.getReadHeader()).getPairNumber();
        }
        if(iter.hasNext()){
            FastqRecord record = iter.next();
            secondPairNumber = parser.parse(record.getReadHeader()).getPairNumber();
        }
        
        return (firstPairNumber==1 && secondPairNumber==2);
    }
}
