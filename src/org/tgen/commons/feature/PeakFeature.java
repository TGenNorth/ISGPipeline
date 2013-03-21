/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.feature;

/**
 *
 * @author jbeckstrom
 */
public class PeakFeature extends Locus{
    
    private final int maxCov;
    private final int median;
    
    public PeakFeature(String chr, int start, int end, int maxCov, int median){
        super(chr, start, end);
        this.maxCov = maxCov;
        this.median = median;
    }

    public int getMedian() {
        return median;
    }

    public int getMaxCov() {
        return maxCov;
    }
    
}
