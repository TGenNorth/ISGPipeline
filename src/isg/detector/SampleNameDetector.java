/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.broadinstitute.sting.utils.collections.Pair;

/**
 *
 * @author jbeckstrom
 */
public class SampleNameDetector {
    
    private List<SampleNameDetectionAlgorithm> detectors = new ArrayList<SampleNameDetectionAlgorithm>();
    
    public SampleNameDetector(){}
    
    public void add(final SampleNameDetectionAlgorithm d){
        detectors.add(d);
    }
    
    /**
     * Detect the sample name associated with a file. The detection will occur 
     * in the same order that the detectors were added in. Thus, it is important 
     * that a fail-safe detection algorithm (one that will detect a sample name 
     * for any file) is added last.
     * 
     * @param f the file to detect the sample name of.
     * @return the sample name or null if no detection could take place.
     */
    public String detect(File f){
        for(SampleNameDetectionAlgorithm d: detectors){
            if(d.detectable(f)){
                return d.apply(f);
            }
        }
        return null;
    }
    
}
