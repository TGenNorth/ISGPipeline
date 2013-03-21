/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.pipeline;

import java.io.File;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class QSub {
    
    public static String createCommand(File script, String name, List<String> commands, List<String> dependencies){
        StringBuilder ret = new StringBuilder();
        CommandUtils.appendWithSpace(ret, "qsub");
        CommandUtils.appendWithSpace(ret, "-N");
        CommandUtils.appendWithSpace(ret, name);
        if(dependencies!=null && dependencies.size()>0){
            CommandUtils.appendWithSpace(ret, "-W");
            CommandUtils.appendWithSpace(ret, "depend=afterok:"+CommandUtils.getDependenciesAsString(dependencies));
        }
        CommandUtils.appendWithSpace(ret, "-v");
        CommandUtils.appendWithSpace(ret, "command=\"" + CommandUtils.createCommand(commands) + "\"");
        ret.append(script.getAbsolutePath());
        return ret.toString();
    }
    
    
}
