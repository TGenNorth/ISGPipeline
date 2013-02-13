package org.nau.bwa;

import org.nau.mummer.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nau.util.ExternalProcess;
import org.tgen.commons.utils.CommandFactory;

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
        ret.add("-f");
        ret.add(saiFile.getAbsolutePath());
        ret.add(refPrefix);
        ret.add(readFile.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
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
