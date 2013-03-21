/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools.util;

import isgtools.model.ISGMatrix;
import isgtools.model.ISGMatrixHeader;
import isgtools.model.ISGMatrixRecord;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixUtils {
    
    private ISGMatrixUtils(){}
    
    public static ISGMatrix removeSampleFromMatrix(ISGMatrix matrix, String sampleName){
        ISGMatrix ret = new ISGMatrix( 
                ISGMatrixHeader.removeSampleFromHeader(matrix.getHeader(), sampleName) );
        int index = matrix.getHeader().indexOfSample(sampleName);
        for(ISGMatrixRecord record: matrix.getRecords()){
            ret.addRecord( ISGMatrixRecordUtils.removeStateAtIndex(record, index) );
        }
        return ret;
    }
}
