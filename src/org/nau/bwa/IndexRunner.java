package org.nau.bwa;

import org.nau.mummer.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nau.util.ExternalProcess;
import org.nau.util.FileUtils;
import org.tgen.commons.utils.CommandFactory;

public class IndexRunner implements Runnable {

    private final String bwa;
    private final File fasta;
    private final String prefix;
    private final File out;

    public IndexRunner(String bwa, File fasta, String prefix) {
        this.bwa = bwa;
        this.fasta = fasta;
        this.prefix = prefix;
        out = new File(prefix+".sa");
    }

    private String[] createAlnCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(bwa);
        ret.add("index");
        ret.add("-p");
        ret.add(prefix);
        ret.add(fasta.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if(FileUtils.exists(out)){
            System.out.printf("%s is already indexed.", fasta.getName());
            return;
        }
        String[] cmd = createAlnCommand();
        ExternalProcess.execute(cmd);
    }
}
