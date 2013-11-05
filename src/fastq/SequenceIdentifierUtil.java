/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jbeckstrom
 */
public class SequenceIdentifierUtil {
    
    private static final List<SequenceIdentifierParser> parsers = new ArrayList<SequenceIdentifierParser>();
    static{
        parsers.add(new IlluminaSequenceIdentifierParser());
    }
    
    public static SequenceIdentifierParser determineParser(final String str){
        for(SequenceIdentifierParser parser: parsers){
            if(parser.canParse(str)){
                return parser;
            }
        }
        return new SequenceIdentifierParser(){

            @Override
            public boolean canParse(String str) {
                return true;
            }

            @Override
            public SequenceIdentifier parse(String str) {
                return new UnknownSequenceIdentifier();
            }
            
        };
    }
    
}
