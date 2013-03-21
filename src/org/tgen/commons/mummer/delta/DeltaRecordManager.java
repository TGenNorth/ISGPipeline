/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.tgen.commons.mummer.delta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class DeltaRecordManager {

    Map<String, List<DeltaRecord>> deltaRecords = new HashMap<String, List<DeltaRecord>>();

    public DeltaRecordManager(){

    }

    public void addDeltaRecord(DeltaRecord deltaRecord){
        String qName = deltaRecord.getHeader().getQuery();
        List<DeltaRecord> deltaQRecords = deltaRecords.get(qName);
        if(deltaQRecords==null){
            deltaQRecords = new ArrayList<DeltaRecord>();
            deltaRecords.put(qName, deltaQRecords);
        }
        deltaQRecords.add(deltaRecord);
    }

    public List<DeltaRecord> getDeltaRecordsByQueryName(String qName){
        return deltaRecords.get(qName);
    }

}
