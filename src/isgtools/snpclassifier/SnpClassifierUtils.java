package isgtools.snpclassifier;

import java.util.*;
import java.awt.*;

import org.biojava.bio.seq.DNATools;
import org.biojava.bio.seq.RNATools;
import org.biojava.bio.symbol.IllegalSymbolException;
import org.biojava.bio.symbol.Symbol;
import org.biojava.bio.symbol.SymbolList;
import org.biojava.bio.symbol.SymbolListViews;
import org.biojava.bio.symbol.TranslationTable;

public class SnpClassifierUtils {

    public static String revComp(String seq) {
        return reverse(compliment(seq));
    }

    public static String reverse(String seq) {
        StringBuffer buff = new StringBuffer(seq);
        buff = buff.reverse();
        return buff.toString();
    }

    public static String compliment(String seq) {
        seq = seq.toLowerCase();
        seq = seq.replace('t', 'A');
        seq = seq.replace('a', 'T');
        seq = seq.replace('c', 'G');
        seq = seq.replace('g', 'C');
        seq = seq.toUpperCase();
        return seq;
    }

    public static char compliment(char state) {
        if (state == 'A') {
            return 'T';
        } else if (state == 'T') {
            return 'A';
        } else if (state == 'C') {
            return 'G';
        } else if (state == 'G') {
            return 'C';
        }
        return state;
    }

    public static boolean isValidBase(char base) {
        if (base == 'A' || base == 'T' || base == 'C' || base == 'G') {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isValidBase(String base) {
        if (base.matches("[aAtTgGcC]")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getTranslation(String dna, int transTable) {
        //get the Euplotoid translation table
        TranslationTable eup = RNATools.getGeneticCode(transTable);//RNATools.getGeneticCode("BACTERIAL");
        String translation = null;

        try {
            //make a DNA sequence including the 'tga' codon
            SymbolList seq = DNATools.createDNA(dna);

            //transcribe to RNA
            //seq = DNATools.transcribeToRNA(seq);
            seq = RNATools.transcribe(seq);

            //veiw the RNA sequence as codons, this is done internally by RNATool.translate()
            seq = SymbolListViews.windowedSymbolList(seq, 3);

            //translate
            SymbolList protein = SymbolListViews.translate(seq, eup);

            //print out the protein
            translation = protein.seqString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return translation;
    }

    public static String createCodon(String dna, int index) {
        String ret = null;

        for (int i = 0; i < dna.length(); i += 3) {

            if (index >= i && index < i + 3) {
                ret = dna.substring(i, i + 3);
                break;
            }

        }

        return ret;
    }

}
