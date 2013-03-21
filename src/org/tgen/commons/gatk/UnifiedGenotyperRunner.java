/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.gatk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class UnifiedGenotyperRunner extends GATKAnalysisRunner {

    public enum OutMode {

        EMIT_VARIANTS_ONLY,
        EMIT_ALL_CONFIDENT_SITES,
        EMIT_ALL_SITES;
    }
    private final File out;
    //optional
    private final OutMode outMode;
    private final int stand_call_conf;
    private final int stand_emit_conf;
    private final int ploidy;

    public UnifiedGenotyperRunner(String gatk, File out, File in, File ref) {
        this(gatk, out, in, ref, -1, -1, OutMode.EMIT_VARIANTS_ONLY, -1);
    }

    public UnifiedGenotyperRunner(String gatk, File out, File in, File ref, int stand_call_conf, int stand_emit_conf, OutMode outMode, int ploidy) {
        super(gatk, in, ref);
        this.out = out;
        this.outMode = outMode;
        this.stand_call_conf = stand_call_conf;
        this.stand_emit_conf = stand_emit_conf;
        this.ploidy = ploidy;
    }

    @Override
    protected boolean beforeRun() {
        if(out.exists() && out.length()>0){
            System.out.println("file already exists: "+out.getAbsolutePath());
            return false;
        }
        return true;
    }

    @Override
    protected String getAnalysisType() {
        return "UnifiedGenotyper";
    }

    @Override
    protected List<String> getArguments() {
        final List<String> ret = new ArrayList<String>();
        addArg(ret, "-o", out.getAbsolutePath());
        addArg(ret, "--output_mode", outMode.name());
        addArg(ret, "-stand_call_conf", stand_call_conf);
        addArg(ret, "-stand_emit_conf", stand_emit_conf);
        addArg(ret, "-ploidy", ploidy);
        return ret;
    }
    
    public static void main(String[] args){
        new UnifiedGenotyperRunner("", new File("test/out.vcf"), new File("test/Banthracis-A0891.bam"), new File("test/ref.fasta")).run();
    }
    
}
