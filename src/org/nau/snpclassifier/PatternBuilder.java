package org.nau.snpclassifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.broadinstitute.sting.utils.variantcontext.Allele;

public class PatternBuilder {

    private StringBuilder pattern = new StringBuilder();
    private List<Character> states = new ArrayList<Character>();

    public PatternBuilder() {
    }
    
    public void addAllele(Allele allele){
        String baseStr = allele.getBaseString();
        if (baseStr.length() > 1) {
            throw new IllegalArgumentException("Allele has more than one base: " + allele);
        }
        if(baseStr.length()==0){
            baseStr = Allele.NO_CALL_STRING;
        }
        addAllele(baseStr.charAt(0));
    }

    public void addAllele(char state) {

        if (state == 'A' || state == 'T' || state == 'C' || state == 'G') {
            int index = states.indexOf(state);
            if (index != -1) {
                index++;
                pattern.append(index);
            } else {
                states.add(state);
                pattern.append(states.size());
            }
        } else {
            pattern.append(state);
        }

    }

    public String build() {
        return pattern.toString();
    }
}
