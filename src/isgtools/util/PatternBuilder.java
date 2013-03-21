package isgtools.util;

import org.nau.isg.matrix.ISGMatrixHeader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;

public class PatternBuilder {

    private Map<String, String> pattern = new HashMap<String, String>();
    private Map<String, Integer> states = new HashMap<String, Integer>();

    public PatternBuilder() {
    }
    
    public static Map<String, String> generatePattern(VariantContext vc){
        return generatePattern(vc, vc.getSampleNames());
    }
    
    public static Map<String, String> generatePattern(VariantContext vc, Collection<String> sampleNames){
        PatternBuilder pb = new PatternBuilder();
        pb.addAllele(ISGMatrixHeader.REF, vc.getReference());
        for(String sample: sampleNames){
            pb.addAllele(sample, vc.getGenotype(sample).getAllele(0));
        }
        return pb.build();
    }
    
    public void addAllele(String sample, Allele allele) {

        if (allele.basesMatch(Allele.NO_CALL) || allele.basesMatch("N")) {
            pattern.put(sample, allele.getBaseString());
        } else {
            Integer index = states.get(allele.getBaseString());
            if (index!=null) {
                pattern.put(sample, Integer.toString(index));
            } else {
                states.put(allele.getBaseString(), states.size()+1);
                pattern.put(sample, Integer.toString(states.size()));
            }
        }

    }

    public Map<String, String> build() {
        return pattern;
    }
    
    public static void main(String[] args){
        
        PatternBuilder pb = new PatternBuilder();
        pb.addAllele("A", Allele.NO_CALL);
        pb.addAllele("B", Allele.create("A"));
        pb.addAllele("C", Allele.create("A"));
        pb.addAllele("D", Allele.create("T"));
        pb.addAllele("E", Allele.create("A"));
        
        System.out.println(pb.build());
    }
}
