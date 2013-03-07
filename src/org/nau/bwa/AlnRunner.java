package org.nau.bwa;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sf.picard.fastq.FastqReader;
import net.sf.picard.util.FastqQualityFormat;
import net.sf.picard.util.QualityEncodingDetector;
import org.nau.util.ExternalProcess;

public class AlnRunner implements Runnable {

    private final String bwa;
    private final String refPrefix;
    private final File readFile;
    private final File saiFile;

    public AlnRunner(String bwa, String refPrefix, File readFile, File saiFile) {
        this.bwa = bwa;
        this.refPrefix = refPrefix;
        this.readFile = readFile;
        this.saiFile = saiFile;
    }

    public boolean exists() {
        return (saiFile.exists() && saiFile.length() > 0);
    }

    private String[] createAlnCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("aln");
        if(isIllumina()){
            ret.add("-I");
        }
        ret.add("-f");
        ret.add(saiFile.getAbsolutePath());
        ret.add(refPrefix);
        ret.add(readFile.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }
    
    private boolean isIllumina(){
        final FastqQualityFormat format = QualityEncodingDetector.detect(
                new FastqReader(readFile));
        switch(format){
            case Illumina:
                return true;
            case Solexa:
                throw new IllegalStateException("Solexa quality scores are not supported by bwa");
            case Standard:
                return false;
            default:
                throw new IllegalStateException("Could not determine quality for fastq");
        }
    }

    public void run() {
        if (exists()) {
            System.out.println(saiFile.getAbsolutePath() + " already exists.");
            return;
        }
        String[] cmd = createAlnCommand();
        ExternalProcess.execute(cmd);
    }
}
