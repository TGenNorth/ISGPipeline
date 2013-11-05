--------------------------------------------------------------------------------
--DESCRIPTION--
--------------------------------------------------------------------------------

ISG (In-Silico Genotyper) uses BWA to align NGS reads against the reference 
(creating bam files), and it uses MUMmer to align fasta-formatted, sequenced genomes to call SNPs. It then 
uses the GATK Unified Genotyper to call and determine ambiguity of SNPs from the 
bam files, and puts them into vcf format.  ISG performs "intelligent" alignment, 
using data at whatever stage they are provided (e.g., reads, bams, vcfs, fastas). 
Adding further sample files and rerunning will process the new additions and create 
a new variant matrix. All previously called variant loci are checked in the new samples, 
and any newly found variant positions are checked/verified in the earlier samples.

ISG creates a matrix of SNPs across multiple taxa. At its core, ISG 
merges single sample VCF files into a matrix. However, it is more complicated than 
that because not every sample has genotype information for a particular locus and 
not every genotyped locus is necessarily correct. In fact, ISG expects that many 
of the variants called are false positives in order to prevent incorrectly assuming 
the reference state. Therefore, two important steps must be added to the merging 
process. First, variants from each sample must be analyzed for ambiguity and marked 
as such before further processing. Second, samples without genotype information 
for a locus must be genotyped by inspecting the reads covering the locus.



--------------------------------------------------------------------------------
--RELEASE NOTES--
--------------------------------------------------------------------------------

v0.16.9

    -New, easier way of running ISG. Refer to "RUNNING ISGPipeline" section for details.
    -Include fasta representation for each output matrix.
    -Improved error message descriptions.
    -Added early validation of input files and arguments.
    -Optionally include calculation of pattern number fields.
    -Display argument defaults on splash screen.
    -Calculate allele frequency from AD field instead of pre-calculated AF field.
    -Modified output directory structure. Refer to "OUTPUT FILES" section for details.


--------------------------------------------------------------------------------
--RUNNING ISGPipeline--
--------------------------------------------------------------------------------

First, make sure that you have all the dependencies installed on your machine. 
Please review the "DEPENDENCIES" section of the README to find out what programs 
are required and where to get them.


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
--Running Options--
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

    Path to bwa executable.

 -mummer,--pathtomummer <pathtomummer>           

    Path to directory containing mummer executables.

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

    Run bwa mem algorithm. Setting this flag requires bwa version 0.7+

 --includepattern                                

    Include pattern fields in output matrix.

 -nt,--num_threads <num_threads>

    How many CPU threads should be allocated?


--------------------------------------------------------------------------------
--INPUT FILES--
--------------------------------------------------------------------------------

All input files must reside at the root of a single directory to be included in an ISG analysis. 
Within the input directory, ISG uses file extensions to determine the type of file. Below is a list of file 
types and supported extensions. Any file with an extension not listed below will be
ignored by ISG.

BAM     - .bam
FASTQ   - .fastq, .fastq.gz, sequence.txt, sequence.txt.gz
VCF     - .vcf
GENBANK - .gbk, .gb
FASTA   - .fasta, .fa

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

ISG will annotate SNPs in the output matrix files if genbank file(s) are provided 
in the input directory. ISG matches a sequence in the reference fasta file with 
a genbank file by using the genbank's filename. For example, consider the following 
reference fasta file:

>ABC
ATCGA....ATGC
>XYZ
ATTC....AATTC

ISG will use the sequence headers (ABC and XYZ) to find the corresponding genbank
files (ABC.gbk and XYZ.gbk) in the input directory. Thus, it is important that 
there is a genbank file for each sequence in the reference fasta and that each 
genbank file is named identically to the header of the corresponding sequence in 
the reference fasta file.  

--------------------------------------------------------------------------------
--OUTPUT FILES--
--------------------------------------------------------------------------------

Depending on what input files are specified, ISG may generate a lot of files in 
the output directory. These files are organized in a directory structure that 
facilitates finding the files by sample name.

There are two subdirectories of the output directory that ISG creates for every 
analysis: "samples" and "ref". The "samples" directory contains a subdirectory for each 
sample. Inside each individual sample directory exists all the intermediate files 
ISG generated for that particular sample. For example, let's say that your analysis 
includes a raw reads file named "ABC.fastq". ISG will create a directory named 
"ABC" (for a discussion on how ISG determines sample names see "SAMPLE NAMES"). 
Inside that directory would be the following files (listed by extension): bam, 
bai, bed, summary, and vcf. 

The "ref" directory is created to store duplicated regions found in the reference as 
well as any duplicated regions found in completely sequenced genomes. As such,
there will always be a file named "ref.interval_list" that contains the repeated 
regions found in the reference. Additionally, if other completely sequenced genomes 
exists there will be files corresponding to the repeats found in those genomes.

The rest of the output files reside at the root of the output directory and fall 
into one of three categories: "all", "dups", and "unique". Each category contains 
a SNP matrix file and a file representing the SNP matrix in a fasta format. 

The "all" category contains variants detected by the pipeline where at least one sample 
contains a "real" variant (i.e. not called the reference, missing, or ambiguous). 
In addition to the SNP matrix and fasta file, the "all" category has an annotated 
SNP matrix (all.variants.final.txt) and a statistics file (all.variants.final.txt.stats).

The "unique" category contains a subset of variants from the "all" category that 
do not overlap a duplicated (repeated) region.

The "dups" category contains a subset of variants from the "all" category that 
fall within a duplicated region.

ambiguous.variants.txt - variants detected by the pipeline, but were marked as 
ambiguous. None of the samples contain a "real" variant in this file.
 

--------------------------------------------------------------------------------
--OPTIONS FILE--
--------------------------------------------------------------------------------

ISGPipeline allows the user to customize how each external program is run through 
an options file provided when running ISGPipeline. An options file with default 
values is provided in the dist/ directory of an ISGPipeline build. Below is an 
example of using the --optionsfile argument:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
     -isg analysis1 \
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

ISGPipeline requires several external programs (dependencies) to run properly. Each dependency must 
be installed on the machine where ISGPipeline is run. You can specify the path 
to each dependency using the options from the command line.

-MUMmer 3+ (http://sourceforge.net/projects/mummer/)
-GATK (2.5+)
-BWA
