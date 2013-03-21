package org.nau.picard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.sf.picard.sam.AddOrReplaceReadGroups;

public class AddOrReplaceReadGroupsRunner implements Runnable {

    private final File in;
    private final File out;
    private final String sampleName;

    public AddOrReplaceReadGroupsRunner(File in, File out, String sampleName) {
        this.in = in;
        this.out = out;
        this.sampleName = sampleName;
    }

    private String[] getArgs() {
        List<String> ret = new ArrayList<String>();
        ret.add("INPUT="+in.getAbsolutePath());
        ret.add("OUTPUT="+out.getAbsolutePath());
        ret.add("SO=coordinate");
        ret.add("RGLB=.");
        ret.add("RGPL=.");
        ret.add("RGPU=.");
        ret.add("RGSM="+sampleName);
        ret.add("VALIDATION_STRINGENCY=SILENT");
//        ret.add("TMP_DIR="+tmpDir.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        AddOrReplaceReadGroups program = new AddOrReplaceReadGroups();
        program.instanceMain(getArgs());
    }
}
