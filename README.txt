--------------------------------------------------------------------------------
--DOWNLOAD--
--------------------------------------------------------------------------------
Compiled JAR files can be downloaded from:

https://sourceforge.net/projects/insilicogenotyp/

--------------------------------------------------------------------------------
--DESCRIPTION--
--------------------------------------------------------------------------------

ISG (In-Silico Genotyper) creates a matrix of SNPs across multiple taxa. At its core, ISG 
merges single sample variant calls (i.e., SNPs and indels in vcf format) for a shared 
sequence region into a matrix. Because not every sample has genotype information for a 
particular locus and not every genotyped locus is necessarily correct, ISG expects that 
many of the variants called are false positives in order to prevent incorrectly assuming the 
reference state. Therefore, two important steps have been added to the merging process. 
First, variants from each sample are analyzed for ambiguity and marked as such before 
further processing. Second, samples without genotype information for a locus are genotyped 
in an intelligent way.

ISG uses BWA to align NGS reads against the reference (creating bam files), and it uses 
MUMmer to align fasta-formatted, sequenced genomes and call SNPs. It then uses the GATK 
Unified Genotyper to call and determine ambiguity of SNPs from the bam files, and puts 
them into VCF format.  ISG performs "intelligent" alignment, using data at whatever stage 
they are provided (e.g., reads, bams, vcfs, fastas). Adding further sample files and 
rerunning will process the new additions and create a new variant matrix. All previously 
called variant loci are checked in the new samples, and any newly found variant positions 
are checked/verified in the earlier samples.
 
The ISG Tools are a collection of java command line utilities that manipulate/annotate the 
results of ISGPipeline (summarized below):
  CalculateMismatch.jar -calculate mismatch distance between adjacent SNPs
  CalculatePattern.jar - calculate pattern of SNP states among samples for each locus
  CalculateStatistics.jar - calculate overall variant statistics for each sample 
  ClassifyMatrix.jar - classify SNPs in matrix as (non)synonymous, intergenic, etc.
  CleanMatrix.jar - remove matrix loci containing ambiguous or missing alleles 
  DetermineStatus.jar - marks status of each locus (e.g., clean, ambiguous, missing, 
   duplicated, etc.)
  FilterMatrix.jar - filter a matrix based on specific parameters
  FindParalogs.jar - find and mark duplicated regions in matrix based on self comparisons 
   of reference and other individual whole genomes
  RemoveGenomes.jar - removes user-specified genomes from a matrix
  ISGToolsBatchRunner.jar - runs a user-specified set of ISG Tools sequentially in batch mode

--------------------------------------------------------------------------------
--RELEASE NOTES--
--------------------------------------------------------------------------------

v0.16.10-2

    - Filtering of duplicate loci in query genomes is optionally selected using "--filterdups" flag.
    - Fixed memory leak when writing large pseudo-fasta files of SNPs for each taxon.

v0.16.10-1

    - The reference FASTA file must be placed in the INPUT directory with all other input files. 
The -R flag must be used to designate the reference file, which will be ignored as a sample file in the comparisons.

v0.16.10

    -Added SnpEff for annotating SNPs. Refer to "ANNOTATIONS" section for details.

v0.16.9-1

    -Added "clean.unique.variants" to output directory.
    -Fixed issue when the reference fasta is included in the input directory.
    -Display meaningful error message when the "-R" required option is omitted.

v0.16.9

    -New, easier way of running ISG. Refer to "RUNNING ISGPipeline" section for details.
    -Include fasta representation for each output matrix.
    -Improved error handling for validating input files.
    -Optionally include calculation of pattern number fields.
    -Display argument defaults on splash screen.
    -Calculate allele frequency from AD field instead of pre-calculated AF field.
    -Modified output directory structure. Refer to "OUTPUT FILES" section for details.


--------------------------------------------------------------------------------
--RUNNING ISGPipeline--
--------------------------------------------------------------------------------

First, make sure that you have all the dependencies installed on your machine. 
Please review the "DEPENDENCIES" section of this README to find out what programs 
are required and where to get them.

To get a listing of optional/required arguments type the following:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala -h


Steps to run an analysis:

Step 1:  
    
    Organize all of your input files (i.e. fastas, fastqs, etc) into a single 
    directory. If you're not sure what file types are acceptable please refer to 
    the "INPUT FILES" section of this README. 

Step 2:
  
    Run ISGPipeline. An example is provided below:

    java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
    -I /path/to/input_dir \
    -O /path/to/output_dir \
    -R /path/to/reference.fasta \
    -bwa /path/to/bwa \
    -mummer /path/to/mummer_dir \
    -gatk /path/to/gatk.jar \
    -nt 1 \
    --allow_potentially_misencoded_quality_scores
    -run

Step 3:

    Wait for the analysis to finish. See "OUTPUT" section of the README for a 
    discussion on the output files and directory structure.

--------------------------------------------------------------------------------
--RUNNING OPTIONS--
--------------------------------------------------------------------------------

To get a listing of optional/required arguments type the following:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala -h

Options:

 -I,--input_dir <input_dir>                       

    Directory containing input files (fastqs, fastas, bams, etc). Refer to "INPUT FILES"
    section for details.

 -O,--output_dir <output_dir>                    

    Directory where output files will be written. Refer to "OUTPUT FILES" section for 
    details.

 -R,--reference_sequence <reference_sequence>    

    Reference sequence fasta file. If you have genbank annotations for this reference 
    refer to "ANNOTATIONS" section for details.

 -gatk,--gatkjarfile <gatkjarfile>               

    Path to GATK jar file. Requires version 2.5+

 -bwa,--pathtobwa <pathtobwa>                    

    Path to bwa executable. Requires version 0.6.2+

 -mummer,--pathtomummer <pathtomummer>           

    Path to directory containing mummer executables. Requires version 3.23+

 -eff,--snp_eff <snp_eff>                                       

    Path to SnpEff jar file. Requires version 3.3h+

 -db,--snp_eff_database <snp_eff_database>                      

    SnpEff database ID.

 -chr_tbl,--chrom_translation_table <chrom_translation_table>

    Path to chromosome translation table. Refere to "ANNOTATIONS" section for 
    details.

 --optionsfile <optionsfile>                     

    Path to options file. Refere to "OPTIONS FILE" section for details.

 --minaf <minaf>                                 

    The minimum allele frequency of an alternative base needed to call a SNP. 
    Default value: 0.75

 --minqual <minqual>                             

    The minimum Phred scaled probability needed to call a SNP. 
    Default value: 30

 --mingq <mingq>                                 

    The minimum genotype quality needed to call a variant. 
    Default value: 4

 --mindp <mindp>                                 

    The minimum depth of reads needed to call a variant. 
    Default value: 3

 --allow_potentially_misencoded_quality_scores   

    Do not fail when encountering base qualities that are too high and that seemingly 
    indicate a problem with the base quality encoding of the BAM file.

 --usebwamem                                     

    Run bwa mem algorithm. Setting this flag requires bwa version 0.7.5a+

 --includepattern                                

    Include pattern fields in output matrix.

 -nt,--num_threads <num_threads>
   
    How many CPU threads should be allocated?
 
 -pbs
 
 	Use PBS queueing system for job scheduling
 	 
--------------------------------------------------------------------------------
--INPUT FILES--
--------------------------------------------------------------------------------

All input files must reside at the root of a single directory to be included in an ISG analysis. 
Within the input directory, ISG uses file extensions to determine the type of file. 
Below is a list of file types and supported extensions. 
Any file with an extension not listed below will be ignored by ISG.

BAM     - .bam (NGS reads aligned against a reference)
FASTQ   - .fastq, .fastq.gz, sequence.txt, sequence.txt.gz (NGS reads)
VCF     - .vcf (variants from bams and/or fastas)
GENBANK - .gbk, .gb (GenBank annotations of reference)
FASTA   - .fasta, .fa (contigs or whole genomes in fasta format)

--------------------------------------------------------------------------------
--SAMPLE NAMES--
--------------------------------------------------------------------------------

ISG uses sample names to identify a particular input genome. As such, the 
sample name must be unique across all input genomes. Any duplicated sample name 
will cause ISG to terminate with an error. Sample names are used throughout the 
pipeline as well as in the output matrix files, thus it is imperative that you 
designate meaningful sample names to each input genome when possible. 

ISG uses filenames and, in some cases, file headers to determine the sample name. 
In the simplest case, the sample name is the filename less the extension. This is 
true for any FASTA input files and single-end FASTQs. For example, if an input 
file had the name "ABC.fasta" ISG would use "ABC" as the sample name to identify 
the genome.

Determining the sample name of a paired-end fastq separated into two distinct files 
is more challenging because the sample name as well as the pairing information is 
contained within the filename. For example, if two input files named "XYZ_1.fastq" 
and "XYZ_2.fastq" were included in the input directory, ISG would recognize the 
two files as a pair and use "XYZ" as the sample name to identify it. If ISG recognizes 
an input file as paired and cannot locate the mate file, it will terminate with 
an error. ISG determines if an input FASTQ is paired by using the following 
regular expression:

(.*)_[0-9]+_([12])_sequence\\..
(.*)_[ATCG]+_L[0-9]+_R([12])_[0-9]+\\..*
(.*)_S[0-9]+_L[0-9]+_R([12])_[0-9]+\\..*
(.*)_R([12])[_\\.].*
(.*)_([12])\\..*

If a BAM or VCF is included in the input directory, ISG will read the file's header 
to determine what sample name to identify the genome by. For BAMs, ISG will look at 
the ReadGroup's SM field while for VCFs, ISG will look at the listed genotypes. 
In both cases, if more than one sample name is found, ISG will exit immediately 
and display an error.

--------------------------------------------------------------------------------
--ANNOTATIONS--
--------------------------------------------------------------------------------

ISG will annotate SNPs in the output matrix files using SnpEff. In order for 
SnpEff to run, you must specify the location of the SnpEff jar file (-eff) and the 
SnpEff database identifier (-db) of the genome you are using as the reference. To see 
a list of all SnpEff databases you can run the following command:

java -jar SnpEff.jar databases

Once you have found the SnpEff database that corresponds to your reference sequence 
make sure that the sequence headers match with the chromosome names found in the 
SnpEff database. If you do not know the chromosome names used by SnpEff, you can 
do a 'dry run' of ISG by running the ISG command without the "-run" option. If the 
'dry run' completes successfully then you are good to go. However, if 
you see an error message referring to mismatching chromosome names then you will 
need to fix the chromosome names in your reference fasta or provide a translation 
table before continuing. 

 Translation Table

A translation table is a file of key-value pairs that lists the sequence names of 
your reference fasta file and the corresponding SnpEff database chromosome names.
For example, if your reference fasta had the names "AmesA,AmesApX01,AmesApX02" and 
the SnpEff database had the chromosome names ",pX01,pX02" (the first name is 
intentionally left blank) then your translation table would look like this:

AmesA=
AmesApX01=px01
AmesApX02=px02
  

**THE FOLLOWING IS DEPRECATED**

ISG will annotate SNPs in the output matrix files if GenBank file(s) are provided 
in the input directory. ISG matches a sequence in the reference fasta file with 
a GenBank file by using the GenBank filename. For example, consider the following 
reference fasta file:

>ABC
ATCGA....ATGC
>XYZ
ATTC....AATTC

ISG will use the sequence headers (ABC and XYZ) to find the corresponding GenBank
files (ABC.gbk and XYZ.gbk) in the input directory. Thus, it is important that 
there is a GenBank file for each sequence in the reference fasta and that each 
GenBank file is named identically to the header of the corresponding sequence in 
the reference fasta file.  

--------------------------------------------------------------------------------
--OUTPUT FILES--
--------------------------------------------------------------------------------

Depending on what input files are specified, ISG may generate a lot of files in 
the output directory. These files are organized in a directory structure that 
facilitates finding the files by sample name.

There are two subdirectories of the output directory that ISG creates for every 
analysis: "samples" and "ref". The "samples" directory contains a subdirectory for each 
sample. Inside each individual sample directory exists all the intermediate files ISG 
generated for that particular sample. For example, let's say that your analysis 
includes a raw reads file named "ABC.fastq". ISG will create a directory named 
"ABC" (for a discussion on how ISG determines sample names see "SAMPLE NAMES"). 
Inside that directory are the following files (listed by extension): bam, 
bai, bed, summary, and vcf. 

The "ref" directory is created to store duplicated regions found in the reference as 
well as any duplicated regions found in completely sequenced genomes. As such,
there will always be a file named "ref.interval_list" that contains the repeated 
regions found in the reference. Additionally, if other completely sequenced genomes 
exist, there will be files corresponding to the repeats found in those genomes.

The rest of the output files reside at the root of the output directory and fall 
into one of three categories: "all", "dups", and "unique". Each category contains 
a SNP matrix file and a file representing the SNP matrix in a fasta format. 

The "all" category contains variants detected by the pipeline where at least one sample 
contains a "real" variant (i.e., not called the reference state, missing, or ambiguous). 
In addition to the SNP matrix and fasta file, the "all" category contains an annotated 
SNP matrix (all.variants.final.txt) and a statistics file (all.variants.final.txt.stats).

The "unique" category contains a subset of variants from the "all" category that 
do not overlap a duplicated (repeated) region.

The "dups" category contains a subset of variants from the "all" category that 
fall within any duplicated region(s).

Ambiguous variants (ambiguous.variants.txt) are variants detected by the pipeline, 
but which were marked as ambiguous. None of the samples contain a "real" variant in this file.
 

--------------------------------------------------------------------------------
--OPTIONS FILE--
--------------------------------------------------------------------------------

ISGPipeline allows the user to customize how each external program is run using 
an options file provided when running ISGPipeline. An options file with default 
values is provided in the dist/ directory of an ISGPipeline build. Below is an 
example of using the --optionsfile argument:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
     -I in \
     -O out \
     --optionsfile path/to/optionsfile.txt
     -run

This file is a key/value properties file where each key represents 
an argument to one of the programs ISGPipeline runs and the value corresponds to 
how that argument is to be used. The key is formatted as follows 
<program identifier>.<argument name>, where the program identifier is a unique id 
internal to ISGPipeline that identifies the program this argument applies to. 
The "argument name" is the name of the argument for the program. For example, 
the following two lines are found in the options file:

UnifiedGenotyper.outMode=EMIT_VARIANTS_ONLY
#UnifiedGenotyper.contamination_fraction_to_filter=<double>

In this example "UnifiedGenotyper" is the unique program identifier specifying 
the UnifiedGenotyper program in the GATK. The first line specifies the "outMode"
argument and its value "EMIT_VARIANTS_ONLY". Since this line is uncommented 
(doesn't begin with a '#') it is active and will be used whenever Unified Genotyper
is executed by ISGPipeline. The second line specifies the argument
"contamination_fraction_to_filter" with a placeholder value <double>. This argument 
is not active because it begins with '#' and will be ignored by ISGPipeline. If 
you would like to set the "contamination_fraction_to_filter" argument, simply 
uncomment that line by removing the '#' character and replace the <double> with 
a numeric value within the range specified in GATK's documentation.

--------------------------------------------------------------------------------
--DEPENDENCIES--
--------------------------------------------------------------------------------

ISGPipeline requires several external programs (dependencies) to run properly. 
Each dependency must be installed on the machine where ISGPipeline is run. 
You can specify the path to each dependency using the options from the command line.

-MUMmer 3.3+ (http://sourceforge.net/projects/mummer/)
-GATK (2.5+)
-BWA (0.6.2+)
-SnpEff 3.3+ (optional)

---------------------------------------------------------------------------------
--TEST DATA--
---------------------------------------------------------------------------------

To test that ISG and all dependencies are installed correctly, test data is included
with the distribution.  The files consist of:

-Reference chromosome (Yersinia pestis Colorado 92)
-Short, paired-end reads from Yersinia pestis EV76 (SRR069197).  Reads have been
 sub-sampled to a reasonable level
-Binary alignment map (BAM) file.  BAM file was generated with BWA-MEM from short
 reads from Y. pestis EV76
-Variant call format (VCF) file.  VCF was generated with GATK from BAM file included above.
-Genome assembly in FASTA format.  Short reads were from EV76 were assembled with SPAdes.
-GenBank file for CO92.  This will be used for annotation

All of these files, except for the reference genome are in test_data/ISG_in.  To run, enter
the ISG folder and do:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala -I test_data/ISG_in/ -O isg_out \
-R test_data/YP_CO92.fasta -bwa PATH_TO/bwa -gatk PATH_TO/GenomeAnalysisTK.jar 
--usebwamem -mummer PATH_TO/MUMmer3.23

If this completes without incident, add "-run" to the end of the script and re-run.  This
dataset should finish, even on a laptop, in a reasonable time frame

