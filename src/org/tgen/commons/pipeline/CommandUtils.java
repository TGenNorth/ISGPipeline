/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class CommandUtils {
    
    public static void appendWithSpace(StringBuilder builder, String str){
        builder.append(str);
        builder.append(" ");
    }
    
    public static String createCommand(List<String> commands){
        StringBuilder ret = null;
        for (String str : commands) {
            if (ret == null) {
                ret = new StringBuilder();
                ret.append(str);
            } else {
                ret.append(" ");
                ret.append(str);
            }
        }
        return ret.toString();
    }
    
    public static String getDependenciesAsString(List<String> dependencies){
        String ret = null;
        for (String str : dependencies) {
            if (ret == null) {
                ret = str;
            } else {
                ret += ":" + str;
            }
        }
        return ret;
    }
    
}
