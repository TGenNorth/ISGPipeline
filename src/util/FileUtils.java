/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.util.Collection;

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
    
    public static File findFirstFileWithExtensions(File dir, String[] extensions){
        Collection<File> files = org.apache.commons.io.FileUtils.listFiles(dir, extensions, false);
        if(!files.isEmpty()){
            return files.iterator().next();
        }
        return null;
    }
    
    public static File findFileUsingExtensions(File dir, String prefix, String[] extensions){
        for(String ext: extensions){
            File f = new File(dir, prefix + ext);
            if(f.exists()){
                return f;
            }
        }
        return null;
    }
}
