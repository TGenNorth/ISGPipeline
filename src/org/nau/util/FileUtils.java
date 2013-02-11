/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nau.util;

import java.io.File;

/**
 *
 * @author jbeckstrom
 */
public class FileUtils {
    
    public static String stripExtension(File f) {
        return stripExtension(f.getName());
    }

    public static String stripExtension(String filename) {
        int index = filename.lastIndexOf('.');
        if (index == -1) {
            return filename;
        }
        return filename.substring(0, index);
    }

    public static boolean exists(File f) {
        return f.exists() && f.length() > 0;
    }
    
}
