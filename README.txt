--------------------------------------------------------------------------------
--DESCRIPTION--
--------------------------------------------------------------------------------

ISGPipeline is a java program that combines SNPs into a matrix for genotyping. 
Functionality includes snp calling using gatk and/or mummer, identification of 
ambiguous SNPs, identification of regions of no coverage using samtools and 
mummer, and annotation of SNPs using genbank.

ISGTools is a collection of java command line utilities that manipulate the 
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

To run the project from the command line, go to the dist folder and
type the following:

java -jar ISGPipeline.jar -S ISGPipelineQScript.scala

After running the above command you will get an error message with a listing of 
optional/required arguments to use.

Steps to run an analysis:

1. To initialize a project, create an empty directory and run ISGPipeline using 
the required arguments.

ex. java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
-isg analysis1 \
-bwa /path/to/bwa \
-mummer /path/to/mummer \
-gatk /path/to/gatkjar
-run

This will create an "analysis1" directory in your working directory. Inside the 
"analysis1" directory you will see several empty directories.


2. Copy the reference fasta file to the "analysis1" directory. This file must be named: "ref.fasta".
- If you have sequence files (ie fastqs), put them in the "analysis1/reads" directory.
- If you have bams, put them and their index files (bai) you want analyzed in the "analysis1/bams" directory. 
- If you have fastas, put them in the "analysis1/fastas" directory.

6. Run ISGPipeline again. 

ex. java -jar ISGPipeline.jar -S ISGPipelineQScript.scala \
-isg analysis1 \
-bwa /path/to/bwa \
-mummer /path/to/mummer \
-gatk /path/to/gatkjar
-run

The ISG argument must be the path to the directory created in step 1. 
The MUMMER argument must be the path to the root directory where Mummer is installed.

7. When ISGPipeline is finished there will be a file called "isg_out.tab" in the 
"analysis1/out" directory. This file is a matrix of ALL SNPs found by the pipeline. 


--------------------------------------------------------------------------------
--DIRECTORY STRUCTURE--
--------------------------------------------------------------------------------

ISGPipeline creates the following directory structure for each analysis:

ROOT --> |---mummer
         |
         |---bams
         |
         |---coverage
         |
         |---fastas
         |
         |---out
         |
         |---vcf
         |
         |---ref.fasta


ROOT
the highest level directory ISGPipeline uses for an analysis. You should name 
this file something meaningful describing your analysis. 

ROOT-->mummer
directory where mummer output is stored. Namely, coords and snps. ISGPipeline will automatically run
mummer on any fasta files found in the fastas directory and write the results to this directory.

ROOT-->coverage 
directory containing coverage intervals for each bam and fasta used in the analysis. ISGPipeline will detect which
regions are covered and write the results to this directory.

ROOT-->fastas 
directory containing fasta files to compare in the analysis. All fasta files in this directory will be used in the analysis.

ROOT-->genBank
directory containing genbank files. Genbank files are optional, but if provided, they will be used to annotate the snps.

ROOT-->out
directory containing ISGPipeline results. Two files will be written here: out.tab and merged.vcf

ROOT-->vcf
directory containing vcf files of all variants detected by mummer and solsnp. A vcf file is created for each genome in the analysis.

ROOT-->vcf-->snps
directory containing snps that pass filtering.

ROOT-->vcf-->ambiguous
directory containing snps that DO NOT pass filtering.


--------------------------------------------------------------------------------
--DEPENDENCIES--
--------------------------------------------------------------------------------

ISGPipeline requires several external programs (dependencies) to run properly. Each dependency must 
be installed on the machine where ISGPipeline is run. You can specify the path 
to each dependency using the options from the command line.

-MUMmer 3+ (http://sourceforge.net/projects/mummer/)
-GATK
-BWA (optional)
