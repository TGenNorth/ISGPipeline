/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.utils;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public class FileUtils {
    
    public static String getFilenameWithoutExtension(File file){
        String ret = file.getName();
        int index = ret.lastIndexOf(".");
        if(index!=-1){
            ret = ret.substring(0, index);
        }
        return ret;
    }
    
}
