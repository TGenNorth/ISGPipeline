/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sf.samtools.util.StringUtil;
import org.broadinstitute.sting.utils.exceptions.UserException;

/**
 *
 * @author jbeckstrom
 */
public class SnpEffConfig {

    private static final String CONFIG_CLASS = "ca.mcgill.mcb.pcingola.snpEffect.Config";
    private static final String GENOME_CLASS = "ca.mcgill.mcb.pcingola.interval.Genome";
    private static final String Snp_Eff_Cmd_Download_Class = "ca.mcgill.mcb.pcingola.snpEffect.commandLine.SnpEff";
    
    private final URLClassLoader classLoader;
    private final Map<String, Class<?>> classCache = new HashMap<String, Class<?>>();
    
    private Object config;
    private Object genome;
    private String[] chromosomeNames;
    private String snpEffectPredictorFilePath;
    
    public SnpEffConfig(File jarFile) throws MalformedURLException {
        classLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()});
    }
    
    public void load(String db, String configPath) throws Exception {
        config = constructConfig(db, configPath);
        
        snpEffectPredictorFilePath = getFileSnpEffectPredictor();
        //download db if it doesn't exist
        if(!new File(snpEffectPredictorFilePath).exists()){
            download(db, configPath);
        }
        
        //load SnpEffectPredictor which has the side-effect of setting the genome
        loadSnpEffectPredictor();
        
        //get chromosome names from genome
        genome = getGenome();
        chromosomeNames = getChromosomeNames();
        Arrays.sort(chromosomeNames);
    }
    
    public void validateChromosomeNames(List<String> seqNames, TranslationTable translationTable) {
        List<String> mismatches = new ArrayList<String>();
        for(String seqName: seqNames){
            if(translationTable!=null){
                System.out.println(seqName);
                seqName = translationTable.translate(seqName);
                System.out.println(seqName);
            }
            int index = Arrays.binarySearch(chromosomeNames, seqName);
            if(index<0){
                mismatches.add(seqName);
            }
        }
        if(mismatches.size()>0){
            throw new UserException(String.format("The SnpEff database chromosome names do not match the chromosome names found in you reference fasta file\n"
                    + "SnpEff chromosome names: '%s'\n"
                    + "reference chromosome names: '%s'\n"
                    + "To fix this problem, please rename the chromosomes in your reference fasta file to match the SnpEff database chromosome names.", StringUtil.join(",", chromosomeNames), StringUtil.join(",", mismatches)));
        }
    }
    
    public String[] chromosomeNames() {
        return chromosomeNames;
    }
    
    public String snpEffectPredictorFilePath() {
        return snpEffectPredictorFilePath;
    }

    private Class<?> findClass(String path) throws ClassNotFoundException{
        Class<?> ret = classCache.get(path);
        if(ret==null){
            ret = Class.forName(path, true, classLoader);
            classCache.put(path, ret);
        }
        return ret;
    }
    
    private Object constructConfig(String db, String config) throws Exception{
        Class<?> configClass = findClass(CONFIG_CLASS);
        return configClass.getConstructor(String.class, String.class).newInstance(db, config);
    }
    
    private void loadSnpEffectPredictor() throws Exception{
        Class<?> configClass = findClass(CONFIG_CLASS);
        Method loadSnpEffectPredictor = configClass.getDeclaredMethod("loadSnpEffectPredictor");
        loadSnpEffectPredictor.invoke(config);
    }
    
    private String getFileSnpEffectPredictor() throws Exception{
        Class<?> configClass = findClass(CONFIG_CLASS);
        Method getFileSnpEffectPredictor = configClass.getDeclaredMethod("getFileSnpEffectPredictor");
        return (String)getFileSnpEffectPredictor.invoke(config);
    }
    
    private Object getGenome() throws Exception{
        Class<?> configClass = findClass(CONFIG_CLASS);
        Method getGenome = configClass.getDeclaredMethod("getGenome");
        return getGenome.invoke(config);
    }
    
    private String[] getChromosomeNames() throws Exception{
        Class<?> genomeClass = findClass(GENOME_CLASS);
        Method getChromosomeNames = genomeClass.getDeclaredMethod("getChromosomeNames");
        return (String[]) getChromosomeNames.invoke(genome);
    }
    
    public void download(String db, String configPath) throws Exception{
        String[] args = new String[]{"download", "-config", configPath, "-v", db};
        Class<?> downloadClass = findClass(Snp_Eff_Cmd_Download_Class);
        Object downloadCmd = downloadClass.getConstructor().newInstance();
        downloadClass.getDeclaredMethod("parseArgs", args.getClass()).invoke(downloadCmd, new Object[]{args});
        downloadClass.getDeclaredMethod("run").invoke(downloadCmd);
    }
    
}
