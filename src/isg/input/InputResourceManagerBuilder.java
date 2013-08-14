/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import isg.util.SequenceFilePairPattern;
import isg.util.SequenceFilePairPatterns;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores input files by sample name.
 * 
 * @author jbeckstrom
 */
public class InputResourceManagerBuilder {

    private final Map<String, InputResource> resources = new HashMap<String, InputResource>();
    private final FASTQInputResources fastqResources;
    
    public InputResourceManagerBuilder(SequenceFilePairPatterns patterns){
        fastqResources = new FASTQInputResources(patterns);
    }
    
    /**
     * Determines file type and adds the corresponding resource to the 
     * resources map.
     * 
     * @param file
     * @throws IOException 
     */
    public void addFile(File file) throws IOException {
        String filename = file.getName();
        if (filename.endsWith(".bam")) {
            addResource(BAMInputResource.create(file));
        } else if (filename.endsWith(".fasta")) {
            addResource(FASTAInputResource.create(file));
        } else if (filename.endsWith(".vcf")) {
            addResource(VCFInputResource.create(file));
        } else if (filename.endsWith(".fastq")
                || filename.endsWith(".txt")
                || filename.endsWith(".gz")) {
            fastqResources.addFile(file);
        }
    }
    
    public void addFilesInDir(File dir) throws IOException {
        if(!dir.isDirectory()){
            throw new IllegalArgumentException("dir is not a directory: "+dir);
        }
        for(File f: dir.listFiles()){
            addFile(f);
        }
    }
    
    public <T extends InputResource> void addResources(List<T> inputResources){
        for(InputResource resource: inputResources){
            addResource(resource);
        }
    }
    
    public void addResource(InputResource inputResource){
        if(resources.containsKey(inputResource.getSample())){
            throw new IllegalArgumentException("sample already exists: "+inputResource.getSample());
        }
        resources.put(inputResource.getSample(), inputResource);
    }
    
    public InputResourceManager build(){
        if(fastqResources.isPending()){
            throw new IllegalStateException("Could not build with missing fastqs!");
        }
        addResources(fastqResources.getResources());
        return new InputResourceManager(resources);
    }

}
