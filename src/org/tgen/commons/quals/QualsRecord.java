/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.quals;

/**
 *
 * @author jbeckstrom
 */
public class QualsRecord {

    private String header;
    private Integer[] quals;

    public QualsRecord(String header, Integer[] quals) {
        this.header = header;
        this.quals = quals;
    }

    public String getHeader() {
        return header;
    }

    public Integer[] getQuals() {
        return quals;
    }

    @Override
    public String toString() {
        return "QualsRecord{" + "header=" + header + "quals=" + quals + '}';
    }

    

}
