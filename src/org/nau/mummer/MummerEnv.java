package org.nau.mummer;

import java.io.File;
import java.io.FileNotFoundException;

public class MummerEnv {

    public static final String NUCMER = "nucmer";
    public static final String SHOWSNPS = "show-snps";
    public static final String DELTA_FILTER= "delta-filter";
    public static final String SHOW_COORDS= "show-coords";
    private File mumDir;
    private File mumOutDir;
    private File nucmer;
    private File deltaFilter;
    private File showSnps;
    private File showCoords;

    public MummerEnv(File mumDir, File mumOutDir) throws FileNotFoundException {
        this.mumDir = mumDir;
        this.mumOutDir = mumOutDir;
        init();
    }

    private void init() throws FileNotFoundException {
        nucmer = getProgram(NUCMER);
        showSnps = getProgram(SHOWSNPS);
        deltaFilter = getProgram(DELTA_FILTER);
        showCoords = getProgram(SHOW_COORDS);
    }
    
    private File getProgram(String name) throws FileNotFoundException{
        File ret = new File(mumDir, name);
        if (!ret.exists()) {
            throw new FileNotFoundException(ret.getAbsolutePath());
        }
        return ret;
    }

    public File getNucmer() {
        return nucmer;
    }

    public File getShowSnps() {
        return showSnps;
    }
    
    public File getDeltaFilter(){
        return deltaFilter;
    }

    public File getMumOutDir() {
        return mumOutDir;
    }

    public File getShowCoords() {
        return showCoords;
    }
}
