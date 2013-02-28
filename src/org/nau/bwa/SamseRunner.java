package org.nau.bwa;

import org.nau.mummer.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nau.util.ExternalProcess;
import org.nau.util.ProcessOutputHandlerSamFileWriterBuilder;
import org.tgen.commons.utils.CommandFactory;

public class SamseRunner implements Runnable {

    private final String bwa;
    private final String refPrefix;
    private final File fq;
    private final File sai;
    private final File sam;
    private final String sampleName;

    public SamseRunner(String bwa, String refPrefix, File fq, File sai, File sam, String sampleName) {
        this.bwa = bwa;
        this.refPrefix = refPrefix;
        this.fq = fq;
        this.sai = sai;
        this.sam = sam;
        this.sampleName = sampleName;
    }

    public boolean exists() {
        return (sam.exists() && sam.length() > 0);
    }

    private String[] createSamseCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("samse");
        ret.add(refPrefix);
        ret.add(sai.getAbsolutePath());
        ret.add(fq.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if (exists()) {
            System.out.println(sam.getAbsolutePath() + " already exists.");
            return;
        }
        ProcessOutputHandlerSamFileWriterBuilder bldr = new ProcessOutputHandlerSamFileWriterBuilder(sam);
        bldr.RGSM(sampleName);
        String[] cmd = createSamseCommand();
        ExternalProcess.execute(cmd, null, bldr.make());
    }
}
