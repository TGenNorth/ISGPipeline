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

public class DeltaFilterRunner implements Runnable {

    private final File deltaFile;
    private final File filterFile;
    private final MummerEnv env;

    public DeltaFilterRunner(File deltaFile, File filterFile, MummerEnv env) {
        this.deltaFile = deltaFile;
        this.filterFile = filterFile;
        this.env = env;
    }
    
    private String[] createDeltaFilterCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getDeltaFilter().getAbsolutePath());
        ret.add("-r");
        ret.add("-q");
        ret.add(deltaFile.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if (filterFile.exists() && filterFile.length() > 0) {
            System.out.println(filterFile.getPath()+" already exists.");
            return;
        }
        String[] cmd = createDeltaFilterCommand();
        try {
            ExternalProcess.execute(cmd, null, new ProcessOutputHandlerFileWriter(filterFile));
        } catch (IOException ex) {
            Logger.getLogger(DeltaFilterRunner.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
