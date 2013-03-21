/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.mummer.delta;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class DeltaRecord {

    private DeltaSequenceHeader header;
    private List<DeltaAlignment> alignments =
            new ArrayList<DeltaAlignment>();

    public DeltaRecord(DeltaSequenceHeader header) {
        this.header = header;
    }

    void addAlignment(DeltaAlignment alignment) {
        alignments.add(alignment);
    }

    public List<DeltaAlignment> getAlignments() {
        return alignments;
    }

    public DeltaSequenceHeader getHeader() {
        return header;
    }
    
    @Override
    public String toString() {
        return "DeltaRecord{" + "header=" + header + "alignments=" + alignments + '}';
    }

    

}
