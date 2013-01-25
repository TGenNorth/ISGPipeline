--------------------------------------------------------------------------------
--DESCRIPTION--
--------------------------------------------------------------------------------

ISGPipeline is a java program that combines SNPs into a matrix for genotyping. 
Functionality includes snp calling using solsnp and/or mummer, identification of 
ambiguous SNPs, identification of regions of no coverage using samtools and 
mummer, and annotation of SNPs using genbank.

ISGTools is a collection of java command line utilities that manipulate the 
results of ISGPipeline. To find out more read the README in the isgtools directory.

--------------------------------------------------------------------------------
--PROCESSES--
--------------------------------------------------------------------------------

SNP calling -
ISGPipeline uses several different methods for calling SNPs. SNP calling on input
bam files will be done using GATK or SolSNP. If GATK is specified, then ISGPipeline 
will run the UnifiedGenotyper on each bam file. 
//TODO Explain how UnifiedGenotyper is run
If GATK is not specified, then SolSNP will be run.
//TODO explain SolSNP 
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

java -jar "ISGPipeline.jar" 

Steps to run an analysis:

1. To initialize a project run ISGPipeline using only the ISG argument. Use a meaningful name that describes your analysis.

ex. java -jar ISGPipeline.jar ISG=analysis1. 

This will create an "analysis1" directory in your working directory. Inside the "analysis1" directory you will see several empty directories.
 
2. Copy the reference fasta file to the "analysis1" directory. This file must be named: "ref.fasta".

3. Put the bams and their index files (bai) you want analyzed in the "analysis1/bams" directory. 

4. Put the public genomes you want analyzed in the "analysis1/fastas" directory.

5. If you have any genbank files cooresponding to your reference place them in the "analysis1/genbank" directory. There must be a genbank file for each chromosome in the reference. 

6. Run ISGPipeline again, but this time specifiy any additional arguments. The only required arguments are ISG and MUMMER. 

ex. java -jar ISGPipeline.jar ISG=analysis1 MUMMER=path/to/mummer

The ISG argument must be the path to the directory created in step 1. The MUMMER argument must be the path to the root directory where Mummer is installed.

7. When ISGPipeline is finished there will be a file called "isg_out.tab" in the "analysis1/out" directory. This file is a matrix of ALL SNPs found by the pipeline. 

Please Note:
ISGPipeline runs several external programs which can take a while to run. Solsnp 
takes the longest, as it is being run in AllCallable mode. I do this so that I 
can get the calls at each position of the genome which is why I set 
MINIMUM_COVERAGE=0. After Solsnp completes, the ambiguous calls and 
snp calls are filtered and placed into the directories vcf/ambiguous and vcf/snps 
respectively. Then, ISGPipeline merges the snps using vcftools. Once this 
completes, ISG is run on the merged vcf file and looks for positions where no 
snp was called. If the position is covered and was not called ambiguous it gets 
the reference state. If the position is covered but is ambiguous it gets called 
'N'. These results are written to out/isg_out.tab.

Command-line options

ISG : 
path to the root directory containing all the files required by the analysis.

MUMMER: 
Path to MUMmer.

GATK: 
Path to GenomeAnalysisTK.jar (version 2.1 or later)

SOLSNP_OPTIONS_FILE
Path to SolSNP options file. To get a list of all available options run SolSNP without any arguments.

MIN_COVERAGE
Minimum amount of reads to be considered covered. Default value is 3.

MIN_SNP_COVERAGE
Minimum amount of reads to call a snp. If a SNP is called by solSNP, but the coverage is below MIN_SNP_COVERAGE than that SNP will be considered ambiguous and will be called 'N' in the output matrix file. Default value is 3.

MIN_QUAL
Minimum genotype quality to be considered a snp. If a SNP is called by solSNP, but the genotype quality is below MIN_QAUL than that SNP will be considered ambiguous and will be called 'N' in the output matrix file. Default value is 4.

FILTER
Solsnp's minimum confidence score allowed for calls. Default value is .85

NUM_THREADS
Number of threads to run.

overwrite
Overwrite existing files

confusion between MIN_COVERAGE and MIN_SNP_COVERAGE.

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
         |---genBank
         |
         |---out
         |
         |---vcf-->|---snps
         |         |
         |         |---ambiguous
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
-GATK (optional)
