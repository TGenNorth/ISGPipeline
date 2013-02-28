/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import net.sf.picard.util.CigarUtil;
import net.sf.samtools.Cigar;
import net.sf.samtools.CigarElement;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileHeader.SortOrder;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFileWriter;
import net.sf.samtools.SAMFileWriterFactory;
import net.sf.samtools.SAMReadGroupRecord;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMSequenceRecord;
import net.sf.samtools.SAMTag;

/**
 *
 * @author jbeckstrom
 */
public class ProcessOutputHandlerSamFileWriter implements ProcessOutputHandler {

    private final File samOrBamFile;
    private final String RGID;
    private final String RGLB;
    private final String RGPL;
    private final String RGPU;
    private final String RGSM;
    private SAMFileReader samFileReader;

    protected ProcessOutputHandlerSamFileWriter(File samOrBamFile, String RGID, String RGLB, String RGPL, String RGPU, String RGSM) {
        this.samOrBamFile = samOrBamFile;
        this.RGID = RGID;
        this.RGLB = RGLB;
        this.RGPL = RGPL;
        this.RGPU = RGPU;
        this.RGSM = RGSM;
    }

    @Override
    public void setInputStream(InputStream is) {
        samFileReader = new SAMFileReader(is);
    }

    @Override
    public void run() {
        final SAMReadGroupRecord rg = new SAMReadGroupRecord(RGID);
        rg.setLibrary(RGLB);
        rg.setPlatform(RGPL);
        rg.setSample(RGSM);
        rg.setPlatformUnit(RGPU);

        samFileReader.setValidationStringency(SAMFileReader.ValidationStringency.SILENT);
        final SAMFileHeader inHeader = samFileReader.getFileHeader();
        final SAMFileHeader outHeader = inHeader.clone();
        outHeader.setReadGroups(Arrays.asList(rg));
        outHeader.setSortOrder(SortOrder.coordinate);

        final SAMFileWriter outWriter = new SAMFileWriterFactory().makeSAMOrBAMWriter(outHeader,
                outHeader.getSortOrder() == inHeader.getSortOrder(),
                samOrBamFile);
        for (final SAMRecord rec : samFileReader) {
            rec.setAttribute(SAMTag.RG.name(), RGID);
            if (!rec.getReadUnmappedFlag()) {
                final SAMSequenceRecord refseq = rec.getHeader().getSequence(rec.getReferenceIndex());
                if (rec.getAlignmentEnd() > refseq.getSequenceLength()) {
                    // 1-based index of first base in read to clip.

                    final int clipFrom = refseq.getSequenceLength() - rec.getAlignmentStart() + 1;
                    final List<CigarElement> newCigarElements = CigarUtil.softClipEndOfRead(clipFrom, rec.getCigar().getCigarElements());
                    rec.setCigar(new Cigar(newCigarElements));
                }
            } else if (rec.getMappingQuality() != 0) {
                rec.setMappingQuality(0);
            }
            outWriter.addAlignment(rec);
        }

        samFileReader.close();
        outWriter.close();
    }
}
