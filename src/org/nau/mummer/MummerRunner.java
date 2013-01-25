package org.nau.mummer;

public class MummerRunner implements Runnable {

    private final NucmerRunner nucmer;
    private final ShowSnpsRunner showSnps;
    private final DeltaFilterRunner deltaFilter;

    public MummerRunner(NucmerRunner nucmer, DeltaFilterRunner deltaFilter, ShowSnpsRunner showSnps) {
        this.nucmer = nucmer;
        this.showSnps = showSnps;
        this.deltaFilter = deltaFilter;
    }

    public void run() {
        nucmer.run();
        deltaFilter.run();
        showSnps.run();
    }
}
