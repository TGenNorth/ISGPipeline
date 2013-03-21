package org.nau.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sf.picard.sam.MarkDuplicates;

public class MarkDuplicatesRunner implements Runnable {

    private final File in;
    private final File out;
    private final File metrics;

    public MarkDuplicatesRunner(File in, File out, File metrics) {
        this.in = in;
        this.out = out;
        this.metrics = metrics;
    }

    private String[] getArgs() {
        List<String> ret = new ArrayList<String>();
        ret.add("I="+in.getAbsolutePath());
        ret.add("O="+out.getAbsolutePath());
        ret.add("METRICS_FILE="+metrics.getAbsolutePath());
        ret.add("REMOVE_DUPLICATES=true");
        ret.add("VALIDATION_STRINGENCY=SILENT");
//        ret.add("TMP_DIR="+tmpDir.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        MarkDuplicates program = new MarkDuplicates();
        program.instanceMain(getArgs());
    }
}
