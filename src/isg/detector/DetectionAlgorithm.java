/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.detector;

import isg.util.Algorithm;
import java.util.Collection;
import org.broadinstitute.sting.utils.collections.Pair;

/**
 *
 * @author jbeckstrom
 */
public interface DetectionAlgorithm<I, O> extends Algorithm<I, O> {

    public boolean detectable(I i);
    
}
