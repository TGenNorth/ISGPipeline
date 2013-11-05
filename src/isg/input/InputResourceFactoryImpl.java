/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import isg.util.SequenceFilePairPattern;
import isg.util.SequenceFilePairPatterns;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.broadinstitute.sting.utils.collections.Pair;
import org.broadinstitute.sting.utils.exceptions.UserException;
import util.FileUtils;
import util.GenomicFileUtils;

/**
 *
 * @author jbeckstrom
 */
public class InputResourceFactoryImpl {

    private final Set<File> alreadyUsedFastqs = new HashSet<File>();
    private final SequenceFilePairPatterns patterns;

    public InputResourceFactoryImpl(SequenceFilePairPatterns patterns) {
        this.patterns = patterns;
    }

    public InputResource<?> create(File f) throws IOException, UserException {
        return create(f, InputResourceType.determineType(f));
    }

    public InputResource<?> create(File f, InputResourceType type) throws IOException, UserException {
        switch (type) {
            case BAM:
                return BamInputResource.create(f);
            case FASTA:
                return FastaInputResource.create(f);
            case VCF:
                return VcfInputResource.create(f);
            case FASTQ:
                return createFastqInputResource(f);
            default:
                return null;
        }
    }

    private InputResource<?> createFastqInputResource(File fastq) throws UserException {
        if(alreadyUsedFastqs.contains(fastq)){
            return null;
        }
        SequenceFilePairPattern pattern = patterns.findPattern(fastq);
        if (pattern == null) {
            //single end
            final String sampleName = FileUtils.stripExtension(fastq);
            return new FastqInputResource(sampleName, fastq);
        } else {
            //try to find matching pairs
            final File fastq2 = pattern.other(fastq);
            alreadyUsedFastqs.add(fastq2);
            if (!fastq2.exists()) {
                throw new InputResourceValidationExceptions.MissingMatesFileException(fastq, fastq2);
            }
            final Pair<File, File> pair = createFastqPair(fastq, fastq2, pattern);
            final String sampleName = pattern.sample(fastq);
            return new FastqPairInputResource(sampleName, pair);
        }
    }

    private Pair<File, File> createFastqPair(File fastq1, File fastq2, SequenceFilePairPattern pattern) {
        if (pattern.isFirst(fastq2)) {
            return new Pair<File, File>(fastq2, fastq1);
        } else {
            return new Pair<File, File>(fastq1, fastq2);
        }
    }
}
