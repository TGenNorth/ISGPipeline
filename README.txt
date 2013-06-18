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
--PROCESSES--
--------------------------------------------------------------------------------

SNP calling -
ISGPipeline will run the UnifiedGenotyper on each bam file. 
//TODO Explain how UnifiedGenotyper is run

SNP calling on input sequence files will be done using MUMmer.

coverage
ISGPipeline will calculate coverage for each genome. For bams, an area will be 
considered covered if it has at least MIN_COVERAGE (default value is 3) reads. For 
fastas, an locus will be considered covered if it aligns to the reference using MUMmer.



--------------------------------------------------------------------------------
--RUNNING ISGPipeline--
--------------------------------------------------------------------------------

To get a listing of optional/required arguments type the following:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala


Steps to run an analysis:

1. To initialize a project, create an empty directory and run ISGPipeline using 
the required arguments.

ex. java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
-isg analysis1 \
-run

This will create an "analysis1" directory in your working directory. Inside the 
"analysis1" directory you will see several empty directories.


2. Copy (or symlink) input files to corresponding directory. Below is a diagram 
of the directory structure corresponding to input file extensions:

"analysis1" --> |---reads (.fastq, sequence.txt)
                |
                |---bams (.bam and .bai)
                |
                |---fastas (.fasta)
                |
                |---vcf (.vcf)
                |
                |---ref.fasta (required)

PLEASE NOTE: 
Copy the reference fasta file to the root of the "analysis1" directory. 
This file must be named "ref.fasta". So, in this example, we would have the
file "analysis1/ref.fasta".

3. Run ISGPipeline again, this time with arguments to the necessary dependencies 
(ie bwa, mummer, etc). 

ex. java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
-isg analysis1 \
-bwa /path/to/bwa \
-mummer /path/to/mummer \
-gatk /path/to/gatkjar
-run

The "-isg" argument must be the path to the directory created in step 1. 
The "-mummer" argument must be the path to the root directory where Mummer is installed.

When ISGPipeline is finished there will be a file called "isg_out.tab" in the 
"analysis1/out" directory. This file is a matrix of ALL SNPs found by the pipeline. 


--------------------------------------------------------------------------------
--DIRECTORY STRUCTURE--
--------------------------------------------------------------------------------

ISGPipeline creates the following directory structure for each analysis. Following
each directory is an indication of how files within that directory are used by 
ISG. An "O" means that ISGPipeline Outputs files into this directory while an "I" 
means that ISGPipeline reads files from this directory Input by the user. Some 
directories can function as both Input and Output. For example, you may have already 
aligned the reads of some genomes in your analysis, but you have the raw reads 
from other genomes that you would like ISGPipeline to align. In this case, you 
would put the aligned reads (.bam and .bai) files in the bams/ directory and the 
raw reads (.fastq) in the reads/ directory. ISGPipeline will use the bams/ directory 
to write the results of aligning the raw reads and to store the .bam files that were 
aligned prior to running ISGPipeline.

ROOT --> |---mummer (O)
         |
         |---reads (I)
         |
         |---bams (I/O)
         |
         |---coverage (O)
         |
         |---fastas (I)
         |
         |---out (O)
         |
         |---vcf (I/O)
         |
         |---ref.fasta


ROOT
the highest level directory ISGPipeline uses for an analysis. You should name 
this file something meaningful describing your analysis. 

ROOT-->mummer
directory where mummer output is stored. Namely, coords and snps. ISGPipeline will automatically run
mummer on any fasta files found in the fastas directory and write the results to this directory.

ROOT-->reads 
directory containing reads files (.fastq, sequence.txt) to align with bwa.

ROOT-->bams 
directory containing aligned reads files (.bam) to call SNPs on.

ROOT-->coverage 
directory containing coverage intervals for each bam and fasta used in the analysis. ISGPipeline will detect which
regions are covered and write the results to this directory.

ROOT-->fastas 
directory containing whole genome (.fasta) files to align using nucmer.

ROOT-->out
directory containing ISGPipeline results. Two files will be written here: out.tab and merged.vcf

ROOT-->vcf
directory containing .vcf files of all variants detected by mummer and gatk. A .vcf file is created for each genome in the analysis.


--------------------------------------------------------------------------------
--OPTIONS FILE--
--------------------------------------------------------------------------------

ISGPipeline allows the user to customize how each external program is run through 
an options file provided when running ISGPipeline. This file is a key/value 
properties file where each key represents 
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
