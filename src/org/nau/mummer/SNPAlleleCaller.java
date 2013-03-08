/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.mummer;

import org.broadinstitute.sting.utils.variantcontext.Allele;

/**
 *
 * @author jbeckstrom
 */
public interface SNPAlleleCaller<T> {
    public Allele callAlternate(T object);
    public Allele callReference(T object);
}
