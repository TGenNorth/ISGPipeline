package org.nau.mummer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nau.util.ExternalProcess;
import org.tgen.commons.utils.CommandFactory;

public class ShowSnpsRunner implements Runnable {

    private final File deltaFile;
    private final File snpsFile;
    private final MummerEnv env;

    public ShowSnpsRunner(File deltaFile, File snpsFile, MummerEnv env) {
        this.deltaFile = deltaFile;
        this.snpsFile = snpsFile;
        this.env = env;
    }
    
    private String[] createShowSnpsCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getShowSnps().getAbsolutePath());
        ret.add("-lrTHI");
        ret.add("-x");
        ret.add("10");
        ret.add(deltaFile.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if (snpsFile.exists() && snpsFile.length() > 0) {
            System.out.println(snpsFile.getPath()+" already exists.");
            return;
        }
        String[] cmd = createShowSnpsCommand();
        ExternalProcess.execute(cmd, null, snpsFile);
    }
}
