/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.reference;

import java.io.File;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;

/**
 *
 * @author jbeckstrom
 */
public class ReferenceSequenceManagerFactory {
    
    public static ReferenceSequenceManager createReferenceSequenceManager(File fasta){
        ReferenceSequenceManager ret = new ReferenceSequenceManager();
        ReferenceSequenceFile refSeqFile = ReferenceSequenceFileFactory.getReferenceSequenceFile(fasta);
        ReferenceSequence refSeq = null;
        while ((refSeq = refSeqFile.nextSequence()) != null) {
            ret.addSequence(refSeq.getName(), refSeq.getBases());
        }
        return ret;
    }
    
}
