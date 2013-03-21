/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.samtools.mpileup;

import org.tgen.commons.feature.CoverageFeature;

/**
 *
 * @author jbeckstrom
 */
public class MPileupRecord extends CoverageFeature{

    public MPileupRecord(String chr, int pos, int cov){
        super(chr, pos, cov);
    }

}
