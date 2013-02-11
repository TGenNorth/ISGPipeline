package org.nau.mummer;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.nau.util.ExternalProcess;
import org.tgen.commons.utils.CommandFactory;

public class NucmerRunner implements Runnable {

    private final String prefix;
    private final File refFasta;
    private final File qryFasta;
    private final File coordsFile;
    private final File deltaFile;
    private final MummerEnv env;

    public NucmerRunner(String prefix, File ref, MummerEnv env) {
        this.prefix = prefix;
        this.refFasta = ref;
        this.qryFasta = ref;
        this.env = env;
        coordsFile = getFile(env.getMumOutDir(), prefix, ".coords");
        deltaFile = getFile(env.getMumOutDir(), prefix, ".delta");
    }

    public NucmerRunner(String prefix, File ref, File qry, MummerEnv env) {
        this.prefix = prefix;
        this.refFasta = ref;
        this.qryFasta = qry;
        this.env = env;
        coordsFile = getFile(env.getMumOutDir(), prefix, ".coords");
        deltaFile = getFile(env.getMumOutDir(), prefix, ".delta");
    }
    
    private File getFile(File dir, String prefix, String suffix){
        if(prefix.startsWith(File.separator)){
            return new File(prefix+suffix);
        }else{
            return new File(dir, prefix+suffix);
        }
    }

    public boolean exists() {
        return (coordsFile.exists() && coordsFile.length() > 0
                && deltaFile.exists() && deltaFile.length() > 0);
    }

    private String[] createNucmerCommand() {
        List<String> ret = new ArrayList<String>();
        ret.add(env.getNucmer().getAbsolutePath());
        if (refFasta == qryFasta) {
            ret.add("--maxmatch");
            ret.add("--nosimplify");
        }
        ret.add("--coords"); 
        ret.add("--prefix=" + prefix);
        ret.add(refFasta.getAbsolutePath());
        ret.add(qryFasta.getAbsolutePath());
        return ret.toArray(new String[ret.size()]);
    }

    public void run() {
        if (exists()) {
            System.out.println(coordsFile.getPath() + " and " + deltaFile.getPath() + " already exists.");
            return;
        }
        String[] cmd = createNucmerCommand();
        ExternalProcess.execute(cmd, env.getMumOutDir());
    }
}
