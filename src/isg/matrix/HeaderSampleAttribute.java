/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package isg.matrix;

/**
 *
 * @author jbeckstrom
 */
public class HeaderSampleAttribute implements HeaderAttribute {

    private final String name;
    private final String sampleName;

    public HeaderSampleAttribute(final String str) {
        String[] split = str.split(":");
        if (split.length == 2) {
            sampleName = split[0];
            name = split[1];
        }else{
            throw new IllegalArgumentException("Failed to parse attribute string: "+str);
        }
    }

    public HeaderSampleAttribute(String sampleName, String name) {
        this.name = name;
        this.sampleName = sampleName;
    }
    
    public static HeaderSampleAttribute createPatternAttribute(String sampleName){
        return new HeaderSampleAttribute(sampleName, HeaderAttribute.PATTERN_STR);
    }

    public String getSampleName() {
        return sampleName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", sampleName, name);
    }
    
    
}
