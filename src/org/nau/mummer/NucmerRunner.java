package org.nau.mummer;

import java.io.File;
import java.util.Arrays;
import org.tgen.commons.utils.CommandFactory;
import org.tgen.commons.utils.ExternalProcess;

public class NucmerRunner implements Runnable {

    private final String prefix;
    private final File fasta1;
    private final File fasta2;
    private final File coordsFile;
    private final File deltaFile;
    private final MummerEnv env;
    private final boolean maxmatch;

    public NucmerRunner(String prefix, File fasta1, File fasta2, boolean maxmatch, MummerEnv env) {
        this.prefix = prefix;
        this.fasta1 = fasta1;
        this.fasta2 = fasta2;
        this.env = env;
        this.maxmatch = maxmatch;
        coordsFile = new File(prefix + ".coords");
        deltaFile = new File(prefix + ".delta");
    }

    public boolean exists() {
        return (coordsFile.exists() && coordsFile.length() > 0
                && deltaFile.exists() && deltaFile.length() > 0);
    }

    public void run() {
        if (exists()) {
            System.out.println(coordsFile.getPath() + " and "+ deltaFile.getPath() +" already exists.");
            return;
        }

        String[] cmd = null;
        if (maxmatch) {
            cmd = CommandFactory.generateMaxmatchNucmerCommand(env.getNucmer(), prefix, fasta1, fasta2);
        } else {
            cmd = CommandFactory.generateNucmerCommand(env.getNucmer(), prefix, fasta1, fasta2);
        }
        new ExternalProcess(cmd, env.getMumOutDir()).run();

    }
}
