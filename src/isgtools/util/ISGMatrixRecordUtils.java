/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isgtools.util;

import org.nau.isg.matrix.ISGMatrixHeader;
import org.nau.isg.matrix.ISGMatrixRecord;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jbeckstrom
 */
public class ISGMatrixRecordUtils {

    private ISGMatrixRecordUtils() {
    }

    public static ISGMatrixRecord removeStateAtIndex(ISGMatrixRecord record, int index) {
        return removeStateAtIndices(record, Arrays.asList(index));
    }

    public static ISGMatrixRecord removeStateAtIndices(ISGMatrixRecord record, List<Integer> indices) {
        List<Character> states = new ArrayList<Character>();
        for (int i = 0; i < record.getNStates(); i++) {
            if (!indices.contains(i)) {
                states.add(record.getState(i));
            }
        }
        Map<String, String> additionalInfo = new HashMap<String, String>(record.getAdditionalInfo());
        return new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), states, additionalInfo);
    }

    public static ISGMatrixRecord includeStateAtIndices(ISGMatrixRecord record, List<Integer> indices) {
        List<Character> states = new ArrayList<Character>();
        for (int i = 0; i < record.getNStates(); i++) {
            if (indices.contains(i)) {
                states.add(record.getState(i));
            }
        }
        Map<String, String> additionalInfo = new HashMap<String, String>(record.getAdditionalInfo());
        return new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), states, additionalInfo);
    }

    public static ISGMatrixRecord addState(Character state, ISGMatrixRecord record) {
        List<Character> states = getCopyOfStates(record);
        states.add(state);
        Map<String, String> additionalInfo = new HashMap<String, String>(record.getAdditionalInfo());
        return new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), states, additionalInfo);
    }

    public static ISGMatrixRecord addStates(List<Character> statesToAdd, ISGMatrixRecord record) {
        List<Character> states = getCopyOfStates(record);
        states.addAll(statesToAdd);

        Map<String, String> additionalInfo = new HashMap<String, String>(record.getAdditionalInfo());
        return new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), states, additionalInfo);
    }

    public static ISGMatrixRecord addAdditionalInfo(Map<String, String> additionalInfoToAdd, ISGMatrixRecord record) {
        Map<String, String> additionalInfo = new HashMap<String, String>(record.getAdditionalInfo());
        additionalInfo.putAll(additionalInfoToAdd);
        return new ISGMatrixRecord(record.getChrom(), record.getPos(), record.getRef(), record.getStates(), additionalInfo);
    }

    public static ISGMatrixRecord addAdditionalInfo(List<String> keys, List<String> values, ISGMatrixRecord record) {
        Map<String, String> additionalInfo = new HashMap<String, String>();
        for (int i = 0; i < keys.size(); i++) {
            additionalInfo.put(keys.get(i), values.get(i));
        }
        return addAdditionalInfo(additionalInfo, record);
    }

    public static List<Character> getCopyOfStates(ISGMatrixRecord record) {
        List<Character> ret = new ArrayList<Character>();
        for (int i = 0; i < record.getNStates(); i++) {
            ret.add(record.getState(i));
        }
        return ret;
    }
}
