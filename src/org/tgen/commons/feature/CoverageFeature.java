/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.feature;

/**
 *
 * @author jbeckstrom
 */
public class CoverageFeature extends Locus {

    private final int cov;

    public CoverageFeature(String chr, int pos, int cov){
        super(chr, pos, pos);
        this.cov = cov;
    }

    public int getCov() {
        return cov;
    }

}
