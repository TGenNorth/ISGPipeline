package org.tgen.commons.mummer.snp;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.broad.tribble.AsciiFeatureCodec;
import org.broad.tribble.Feature;
import org.broad.tribble.readers.LineReader;

/**
 *
 * @author jbeckstrom
 */
public class MumSNPCodec extends AsciiFeatureCodec<MumSNPFeature> {
    // the minimum number of features in the Mumsnp file line
    private static final int minimumFeatureCount = 14;

    // the minimum number of features in the Mumsnp file line
    private static final int maximumFeatureCount = 16;

    public MumSNPCodec(){
        super(MumSNPFeature.class);
    }

    @Override
    public Feature decodeLoc(String line) {
        return decode(line);
    }
    
    @Override
    public MumSNPFeature decode(String line) {
        String[] array = line.split("\\s+");

        // make sure the split was successful - that we got an appropriate number of fields
        if (array.length != minimumFeatureCount && array.length != maximumFeatureCount)
            throw new IllegalArgumentException("Unable to parse line " + line + ", the length of split features is not a valid length");

        int index = 0;

        //known features columns
        int rPos = Integer.parseInt(array[index++]);
        String rBase = array[index++];
        String qBase = array[index++];
        int qPos = Integer.parseInt(array[index++]);
        int buff = Integer.parseInt(array[index++]);
        int dist = Integer.parseInt(array[index++]);

        //uknown feature columns
        int rNumRepeat = -1;
        int qNumRepeat = -1;

        if(array.length==maximumFeatureCount){
            rNumRepeat = Integer.parseInt(array[index++]);
            qNumRepeat = Integer.parseInt(array[index++]);
        }

        int rLength = Integer.parseInt(array[index++]);
        int qLength = Integer.parseInt(array[index++]);
        String rContext = array[index++];
        String qContext = array[index++];
        int rDir = Integer.parseInt(array[index++]);
        int qDir = Integer.parseInt(array[index++]);
        String rFastaID = array[index++];
        String qFastaID = array[index++];

        return new MumSNPFeature(rPos,
                         rBase,
                         qBase,
                         qPos,
                         buff,
                         dist,
                         rNumRepeat,
                         qNumRepeat,
                         rLength,
                         qLength,
                         rContext,
                         qContext,
                         rDir,
                         qDir,
                         rFastaID,
                         qFastaID);
    }

    
    
}
