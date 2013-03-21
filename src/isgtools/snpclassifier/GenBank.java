package isgtools.snpclassifier;

import org.biojava.bio.seq.*;
import org.biojava.bio.seq.io.*;
import java.io.*;

import org.biojava.bio.*;
import org.biojavax.Namespace;
import org.biojavax.RichObjectFactory;
import org.biojavax.bio.seq.RichFeature;
import org.biojavax.bio.seq.RichSequence;
import org.biojavax.bio.seq.RichSequenceIterator;

import java.util.*;
import java.net.*;
import net.sf.picard.util.Interval;
import net.sf.picard.util.IntervalTree;
import net.sf.picard.util.OverlapDetector;
import org.biojava.bio.symbol.SymbolList;
import org.biojavax.RankedCrossRef;
import org.biojavax.RichAnnotation;

public class GenBank {

    private File file;
    private URL url;
    private ArrayList<RichFeature> featureList;
    private RichFeature source;
    private String name;
    private OverlapDetector<RichFeature> featureOverlapDetector = new OverlapDetector<RichFeature>(0, 0);

    public GenBank(File file) {
        setFile(file);
        featureList = new ArrayList<RichFeature>();
        parse();
    }

    public GenBank(URL url) {
        this.url = url;
        parse();
    }

    private void setFile(File file) {
        this.file = file;
        name = file.getName();
        name = name.substring(0, name.lastIndexOf('.'));
    }

    public String getName() {
        return name;
    }

    private void parse() {
        BufferedReader br = null;

        try {

            //create a buffered reader to read the sequence file specified by args[0]
            if (file != null) {
                br = new BufferedReader(new FileReader(file));
            }

            if (url != null) {
                br = new BufferedReader(
                        new InputStreamReader(
                        url.openStream()));
            }

        } catch (Exception ex) {
            //can't find the file specified by args[0]
            ex.printStackTrace();
            System.exit(-1);
        }

        //read the GenBank File

        //SequenceIterator sequences = SeqIOTools.readGenbank(br);

        // an input GenBank file

        Namespace ns = RichObjectFactory.getDefaultNamespace();
        // we are reading DNA sequences
        RichSequenceIterator seqs = RichSequence.IOTools.readGenbankDNA(br, ns);

        //iterate through the sequences
        while (seqs.hasNext()) {
            try {
                RichSequence rs = seqs.nextRichSequence();

                //Sequence seq = sequences.nextSequence();
                //do stuff with the sequence
                //FeatureFilter ff = new FeatureFilter.ByType("gene");

                //get the filtered Features
                Iterator<Feature> i = rs.features();//rs.filter(ff);

                //iterate over the Features in fh
                //RichFeature oldFeature = null;
                TreeMap<String, RichFeature> featureMap = new TreeMap<String, RichFeature>();

                while (i.hasNext()) {
                    Feature f = i.next();
                    RichFeature rf = (RichFeature) f;

                    Interval interval = new Interval("", f.getLocation().getMin(), f.getLocation().getMax());
                    featureOverlapDetector.addLhs(rf, interval);

                    if (rf.getType().equals("source")) {
                        source = rf;
                    } else {

                        String locusTag = getValue("locus_tag", rf);

                        if (locusTag != null) {

                            RichFeature currentFeature = featureMap.get(locusTag);
                            if (currentFeature == null) {
                                featureMap.put(locusTag, rf);
                            } else {
                                if (currentFeature.getType().equals("gene")) {

                                    if (annotationContains("pseudo", currentFeature)) {
                                        currentFeature.setType("pseudo_gene");
                                    } else {
                                        featureMap.put(locusTag, rf);
                                    }

                                }
                            }

                        } else {

                            featureList.add(rf);
                        }

                    }

                    /*
                    if(oldFeature!=null){
                    if(oldFeature.getType().equals("gene") && rf.getType().equals("gene")){
                    oldFeature.setType("psuedo_gene");
                    }
                    }
                    //System.out.println("strand: "+rf.getStrand());
                    featureList.add(rf);
                    oldFeature = rf;
                     */
                }

                //remove gene features
                ArrayList<RichFeature> values = new ArrayList<RichFeature>(featureMap.values());
                for (int j = 0; j < values.size(); j++) {
                    RichFeature rf = values.get(j);
                    if (rf.getType().equals("gene")) {
                        rf.setType("pseudo_gene");
                    }
                    featureList.add(rf);
                }


            } catch (BioException ex) {
                //not in GenBank format
                ex.printStackTrace();
                System.exit(-1);
            } catch (NoSuchElementException ex) {
                //request for more sequence when there isn't any
                ex.printStackTrace();
                System.exit(-1);
            }

        }

    }

    public String getSequenceForFeature(String key, String value) {

        for (int i = 0; i < featureList.size(); i++) {
            Feature feature = featureList.get(i);
            //System.out.println(feature.getType()+" ");
            Annotation ann = feature.getAnnotation();
            for (Iterator iter = ann.keys().iterator(); iter.hasNext();) {
                Object tmp_key = iter.next();
                Object tmp_value = ann.getProperty(tmp_key);
                if (tmp_key.toString().equalsIgnoreCase(key) && tmp_value.toString().equalsIgnoreCase(value)) {
                    //System.out.println(tmp_value+" = "+value+" "+feature.getSequence().seqString().length());
                    int max = feature.getLocation().getMax();
                    int min = feature.getLocation().getMin();
                    return feature.getSequence().subStr(min, max);
                }
            }
        }

        return null;

    }

    public String getValue(String key, RichFeature feature) {

        Annotation ann = feature.getAnnotation();
        try {
            String value = ann.getProperty(key).toString();
            return value;
        } catch (Exception e) {
            return null;
        }


    }

    public boolean annotationContains(String needle, RichFeature rf) {

        Annotation ann = rf.getAnnotation();

        for (Iterator iter = ann.keys().iterator(); iter.hasNext();) {
            Object tmp_key = iter.next();
            Object tmp_value = ann.getProperty(tmp_key);

            if (tmp_key.toString().contains(needle)) {
                return true;
            } else if (tmp_value != null && tmp_value.toString().contains(needle)) {
                return true;
            }
        }

        return false;
    }

    public RichFeature getFeatureContaining(int pos) {

        for (int i = 0; i < featureList.size(); i++) {

            RichFeature feature = featureList.get(i);
            int max = feature.getLocation().getMax();
            int min = feature.getLocation().getMin();

            if (pos >= min && pos <= max) {
                return feature;
            }
        }

        return null;

    }

    public List<RichFeature> getFeaturesContaining(int pos) {
        final List<RichFeature> ret = new ArrayList<RichFeature>();

        for (int i = 0; i < featureList.size(); i++) {

            RichFeature feature = featureList.get(i);
            int max = feature.getLocation().getMax();
            int min = feature.getLocation().getMin();

            if (pos >= min && pos <= max) {
                ret.add(feature);
            }
        }

        return ret;

    }

    public Collection<RichFeature> getOverlappingFeatures(int pos) {
        final Interval i = new Interval("", pos, pos);
        return featureOverlapDetector.getOverlaps(i);
    }

    public String getSourceSeq() {
        String seq = null;

        if (source != null) {

            int max = source.getLocation().getMax();
            int min = source.getLocation().getMin();
            seq = source.getSequence().subStr(min, max).toUpperCase();

        }

        return seq;
    }

    public void write(File file) {

        try {

            PrintWriter pw = new PrintWriter(new FileWriter(file));

            for (int i = 0; i < featureList.size(); i++) {

                RichFeature rf = featureList.get(i);


                int max = rf.getLocation().getMax();
                int min = rf.getLocation().getMin();
                String seq = rf.getSequence().subStr(min, max).toUpperCase();
                char comp = '+';

                if (rf.getStrand().toString().equals("NEGATIVE")) {
                    SymbolList symL = DNATools.createDNA(seq);

                    //reverse complement it
                    symL = DNATools.reverseComplement(symL);

                    seq = symL.seqString();
                    comp = '-';
                }

                pw.println(">" + getValue("locus_tag", rf) + " " + comp);

                StringBuffer line = new StringBuffer();
                int count = 0;
                int total = 0;

                for (int j = 0; j < seq.length(); j += 3) {

                    if (j + 3 <= seq.length()) {
                        String codon = seq.substring(j, j + 3);
                        line.append(codon + " ");
                        count++;
                    }

                    if (count == 10) {
                        total += count * 3;
                        pw.println(line.toString() + total);
                        count = 0;
                        line = new StringBuffer();
                    }

                }

                total += count * 3;
                pw.println(line.toString() + total);

            }

            pw.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        File file = new File("/Users/jbeckstrom/Desktop/NC_009076.gbk");
        //URL url = new URL("http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?WebEnv=0lE4G-YU-VxOPHnNpvZH03tDKPa3E5r6S-y6T0wdVtAUh9WWOb26FM7Ujy-ZWOU-habZlwq_jrcWtOUd%40264E35BB8A269FF0_0177SID&db=nucleotide&qty=1&c_start=1&list_uids=149147510&uids=&dopt=gb&dispmax=5&sendto=t&fmt_mask=0&from=begin&to=end&extrafeatpresent=1&ef_CDD=8&ef_MGC=16&ef_HPRD=32&ef_STS=64&ef_tRNA=128&ef_microRNA=256&ef_Exon=512");
        GenBank gb = new GenBank(file);
        for(RichFeature rf: gb.getOverlappingFeatures(1)){
            
            System.out.println(rf.getType());
            for(Object obj: rf.getRankedCrossRefs()){
                RankedCrossRef rcr = (RankedCrossRef)obj;
                System.out.println(rcr.getCrossRef().getDbname());
            }
            System.out.println(rf.countFeatures());
            RichAnnotation annot = rf.getRichAnnotation();
            for(Object key: annot.keys()){
                Object prop = annot.getProperty(key);
                System.out.println(key+" "+prop);
            }
        }
    }
}
