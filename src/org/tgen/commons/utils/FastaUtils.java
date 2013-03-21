package org.tgen.commons.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.sf.picard.reference.FastaSequenceFile;
import net.sf.picard.reference.ReferenceSequence;

public class FastaUtils {

    public static boolean hasMultipleSequences(File fasta) {
        FastaSequenceFile fastaSeqFile = new FastaSequenceFile(fasta, false);
        fastaSeqFile.nextSequence();
        return (fastaSeqFile.nextSequence() != null);
    }

    public static List<File> splitFasta(File fasta, File outDir) throws IOException {
        List<File> ret = new ArrayList<File>();
        FastaSequenceFile fastaSeqFile = new FastaSequenceFile(fasta, false);
        ReferenceSequence refSeq = null;
        while ((refSeq = fastaSeqFile.nextSequence()) != null) {
            File refSeqFile = new File(outDir.getAbsolutePath() + "/" + getFilename(refSeq));
            PrintWriter pw = new PrintWriter(new FileWriter(refSeqFile));
            pw.println(">"+refSeq.getName());
            pw.println(refSeq.getBases());
            pw.close();
            ret.add(refSeqFile);
        }
        return ret;
    }

    public static String getFilename(ReferenceSequence refSeq) {
        return refSeq.getName() + ".fa";
    }
}
