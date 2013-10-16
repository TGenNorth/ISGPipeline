--------------------------------------------------------------------------------
--DESCRIPTION--
--------------------------------------------------------------------------------

ISGPipeline is a java program that combines SNPs into a matrix for genotyping. 
Functionality includes snp calling using gatk and/or mummer, identification of 
ambiguous SNPs, identification of regions of no coverage using GATK and 
mummer.

ISGTools is a collection of java command line utilities that manipulate/annotate the 
results of ISGPipeline. To find out more read the README in the isgtools directory.


--------------------------------------------------------------------------------
--RUNNING ISGPipeline--
--------------------------------------------------------------------------------

First, make sure that you have all the dependencies installed on your machine. 
Please review the DEPENDENCIES section of the README to find out what programs 
are required and where to get them.

To get a listing of optional/required arguments type the following:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala


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
    -run

Step 3:

    Be patient and wait for the analysis to finish. See "OUTPUT" section of the 
    README for a discussion on the output files and directory structure.

--------------------------------------------------------------------------------
--INPUT FILES--
--------------------------------------------------------------------------------

To make life easier, ISG allows you to include as many files as you want 
ISG uses file extensions to determine the type of file. Below is a list of file 
types and supported extensions:

BAM     - .bam
FASTQ   - .fastq, .fastq.gz, sequence.txt, sequence.txt.gz
VCF     - .vcf
GENBANK - .gbk, .gb
FASTA   - .fasta, .fa


--------------------------------------------------------------------------------
--OUTPUT FILES--
--------------------------------------------------------------------------------

Depending on what input files are specified, ISG may generate a lot of files in 
the output directory. Thus, it is important to understand its structure so that 
you may quickly find the files of most interest to you.

There are two subdirectories that ISG creates for every analysis: samples and ref. 
The samples directory contains a subdirectory for each sample. Inside each individual
sample directory exists all the intermediate files ISG generated for that particular
sample. For example, if your analysis included a file named "ABC.fastq" then a 
directory would exist with the path "out/samples/ABC/". Inside that directory would 
be the following files (listed by extension): bam, bai, bed, summary, and vcf. 

The "ref" directory is created to store duplicated regions found in the reference as 
well as any duplicated regions found in completely sequenced genomes.

The rest of the output files reside at the root of the output directory and are 
described below:

all.variants.txt - variants detected by the pipeline where at least one sample 
contains a "real" variant (i.e. not called the reference, missing, or ambiguous). 

ambiguous.variants.txt - variants detected by the pipeline, but were marked as 
ambiguous. None of the samples contain a "real" variant in this file.

unique.variants.txt - variants from the all.variants.txt that do not overlap a 
duplicated (repeated) region.

dups.variants.txt - variants from the all.variants.txt that fall within a 
duplicated region.

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
-GATK
-BWA (optional)
