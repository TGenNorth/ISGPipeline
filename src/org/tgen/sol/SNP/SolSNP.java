/**
 *
 */
package org.tgen.sol.SNP;

import net.sf.picard.PicardException;
import net.sf.picard.cmdline.CommandLineProgram;
import net.sf.picard.cmdline.Option;
import net.sf.picard.cmdline.StandardOptionDefinitions;
import net.sf.picard.cmdline.Usage;
import net.sf.picard.io.IoUtil;
import net.sf.picard.reference.ReferenceSequence;
import net.sf.picard.reference.ReferenceSequenceFile;
import net.sf.picard.reference.ReferenceSequenceFileFactory;
import org.tgen.sol.MappedBaseInfo;
import org.tgen.sol.PositionInfo;
import org.tgen.sol.SamPositionIterator;

import java.io.*;
import java.util.*;
import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMReadGroupRecord;

enum OutputFormat {

    GFF,
    VCF
}

enum OutputMode {

    Variants,
    VariantsAndReference,
    AllCallable,
    KnownCalls,
    VariantsAndNonReference;
}

/**
 * @author achristoforides
 */
public class SolSNP extends CommandLineProgram {

    @Usage
    public final String USAGE = getStandardUsagePreamble() + "SolSNP: Uses a modified Kolmogorov�Smirnov test to produce SNP variant calls from a SAM/BAM alignment file and a reference.";
    @Option(shortName = StandardOptionDefinitions.INPUT_SHORT_NAME, doc = "Input SAM/BAM file. Needs to be sorted by coordinate.")
    public File INPUT;
    @Option(shortName = StandardOptionDefinitions.OUTPUT_SHORT_NAME, doc = "Output file.")
    public File OUTPUT;
    @Option(doc = "Known calls file.", optional = true)
    public File KNOWN_CALLS;
    @Option(doc = "Strand Mode", optional = true)
    public StrandMode STRAND_MODE = StrandMode.VariantConsensus;
    @Option(doc = "Set to true to create a summary metrics directory upon completion of analysis.", optional = true)
    public Boolean SUMMARY = false;
    @Option(shortName = StandardOptionDefinitions.REFERENCE_SHORT_NAME,
    doc = "Reference Sequence File")
    public File REFERENCE_SEQUENCE;
    @Option(doc = "Minimum confidence score allowed for calls. ")
    public double FILTER;
    @Option(doc = "Calculate allelic balance by sampling the data.", optional = true)
    public Boolean CALCULATE_ALLELIC_BALANCE = false;
    @Option(doc = "Additional score bias towards a variant call. (Range: 0.0 - 1.0)", optional = true)
    public double CALL_BIAS = 0;
    @Option(doc = "Minimum base quality", optional = true)
    public int MINIMUM_BASE_QUALITY = 1; // By default, only bases with quality zero are trimmed
    @Option(doc = "Minimum mapping quality", optional = true)
    public int MINIMUM_MAPQ = 1; // By default, only reads with mapping quality zero are trimmed
    @Option(doc = "Output Format", optional = true)
    public OutputFormat OUTPUT_FORMAT = OutputFormat.GFF;
    @Option(doc = "Ploidy", optional = true)
    public Ploidy PLOIDY = Ploidy.Diploid;
    @Option(doc = "Minimum coverage", optional = true)
    public int MINIMUM_COVERAGE = 3;
    @Option(doc = "Maximum coverage", optional = true)
    public int MAXIMUM_COVERAGE = 0;
    @Option(doc = "Region (syntax: sequence_name,start,end)", optional = true)
    public String REGION = "";
    @Option(doc = "Region file (syntax: sequence_name:start-end)", optional = true)
    public File REGION_FILE;
    @Option(doc = "Choose what to output", optional = true)
    public OutputMode OUTPUT_MODE = OutputMode.Variants;
    @Option(doc = "Maximum genomic distance between paired reads.", optional = true)
    public Integer MAX_MATE_DISTANCE = Integer.MAX_VALUE;
    @Option(doc = "Minimum genomic distance between paired reads.", optional = true)
    public Integer MIN_MATE_DISTANCE = 0;
    @Option(doc = "Add genotype information to the output. ")
    public static Boolean GENERATE_GENOTYPES = true;
    @Option(doc = "Minimum 'Alignment Score' for a read to be considered.", optional = true)
    public static Integer MIN_ALIGNMENT_SCORE = 0;
    //Private Fields
    private HashMap<String, HashMap<Integer, SNPCall>> known_calls;
    AnalysisMetrics metrics = new AnalysisMetrics();
    PrintWriter summary_file = null;
    PrintWriter validation_file = null;
    PrintWriter out = null;
    PrintWriter false_negatives_file = null;
    private int snp_count = 0;
    //constants
    public static String SOLSNP_VERSION = "1.11";
    public static int MAX_CONFIDENCE = 30;

    /* (non-Javadoc)
     * @see net.sf.picard.cmdline.CommandLineProgram#doWork()
     */
    @Override
    protected int doWork() {


        //make sure our text output is UNIX-compatible, even when run on Windows
        System.setProperty("line.separator", "\n");

        //if we're allelic balancing, get this done before anything else
        org.tgen.sol.AllelicBalancer balancer = new org.tgen.sol.AllelicBalancer(INPUT);

        //initial assertions
        IoUtil.assertFileIsReadable(INPUT);
        IoUtil.assertFileIsReadable(REFERENCE_SEQUENCE);

        //initialize output
        if (OUTPUT != null) {
            IoUtil.assertFileIsWritable(OUTPUT);
            try {
                out = new PrintWriter(OUTPUT);
            } catch (FileNotFoundException e) {
                // we already asserted this so we should not get here
                throw new PicardException("Unexpected error. ", e);
            }
        } else {
            out = new PrintWriter(System.out);
        }

        WriteHeader(OUTPUT_FORMAT, out);

        //initialize reference
        ReferenceSequenceFile reference_file = ReferenceSequenceFileFactory.getReferenceSequenceFile(REFERENCE_SEQUENCE);

        //initialize auxiliary data
        InitKnownCalls();
        InitSummaryMetrics();

        //initialize SAM/BAM data iterators

        List<SamPositionIterator> positionIterators = new ArrayList<SamPositionIterator>();

        if (REGION_FILE != null) {
            BufferedReader region_reader = null;
            String line = null;
            try {
                region_reader = new BufferedReader(new FileReader(REGION_FILE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            try {

                while ((line = region_reader.readLine()) != null) {
                    String[] tokens = line.split(":");

                    String seq_name = tokens[0];
                    String positions = tokens[1];

                    tokens = positions.split("-");

                    int start = 0, end = 0;
                    try {
                        start = Integer.parseInt(tokens[0]);
                        end = Integer.parseInt(tokens[1]);
                    } catch (Exception e) {
                        System.err.println("Error parsing definition string");
                    }

                    SamPositionIterator position_iterator = new SamPositionIterator(INPUT, MINIMUM_COVERAGE, MAXIMUM_COVERAGE, MINIMUM_MAPQ, MIN_MATE_DISTANCE, MAX_MATE_DISTANCE, MINIMUM_BASE_QUALITY, MIN_ALIGNMENT_SCORE, seq_name, start, end);
                    positionIterators.add(position_iterator);
                }
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (REGION.equals("")) {
            SamPositionIterator position_iterator = new SamPositionIterator(INPUT, MINIMUM_COVERAGE, MAXIMUM_COVERAGE, MINIMUM_MAPQ, MIN_MATE_DISTANCE, MAX_MATE_DISTANCE, MINIMUM_BASE_QUALITY, MIN_ALIGNMENT_SCORE);
            positionIterators.add(position_iterator);
        } else {
            //split the region string into sequence name/beginning/end
            String[] tokens = REGION.split(",");
            int start = 0, end = 0;

            try {
                start = Integer.parseInt(tokens[1]);
                end = Integer.parseInt(tokens[2]);
            } catch (Exception e) {
                System.err.println("Error parsing definition string");
            }
            SamPositionIterator position_iterator = new SamPositionIterator(INPUT, MINIMUM_COVERAGE, MAXIMUM_COVERAGE, MINIMUM_MAPQ, MIN_MATE_DISTANCE, MAX_MATE_DISTANCE, MINIMUM_BASE_QUALITY, MIN_ALIGNMENT_SCORE, tokens[0], start, end);
            positionIterators.add(position_iterator);
        }

        SolSNPCaller s = new SolSNPCaller(STRAND_MODE, CALL_BIAS, PLOIDY);
        SNPCall SNP = new SNPCall();
        SNPCall known_call = new SNPCall();

        int coverage;
        int previous_position = 0;

        final Map<SNPCallPair, TreeMap<Integer, Long>> calltable_validation = new HashMap<SNPCallPair, TreeMap<Integer, Long>>();

        ReferenceSequence reference = null;

        while ((reference = reference_file.nextSequence()) != null) {
            for (SamPositionIterator position_iterator : positionIterators) {
                position_iterator.nextSequence();
                String ref_name = reference.getName();

                if (!position_iterator.getCurrentSequence().equals(ref_name)) {
                    continue;
                }

                System.out.println("Processing reads on sequence " + ref_name);

                byte[] ref_array = reference.getBases();

                // Iterate through loci with enough coverage
                while (position_iterator.hasMoreElements()) {

                    PositionInfo p = position_iterator.nextElement();
                    int curp;

                    if (p == null) {
                        curp = ref_array.length;
                    } else {
                        curp = p.position;
                        char reference_nucleotide = (char) ref_array[p.position - 1];
                        metrics.UpdatePositionMetrics(p, reference_nucleotide);
                    }

                    int nc = previous_position + 1;

                    while (nc < curp) {
                        Character reference_nucleotide = (char) ref_array[nc - 1];

                        GetKnownCall(ref_name, nc, reference_nucleotide, known_call);
                        SNP.callType = CallType.Uncallable;

                        metrics.UpdateSNPMetrics(SNP, known_call, reference_nucleotide);
                        nc++;
                    }

                    previous_position = curp;

                    if (p == null) {
                        break;
                    }

                    //SNP Calling!
                    char reference_nucleotide = (char) ref_array[p.position - 1];
                    coverage = p.mappedBases.size();

                    GetKnownCall(ref_name, curp, reference_nucleotide, known_call);

                    //Attempt to call SNP at this locus
                    s.isSNP(p, reference_nucleotide, SNP);

                    //System.out.println(SNP.callType);
                    //Apply low-confidence filter

                    double confidence = 0;

                    if (STRAND_MODE == StrandMode.VariantConsensus) {
                        if (SNP.callType == CallType.HomozygoteReference) {
                            confidence = Math.abs(1 - SNP.variantProb);
                        } else {
                            confidence = Math.abs(SNP.variantProb);
                        }
                    } else {
                        confidence = Math.abs(SNP.genotypeProb);
                    }

                    if (confidence < FILTER) {
                        SNP.callType = CallType.Uncallable;
                    }

                    metrics.UpdateSNPMetrics(SNP, known_call, reference_nucleotide);

                    //output, according to output mode
                    switch (OUTPUT_MODE) {
                        case AllCallable:
                            WriteRecord(out, SNP, known_call, p, reference_nucleotide);
                            break;
                        case Variants:
                            if (SNP.callType == CallType.HomozygoteNonReference || SNP.callType == CallType.Heterozygote) {
                                WriteRecord(out, SNP, known_call, p, reference_nucleotide);
                            } else if (known_call.callType != CallType.Unknown && known_call.callType != CallType.HomozygoteReference) {
                                WriteRecord(false_negatives_file, SNP, known_call, p, reference_nucleotide);
                            }
                            break;
                        case VariantsAndReference:
                            if (SNP.callType == CallType.HomozygoteNonReference || SNP.callType == CallType.Heterozygote || SNP.callType == CallType.HomozygoteReference) {
                                WriteRecord(out, SNP, known_call, p, reference_nucleotide);
                            }
                            break;
                        case KnownCalls:
                            if (known_call.callType != CallType.Unknown) {
                                WriteRecord(out, SNP, known_call, p, reference_nucleotide);
                            }
                            break;
                        case VariantsAndNonReference:
                            if (SNP.callType == CallType.HomozygoteNonReference || SNP.callType == CallType.Heterozygote || SNP.callType != CallType.HomozygoteReference) {
                                WriteRecord(out, SNP, known_call, p, reference_nucleotide);
                            }
                            break;
                    }

                }
            }
        }
        //Finalize output
        out.close();

        if (KNOWN_CALLS != null) {
            false_negatives_file.close();
        }

        if (SUMMARY) {
            WriteSummaryMetrics();
        }

        return 0;
    }

    private void WriteHeader(OutputFormat output_format, PrintWriter out) {
        switch (output_format) {
            case GFF:
                break;
            case VCF: {
                out.println("##fileformat=VCFv4.0");
                out.println("##source=SolSNP-" + SOLSNP_VERSION);
                out.println("##reference=" + REFERENCE_SEQUENCE.getName());
                out.println("##INFO=<ID=DP,Number=1,Type=Integer,Description=\"Total Depth\">");
                out.println("##INFO=<ID=CL,Number=1,Type=String,Description=\"Putative genotype call\">");
                out.println("##INFO=<ID=KC,Number=1,Type=String,Description=\"Genotype call from known calls file\">");
                out.println("##INFO=<ID=PL,Number=1,Type=String,Description=\"Full pileup (Uppercase is forward strand)\">");
                out.println("##FORMAT=<ID=GT,Number=1,Type=String,Description=\"Genotype\">");
                out.println("##FORMAT=<ID=GQ,Number=1,Type=Integer,Description=\"Genotype Quality\">");
                out.println("##command=" + getCommandLine());

                if (GENERATE_GENOTYPES) {
                    String sample_name = getSampleName(INPUT);
                    out.println("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\t" + sample_name);
                } else {
                    out.println("#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO");
                }
            }
        }

    }

    private void WriteRecord(PrintWriter out, SNPCall SNP, SNPCall known_call, PositionInfo p, char reference_nucleotide) {
        switch (OUTPUT_FORMAT) {
            case GFF: {
                out.printf("snp_%s_%d\tsolsnp-call\tsnp\t%d\t%d\t%.6f\t.\t.\tcall=%s;i=%s;ref=%s;",
                        p.sequenceName, ++snp_count,
                        p.position, p.position, SNP.variantProb,
                        SNP.toString(), p.sequenceName,
                        (char) reference_nucleotide);

                out.print(SNP.misc);

                for (String score : SNP.scores.keySet()) {
                    out.printf("%s=%5.9f;", score, SNP.scores.get(score));
                }

                out.print("pileup=");

                for (MappedBaseInfo b : p.mappedBases) {
                    out.print(b.nucleotide);
                }

                out.println();
            }
            break;
            case VCF: {
                double phred_score;
                if (SNP.allele2 == (char) reference_nucleotide) {
                    if (SNP.variantProb == 0) {
                        phred_score = MAX_CONFIDENCE;
                    } else {
                        phred_score = -10 * Math.log10(SNP.variantProb);
                    }
                } else if (SNP.variantProb == 1) {
                    phred_score = MAX_CONFIDENCE;
                } else {
                    phred_score = -10 * Math.log10(1 - SNP.variantProb);
                }


                out.printf("%s\t%d\t%s\t%s\t%s\t%.1f\tPASS\tCL=%s;",
                        p.sequenceName,
                        p.position, (known_call.ID == null ? "." : known_call.ID),
                        (char) reference_nucleotide,
                        SNP.getAlternateAllele(),//(SNP.allele2 == (char) reference_nucleotide ? '.' : SNP.allele2),
                        phred_score,
                        SNP.toString());

                if (known_call.callType != CallType.Unknown) {
                    out.printf("KC=%s;", known_call);
                }

                out.printf("DP=%d;", p.mappedBases.size());
                out.printf("AR=%.2f;", SolSNPCaller.CalculateAlleleBalance(p.mappedBases, reference_nucleotide));

                out.print("PL=");

                for (MappedBaseInfo b : p.mappedBases) {
                    if (b.strand) {
                        out.print(b.nucleotide);
                    } else {
                        out.print(Character.toLowerCase(b.nucleotide));
                    }
                }

                if (GENERATE_GENOTYPES && SNP.callType != CallType.NoCall) {
                    out.print("\tGT:GQ\t");

                    //genotype
                    switch (PLOIDY) {
                        case Diploid: {
                            if (SNP.callType == CallType.HomozygoteReference) {
                                out.print("0/0");
                            } else if (SNP.callType == CallType.Heterozygote) {
                                out.print("0/1");
                            } else {// if (SNP.callType == CallType.HomozygoteNonReference) {
                                out.print("1/1");
                            }
                            break;
                        }
                        case Haploid: {
                            if (SNP.callType == CallType.HomozygoteReference) {
                                out.print("0");
                            } else { // if (SNP.callType == CallType.HomozygoteNonReference) {
                                out.print("1");
                            }
                            break;
                        }
                    }

                    //genotype quality
                    double genotype_score = (SNP.genotypeProb == 1 ? MAX_CONFIDENCE : -10 * Math.log10(1 - SNP.genotypeProb));
                    out.print(":");
                    out.printf("%.1f", Math.abs(genotype_score));

                }

                out.println();
            }
        }


    }

    private void InitSummaryMetrics() {
        if (SUMMARY) {
            String summarydir_name = OUTPUT + "_summary" + File.separator;
            new File(summarydir_name).mkdir();
            try {
                summary_file = new PrintWriter(summarydir_name + "summary.txt");
                validation_file = new PrintWriter(summarydir_name + "validation.txt");
            } catch (FileNotFoundException e) {
                // we already asserted this so we should not get here
                throw new PicardException("Could not create metrics files", e);
            }

        }
    }

    private void WriteSummaryMetrics() {
        //Ti/Tv
        summary_file.write("Transition count\t" + metrics.transitions + "\n");
        summary_file.write("Transversion count\t" + metrics.transversions + "\n");
        summary_file.write("Ti/TV\t" + (double) metrics.transitions / (double) metrics.transversions + "\n");

        //allele balance
        summary_file.write("\nAllelic Balance on known calls\n--\n");
        summary_file.write("Ref\t " + metrics.balance_ref / metrics.n_ref);
        summary_file.write("\nHet\t " + metrics.balance_het / metrics.n_het);
        summary_file.write("\nHom\t " + metrics.balance_hom / metrics.n_hom);

        //mismatch counts
        summary_file.write("\nReference Mismatch Counts\n--\n");

        for (String key : metrics.mismatch_counts.keySet()) {
            Integer h2 = metrics.mismatch_counts.get(key);
            summary_file.write(key.charAt(0) + "->" + key.charAt(1) + "\t" + h2.toString() + "\t" + ((double) h2) / metrics.mismatch_total * 100 + "\n");
        }

        summary_file.close();

        ///validation table
        validation_file.print("\t");
        for (CallType c2 : CallType.values()) {
            validation_file.print(c2);
            validation_file.print("\t");
        }
        validation_file.println();

        for (CallType c1 : CallType.values()) {
            validation_file.print(c1);
            validation_file.print("\t");
            for (CallType c2 : CallType.values()) {
                SNPCallPair pair = new SNPCallPair(c1, c2);
                Long count = metrics.calltable.get(pair);
                if (count == null) {
                    validation_file.print(0);
                } else {
                    validation_file.print(count);
                }
                validation_file.print("\t");
            }
            validation_file.println();
        }
        validation_file.close();
    }

    private void InitKnownCalls() {

        FileReader known_calls_filereader;
        BufferedReader known_calls_bufferedreader;
        known_calls = new HashMap<String, HashMap<Integer, SNPCall>>();

        if (KNOWN_CALLS != null) {
            try {
                known_calls_filereader = new FileReader(KNOWN_CALLS);
                known_calls_bufferedreader = new BufferedReader(known_calls_filereader);
                false_negatives_file = new PrintWriter(OUTPUT + ".falsenegatives");

                String currentRecord;

                while ((currentRecord = known_calls_bufferedreader.readLine()) != null) {
                    String[] fields = (currentRecord.split("\t"));
                    if (fields.length == 4) {
                        if (!known_calls.containsKey(fields[1])) {
                            known_calls.put(fields[1], new HashMap<Integer, SNPCall>());
                        }

                        SNPCall k = new SNPCall();
                        k.allele1 = fields[3].charAt(0);
                        k.allele2 = fields[3].charAt(1);
                        k.ID = fields[0];

                        known_calls.get(fields[1]).put(Integer.parseInt(fields[2]), k);
                    } else {
                        throw new PicardException("Invalid 'known calls' format.");
                    }
                }
            } catch (Exception e) {
                throw new PicardException(e.getMessage());
            }

            //output file header
            WriteHeader(OUTPUT_FORMAT, false_negatives_file);

        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.exit(new SolSNP().instanceMain(args));
    }

    private SNPCall GetKnownCall(String ref_name, int position, char reference_nucleotide, SNPCall call) {

        call.callType = CallType.Unknown;
        call.allele1 = 'N';
        call.allele2 = 'N';
        call.ID = null;

        HashMap<Integer, SNPCall> ref_known_calls = known_calls.get(ref_name);

        if (ref_known_calls != null) {
            SNPCall known_call = ref_known_calls.get(position);

            if (known_call != null) // call
            {
                call.allele1 = known_call.allele1;
                call.allele2 = known_call.allele2;
                call.ID = known_call.ID;

                if (call.allele1 == call.allele2) {
                    if (call.allele1 == reference_nucleotide) {
                        call.callType = CallType.HomozygoteReference;
                    } else {
                        call.callType = CallType.HomozygoteNonReference;
                    }

                } else {
                    call.callType = CallType.Heterozygote;
                }

            }

        }

        return call;
    }
    
    public static String getSampleName(File bam){
        final SAMFileReader in = new SAMFileReader(bam);
        final SAMFileHeader header = in.getFileHeader();
        if(header.getReadGroups().isEmpty()){
            return bam.getName().substring(0, bam.getName().lastIndexOf('.'));
        }else{
            final SAMReadGroupRecord rg = header.getReadGroups().get(0);
            return rg.getSample();
        }
    }
}
