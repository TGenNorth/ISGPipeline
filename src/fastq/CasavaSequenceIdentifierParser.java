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
public class CasavaSequenceIdentifierParser implements SequenceIdentifierParser{
    //@EAS139:136:FC706VJ:2:2104:15343:197393 1:Y:18:ATCACG
    private static final String CasavaPatternRegex = 
            "@{0,1}([^:]+):([0-9]+):([^:]+):([0-9]+):([0-9]+):([0-9]+):([0-9]+)\\s+([12]):([YN]):([0-9]+):([ATCG]+).*";
    private static final Pattern CasavaPattern = Pattern.compile(CasavaPatternRegex);
    private static final int ExpectedGroupCount = 11;
    
    @Override
    public boolean canParse(final String str){
        return str.trim().matches(CasavaPatternRegex);
    }

    @Override
    public SequenceIdentifier parse(String str) {
        str = str.trim();
        //remove '@'
        if(str.startsWith(FastqConstants.SEQUENCE_HEADER)){
            str = str.substring(1);
        }
        final Matcher matcher = CasavaPattern.matcher(str);
        if(!matcher.find() || matcher.groupCount()!=ExpectedGroupCount){
            throw new IllegalArgumentException("Failed to parse sequence identifier: "+str);
        }
        int i = 1;
        final String instrumentName = matcher.group(i++);
        final String runId = matcher.group(i++);
        final String flowCellId = matcher.group(i++);
        final int flowcellLane = Integer.parseInt(matcher.group(i++));
        final int tileNumber = Integer.parseInt(matcher.group(i++));
        final int xCoord = Integer.parseInt(matcher.group(i++));
        final int yCoord = Integer.parseInt(matcher.group(i++));
        
        final int pairNum = Integer.parseInt(matcher.group(i++));
        final String failFilter = matcher.group(i++);
        final int controlBits = Integer.parseInt(matcher.group(i++));
        final String index = matcher.group(i++);
        
        return new CasavaSequenceIdentifier(instrumentName,
                runId,
                flowCellId,
                flowcellLane,
                tileNumber,
                xCoord,
                yCoord,
                pairNum,
                failFilter,
                controlBits,
                index);
    }
            
}
