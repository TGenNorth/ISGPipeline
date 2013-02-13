package org.nau.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sf.picard.sam.BuildBamIndex;

public class BuildBamIndexRunner implements Runnable {

    private final File in;
    private final File out;

    public BuildBamIndexRunner(File in, File out) {
        this.in = in;
        this.out = out;
    }

    private String[] getArgs() {
        List<String> ret = new ArrayList<String>();
        ret.add("I="+in.getAbsolutePath());
        ret.add("O="+out.getAbsolutePath());
        ret.add("VALIDATION_STRINGENCY=SILENT");
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        BuildBamIndex program = new BuildBamIndex();
        program.instanceMain(getArgs());
    }
}
