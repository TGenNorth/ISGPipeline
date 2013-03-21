/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.mummer.delta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.samtools.util.SequenceUtil;

/**
 *
 * @author jbeckstrom
 */
public class DeltaFileReader {

    private File file;
    private BufferedReader reader;
    private String line;
    private String refName, qryName;

    public DeltaFileReader(File file) throws FileNotFoundException {
        reader = new BufferedReader(new FileReader(file));
        this.file = file;
        init();
    }

    private void init() {
        while (true) {
            advanceLine();
            if (line == null || line.startsWith(">")) {
                break;
            }
        }
    }

    private void advanceLine() {
        try {
            line = reader.readLine();
//            System.out.println(line);
        } catch (IOException ex) {
            line = null;
        }
    }

    public DeltaAlignment nextAlignment() {
        if (line == null) {
            return null;
        } else if (line.startsWith(">")) {
            readSequenceHeader();
            advanceLine();
        }

        DeltaAlignmentBuilder b = readAlignmentHeader();

        while (true) {
            advanceLine();
            int dist = Integer.parseInt(line);
            b.appendOffset(dist);
            if (dist == 0) {
                break;
            }
        }
        advanceLine();

        return b.build();
    }

    private DeltaAlignmentBuilder readAlignmentHeader() {
        String[] split = line.split("\\s");
        int refStart = Integer.parseInt(split[0]);
        int refEnd = Integer.parseInt(split[1]);
        int queryStart = Integer.parseInt(split[2]);
        int queryEnd = Integer.parseInt(split[3]);
        return new DeltaAlignmentBuilder(refName, refStart, refEnd, qryName, queryStart, queryEnd);
    }

    private void readSequenceHeader() {
        String str = line.substring(1);
        String[] split = str.split("\\s");
        refName = split[0];
        qryName = split[1];
    }

    public static void main(String[] args) throws FileNotFoundException {
        File f = new File("test/MSHR1655_vs_ref.filter");
        DeltaFileReader reader = new DeltaFileReader(f);
        ReferenceSequenceFile ref = ReferenceSequenceFileFactory.getReferenceSequenceFile(
                new File("test/ref.fasta"));
        ReferenceSequenceFile qry = ReferenceSequenceFileFactory.getReferenceSequenceFile(
                new File("test/MSHR1655.fasta"));
        byte[] refSeq = ref.nextSequence().getBases();
        byte[] qrySeq = qry.nextSequence().getBases();

        DeltaAlignment alignment = null;
        while ((alignment = reader.nextAlignment()) != null) {
            List<DeltaMatch> refMatches = alignment.getRefMatches();
            List<DeltaMatch> qryMatches = alignment.getQryMatches();
            int matchCount = 0;
            int totalCount = 0;
            if (!alignment.isReverse()) {
                continue;
            }
            for (int i = 0; i < refMatches.size(); i++) {
                DeltaMatch rm = refMatches.get(i);
                DeltaMatch qm = qryMatches.get(i);
                int ri = alignment.getRefStart() + rm.getStart() - 1;
                int qi = alignment.getQryStart() - qm.getStart() - 1;

                for (int j = 0; j < rm.length(); j++) {
                    if (refSeq[ri] == SequenceUtil.complement(qrySeq[qi])) {
                        matchCount++;
                    }
                    qi--;
                    ri++;
                    totalCount++;
                }
            }
            double percent = 100 * ((double) matchCount / totalCount);
            System.out.println(percent + "%");

        }

    }
}
