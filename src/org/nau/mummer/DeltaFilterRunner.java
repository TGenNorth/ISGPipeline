package org.nau.mummer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import org.tgen.commons.utils.CommandFactory;
import org.tgen.commons.utils.ExternalProcess;

public class DeltaFilterRunner implements Runnable {

    private final File deltaFile;
    private final File filterFile;
    private final MummerEnv env;

    public DeltaFilterRunner(File deltaFile, File filterFile, MummerEnv env) {
        this.deltaFile = deltaFile;
        this.filterFile = filterFile;
        this.env = env;
    }

    public void run() {
        if (filterFile.exists() && filterFile.length() > 0) {
            System.out.println(filterFile.getPath()+" already exists.");
            return;
        }
        String[] cmd = CommandFactory.generateDeltaFilterCommand(env.getDeltaFilter(), deltaFile);
        ExternalProcess ep;
        try {
            ep = new ExternalProcess(cmd, new FileOutputStream(filterFile));
            ep.run();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
