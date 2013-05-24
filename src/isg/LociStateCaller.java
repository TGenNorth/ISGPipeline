/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg;

import org.broadinstitute.sting.gatk.walkers.coverage.CallableLoci.CalledState;

/**
 *
 * @author jbeckstrom
 */
public interface LociStateCaller {
    public CalledState call(String chr, int pos);
}
