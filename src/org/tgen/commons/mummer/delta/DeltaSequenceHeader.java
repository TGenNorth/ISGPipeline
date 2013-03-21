/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.mummer.delta;

/**
 *
 * @author jbeckstrom
 */
public class DeltaSequenceHeader {

    private String ref;
    private String query;

    public DeltaSequenceHeader(String ref, String query){
        this.ref = ref;
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public String getRef() {
        return ref;
    }

    

}
