/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mummer.coords;

import java.io.File;
import java.util.Iterator;
import mummer.coords.Coord;
import mummer.coords.CoordsFileReader;
import mummer.coords.CoordsRecord;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalList;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMSequenceDictionary;
import net.sf.samtools.util.CloserUtil;
import org.broadinstitute.sting.commandline.Input;
import org.broadinstitute.sting.commandline.Output;
import org.broadinstitute.sting.queue.function.AbstractInProcessFunction;

/**
 *
 * @author jbeckstrom
 */
public class CoordsCoverageInProcessFunction extends AbstractInProcessFunction {

    @Input(doc = "input coords files")
    public File coordsFile;
    
    @Input(doc = "reference sequence file")
    public File referenceSequence;
    
    @Output(doc = "output interval list")
    public File outFile;
    
    @Override
    public void run() {
        final ReferenceSequenceFile ref = ReferenceSequenceFileFactory
                .getReferenceSequenceFile(referenceSequence);

        final SAMFileHeader header = new SAMFileHeader();
        header.setSequenceDictionary(ref.getSequenceDictionary());
        final IntervalList intervalList = new IntervalList(header);
        
        Iterator<Interval> iter = new CoordsCoverage(coordsFile, referenceSequence);
        while(iter.hasNext()){
            intervalList.add(iter.next());
        }
        CloserUtil.close(iter);
        
        intervalList.unique();
        intervalList.write(outFile);
    }

}
