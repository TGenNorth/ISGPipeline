/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import net.sf.picard.PicardException;

/**
 *
 * @author jbeckstrom
 */
public class IoUtil extends net.sf.picard.io.IoUtil {
    
    public static void assertFileIsExecutable(final File file) {
        if (file == null) {
			throw new IllegalArgumentException("Cannot check executability of null file.");
		} else if (!file.exists()) {
            throw new PicardException("Cannot execute non-existent file: " + file.getAbsolutePath());
        }
        else if (file.isDirectory()) {
            throw new PicardException("Cannot execute file because it is a directory: " + file.getAbsolutePath());
        }
        else if (!file.canExecute()) {
            throw new PicardException("File exists but is not executable: " + file.getAbsolutePath());
        }
    }
    
}
