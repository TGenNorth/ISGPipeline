/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrix {
    
    private ISGMatrixHeader header;
    private List<ISGMatrixRecord> records;
    
    public ISGMatrix(ISGMatrixHeader header){
        this.header = header;
        records = new ArrayList<ISGMatrixRecord>();
    }
    
    public void addRecord(ISGMatrixRecord record){
        records.add(record);
    }

    public ISGMatrixHeader getHeader() {
        return header;
    }

    public List<ISGMatrixRecord> getRecords() {
        return records;
    }
    
}
