package org.nau.bwa;

import org.nau.mummer.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nau.util.ExternalProcess;
import org.tgen.commons.utils.CommandFactory;

public class SampeRunner implements Runnable {

    private final String bwa;
    private final String refPrefix;
    private final File read1;
    private final File read2;
    private final File sai1;
    private final File sai2;
    private final File sam;

    public SampeRunner(String bwa, String refPrefix, File read1, File read2, File sai1, File sai2, File sam) {
        this.bwa = bwa;
        this.refPrefix = refPrefix;
        this.read1 = read1;
        this.read2 = read2;
        this.sai1 = sai1;
        this.sai2 = sai2;
        this.sam = sam;
    }

    public boolean exists() {
        return (sam.exists() && sam.length() > 0);
    }

    private String[] createSampeCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("sampe");
        ret.add("-f");
        ret.add(sam.getAbsolutePath());
        ret.add(refPrefix);
        ret.add(sai1.getAbsolutePath());
        ret.add(sai2.getAbsolutePath());
        ret.add(read1.getAbsolutePath());
        ret.add(read2.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if (exists()) {
            System.out.println(sam.getAbsolutePath() + " already exists.");
            return;
        }
        String[] cmd = createSampeCommand();
        ExternalProcess.execute(cmd);
    }
}
