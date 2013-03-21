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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.nau.util.ExternalProcess;
import org.nau.util.ProcessOutputHandlerFileWriter;
import org.tgen.commons.utils.CommandFactory;

public class ShowSnpsRunner implements Runnable {

    private final File deltaFile;
    private final File snpsFile;
    private final MummerEnv env;
    private final boolean sortByQuery;

    public ShowSnpsRunner(File deltaFile, File snpsFile, MummerEnv env) {
        this(deltaFile, snpsFile, false, env);
    }
    
    public ShowSnpsRunner(File deltaFile, File snpsFile, boolean sortByQuery, MummerEnv env) {
        this.deltaFile = deltaFile;
        this.snpsFile = snpsFile;
        this.sortByQuery = sortByQuery;
        this.env = env;
    }
    
    private String[] createShowSnpsCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getShowSnps().getAbsolutePath());
        ret.add("-lTH");
        ret.add("-x");
        ret.add("10");
        ret.add(sortByQuery ? "-q" : "-r");
        ret.add(deltaFile.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if (snpsFile.exists() && snpsFile.length() > 0) {
            System.out.println(snpsFile.getPath()+" already exists.");
            return;
        }
        String[] cmd = createShowSnpsCommand();
        try {
            ExternalProcess.execute(cmd, null, new ProcessOutputHandlerFileWriter(snpsFile));
        } catch (IOException ex) {
            Logger.getLogger(ShowSnpsRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
