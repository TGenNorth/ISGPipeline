/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.input;

import java.io.File;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.broadinstitute.sting.utils.exceptions.UserException;

/**
 *
 * @author jbeckstrom
 */
public class InputResourceValidationExceptions extends UserException{
    
    public InputResourceValidationExceptions(String message){
        super(message);
    }
    
    public static final class CompoundUserException extends UserException {
        public CompoundUserException(List<Exception> exceptions){
            super(String.format("The following User Errors occured: %s", messageString(exceptions)));
        }
        
        public static String messageString(List<Exception> exceptions){
            String str = "";
            for(Exception e: exceptions){
                str += e.getMessage() + "\n";
            }
            return str;
        }
    }

    public static final class MissingMatesFileException extends UserException{
        public MissingMatesFileException(File reads, File mates){
            super(String.format("Could not find matching mates file for reads %s. Expecting %s", reads.getAbsolutePath(), mates.getAbsolutePath()));
        }
    }
    
    public static final class MoreThanOneSampleException extends UserException{
        public MoreThanOneSampleException(File f){
            super(String.format("More than one sample detected in file header: %s", f.getAbsoluteFile()));
        }
    }
    
    public static final class MissingReadGroupException extends MalformedBAM{
        public MissingReadGroupException(File f){
            super(f, "Missing read group in file header");
        }
    }
    
    public static final class NoSampleDetectedException extends UserException{
        public NoSampleDetectedException(File f){
            super(String.format("No sample detected in file header: %s", f.getAbsoluteFile()));
        }
    }
    
    public static final class DuplicateSampleNamesException extends UserException{
        public DuplicateSampleNamesException(InputResource r1, InputResource r2){
            super(String.format("Duplicate sample names detected in resources: %s and %s", r1, r2));
        }
    }
    
    
}
