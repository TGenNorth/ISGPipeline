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

public class ShowSnpsRunner implements Runnable {

    private final File deltaFile;
    private final File snpsFile;
    private final boolean contigs;
    private final MummerEnv env;

    public ShowSnpsRunner(File deltaFile, File snpsFile, boolean contigs, MummerEnv env) {
        this.contigs = contigs;
        this.deltaFile = deltaFile;
        this.snpsFile = snpsFile;
        this.env = env;
    }

    public void run() {
        if (snpsFile.exists() && snpsFile.length() > 0) {
            System.out.println(snpsFile.getPath()+" already exists.");
            return;
        }
        String[] cmd = CommandFactory.generateShowSnpsCommand(env.getShowSnps(), deltaFile, contigs);
        ExternalProcess ep;
        try {
            ep = new ExternalProcess(cmd, new FileOutputStream(snpsFile));
            ep.run();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
