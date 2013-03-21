/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.utils;

import org.tgen.commons.feature.CoverageFeature;
import org.tgen.commons.feature.Locus;
import org.tgen.commons.gff.GffRecord;

/**
 *
 * @author jbeckstrom
 */
public class ConverterUtils {

    public static GffRecord toGff(Locus locus) {
        GffRecord ret = new GffRecord();
        ret.setStart(locus.getStart());
        ret.setEnd(locus.getEnd());
        ret.setAttribute("i", locus.getChr());
        return ret;
    }
}
