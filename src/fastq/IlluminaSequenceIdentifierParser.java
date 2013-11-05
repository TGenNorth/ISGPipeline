/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fastq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sf.picard.fastq.FastqConstants;

/**
 *
 * @author jbeckstrom
 */
public class IlluminaSequenceIdentifierParser implements SequenceIdentifierParser{
    //@HWUSI-EAS100R:6:73:941:1973#0/1
    private static final String IlluminaPatternRegex = "@{0,1}([^:]+):([0-9]+):([0-9]+):([0-9]+):([0-9]+)#([^/])+/([12]).*";
    private static final Pattern IlluminaPattern = Pattern.compile(IlluminaPatternRegex);
    
    @Override
    public boolean canParse(final String str){
        return str.trim().matches(IlluminaPatternRegex);
    }

    @Override
    public SequenceIdentifier parse(String str) {
        str = str.trim();
        //remove '@'
        if(str.startsWith(FastqConstants.SEQUENCE_HEADER)){
            str = str.substring(1);
        }
        final Matcher matcher = IlluminaPattern.matcher(str);
        if(!matcher.find() || matcher.groupCount()!=7){
            throw new IllegalArgumentException("Failed to parse sequence identifier: "+str);
        }
        int i = 1;
        final String instrumentName = matcher.group(i++);
        final int flowcellLane = Integer.parseInt(matcher.group(i++));
        final int tileNumber = Integer.parseInt(matcher.group(i++));
        final int xCoord = Integer.parseInt(matcher.group(i++));
        final int yCoord = Integer.parseInt(matcher.group(i++));
        final String index = matcher.group(i++);
        final int pairNum = Integer.parseInt(matcher.group(i++));
        
        return new IlluminaSequenceIdentifier(instrumentName,
                flowcellLane,
                tileNumber,
                xCoord,
                yCoord,
                index,
                pairNum);
    }
            
}
