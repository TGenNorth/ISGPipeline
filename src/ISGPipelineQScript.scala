  /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import org.broadinstitute.sting.alignment.bwa.BWTFiles
import org.broadinstitute.sting.queue.QScript
import org.broadinstitute.sting.queue.function.JavaCommandLineFunction
import org.broadinstitute.sting.queue.extensions.gatk.FastaStats
import org.broadinstitute.sting.queue.extensions.picard.AddOrReplaceReadGroups
import org.broadinstitute.sting.queue.extensions.picard.MarkDuplicates
import bwa._
import isg.input._
import isg.util.SequenceFilePair
import isg.util.SequenceFilePairMatcher
import isg.util.SequenceFilePairPattern
import isg.util.SequenceFilePairPatterns
import java.util.Arrays
import java.util.Properties
import mummer._
import mummer.coords.CoordsCoverageInProcessFunction
import gatk._
import gatk.UnifiedGenotyperCommandLineFunction.GenotypeLikelihoodsModel
import org.broadinstitute.sting.queue.function.QFunction
import scala.collection.JavaConversions._
import mummer.coords.CoordsDupsInProcessFunction
import mummer.snps.MumSnpToVcf
import org.apache.commons.io.FileUtils
import util.TypedProperties
import util.GenomicFileUtils
import org.broadinstitute.sting.commandline.Hidden

class ISGPipelineQScript extends QScript {

  @Hidden //deprecated, use input, output, ref instead
  @Input(doc="The analysis directory.", shortName="isg", exclusiveOf="I,O,R", required=false)
  var isgRoot: File = null
  
  @Input(doc="Directory containing input files (fastqs, fastas, bams, etc)", shortName="I", fullName="input_dir", exclusiveOf="isg")
  var inputDir: File = null
  
  @Input(doc="Directory where output files will be written", shortName="O", fullName="output_dir", exclusiveOf="isg")
  var outputDir: File = null
  
  @Input(doc="Reference sequence file.", shortName="R", fullName="reference_sequence", exclusiveOf="isg")
  var referenceSequence: File = null
  
  @Argument(doc="Path to GATK jar file", shortName="gatk", required=false)
  var gatkJarFile: File = null
  
  @Argument(doc ="Path to bwa executable.", shortName="bwa", required=false)
  var pathToBWA: String = null
  
  @Argument(doc="Path to mummer directory.", shortName="mummer", required=false)
  var pathToMummer: String = null
  
  @Argument(doc="Path to options file.", required=false)
  var optionsFile: File = null
  
  @Hidden
  @Argument(doc="Ploidy", required=false)
  var ploidy: java.lang.Integer = 1
  
  @Argument(doc="The minimum allele frequency of an alternative base needed to call a variant. Default value: 0.75", required=false)
  var minAF: Float = 0.75F
  
  @Argument(doc="The minimum Phred scaled probability needed to call a variant. Default value: 30", required=false)
  var minQual: java.lang.Integer = 30
  
  @Argument(doc="The minimum genotype quality needed to call a variant. Default value: 4", required=false)
  var minGQ: java.lang.Integer = 4
  
  @Argument(doc="The minimum depth of reads needed to call a variant. Default value: 3", required=false) 
  var minDP: java.lang.Integer = 3
  
  @Argument(doc="Do not fail when encountering base qualities that are too high "+
            "and that seemingly indicate a problem with the base quality encoding"+ 
            "of the BAM file", required=false)
  var allow_potentially_misencoded_quality_scores: Boolean = false
  
  @Hidden
  @Argument(doc="Include indels in final output.", required=false)
  var includeIndels: Boolean = false
  
  @Argument(doc="Run bwa mem algorithm.", required=false)
  var useBWAMem: Boolean = false
  
  @Argument(doc="Include pattern fields in output matrix.", required=false)
  var includePattern: Boolean = false
  
  var VCF_FILES: Set[File] = Set()
  var COV_FILES: Set[File] = Set()
  var DUPS_FILES: Set[File] = Set()
  
  def addVcf(vcf: File) { VCF_FILES += vcf }
  def addCov(cov: File) { COV_FILES += cov }
  def addDups(dups: File) { DUPS_FILES += dups }
  
  trait UNIVERSAL_GATK_ARGS extends GATKCommandLineFunction {
    this.jarFile = gatkJarFile;
    this.allowPotentiallyMisencodedQuals = allow_potentially_misencoded_quality_scores;
  }
  
  var fastaDir: File = _
  var mummerDir: File = _
  var readsDir: File = _
  var bamsDir: File = _
  var vcfDir: File = _
  var covDir: File = _
  var gbkDir: File = _
  var dupsDir: File = _
  var samplesoutputDir: File = _
  var refoutputDir: File = _
  var typedProperties: TypedProperties = new TypedProperties()
  
  def init() {
    
    if(isgRoot!=null){
      mkdir(isgRoot)
      
      fastaDir = mkdir(new File(isgRoot, "fastas"))
      mummerDir = mkdir(new File(isgRoot, "mummer"))
      readsDir = mkdir(new File(isgRoot, "reads"))
      bamsDir = mkdir(new File(isgRoot, "bams"))
      vcfDir = mkdir(new File(isgRoot, "vcf"))
      covDir = mkdir(new File(isgRoot, "coverage"))
      gbkDir = mkdir(new File(isgRoot, "genBank"))
      dupsDir = mkdir(new File(isgRoot, "dups"))
      referenceSequence = new File(isgRoot, "ref.fasta")
      outputDir = mkdir(new File(isgRoot, "out"))
    }else{
      mkdir(outputDir)
    }
    
    samplesoutputDir = mkdir(new File(outputDir, "samples"))
    refoutputDir = mkdir(new File(outputDir, "ref"))
    
    
    //remove *.done files from outputDir
    for(outFile : File <- outputDir.listFiles){
      if(outFile.getName.endsWith(".done")) outFile.delete
    }
    
    //load options file if specified
    if(optionsFile!=null){
      typedProperties.loadFromFile(optionsFile)
    }
    
  }
  
  override def add(functions: QFunction*) {
    //apply any user-defined arguments to function
    for(f : QFunction <- functions){
      typedProperties.applyToArgumentAnnotatedFieldsOfObjectUsingPrefix(f, f.analysisName+".")
      super.add(f)
    }
  }
  
  def mkdir(dir: File): File ={
    if(!dir.isDirectory){
      dir.mkdir
    }
    return dir
  }
  
  /**
   * In script, you create and then add() functions to the pipeline.
   */
  def script() {

    //initialize
    init
    initRef
    
    //create patterns to be used when detecting fastq pairs
    val sequencePatterns = new SequenceFilePairPatterns()
    sequencePatterns.addPattern(new SequenceFilePairPattern("(.*)_[0-9]+_([12])_sequence\\..*"))
    sequencePatterns.addPattern(new SequenceFilePairPattern("(.*)_[ATCG]+_L[0-9]+_R([12])_[0-9]+\\..*"))
    sequencePatterns.addPattern(new SequenceFilePairPattern("(.*)_S[0-9]+_L[0-9]+_R([12])_[0-9]+\\..*"))
    sequencePatterns.addPattern(new SequenceFilePairPattern("(.*)_R([12])[_\\.].*"))
    sequencePatterns.addPattern(new SequenceFilePairPattern("(.*)_([12])\\..*"))
    
    //create factories for detecting/creating input resources
    val inputResourceManagerBuilder = new InputResourceManagerBuilder()
    inputResourceManagerBuilder.addFactory(new VcfInputResourceFactory())
    inputResourceManagerBuilder.addFactory(new FastaInputResourceFactory())
    inputResourceManagerBuilder.addFactory(new BamInputResourceFactory())
    inputResourceManagerBuilder.addFactory(new FastqInputResourceFactory(sequencePatterns))
    
    //add input files
    if(inputDir != null){
      inputResourceManagerBuilder.addDir(inputDir)
    }else{
      inputResourceManagerBuilder.addDir(fastaDir)
      inputResourceManagerBuilder.addDir(readsDir)
      inputResourceManagerBuilder.addDir(bamsDir)
      inputResourceManagerBuilder.addDir(vcfDir)
    }
    val inputResourceManager: InputResourceManager = inputResourceManagerBuilder.build
    
    System.out.println(referenceSequence.getParent)
    
    //prepare input files for isg
    val visitor = new InputResourceVisitorImpl()
    inputResourceManager.applyAll(visitor)
    
    //add isg
    if(!inputResourceManager.samples.isEmpty){
      val all = new File(outputDir, "all.variants.txt")
      val allFasta = new File(outputDir, "all.variants.fasta")
      val allFinal = new File(outputDir, "all.variants.final.txt")
      val unique = new File(outputDir, "unique.variants.txt")
      val uniqueFasta = new File(outputDir, "unique.variants.fasta")
      val dups = new File(outputDir, "dups.variants.txt")
      val dupsFasta = new File(outputDir, "dups.variants.fasta")
    
      var sampleDirs: List[File] = List()      
      for(sample : String <- inputResourceManager.samples){
        sampleDirs = new File(samplesoutputDir, sample) :: sampleDirs
      }
      
      var programs = Array("CalculateMismatch", "DetermineStatus", "ClassifyMatrix")
      if(includePattern){
        programs +:= "CalculatePattern"
      }
      
      add(new ISG(referenceSequence, sampleDirs))
      add(new FormatForTree(all, allFasta))
      add(new BatchRunner(all, allFinal, programs))
      if(!DUPS_FILES.isEmpty){ 
        add(new FilterDups(allFinal, DUPS_FILES.toSeq, unique, dups))
        add(new FormatForTree(unique, uniqueFasta))
        add(new FormatForTree(dups, dupsFasta))
      }
    }
    
  }
  
  def initRef() {
    if(!referenceSequence.exists) return;
    
    //run mummer on reference
    val filename = stripExtension(referenceSequence)
    val prefix = refoutputDir.getPath + "/" + filename + "_" + filename
    val coords = new File(refoutputDir, filename + "_" + filename + ".coords")
    val refDups = new File(refoutputDir, filename + ".interval_list")

    add(new Nucmer(referenceSequence, referenceSequence, prefix, true, true))
    add(new CoordsDup(coords, referenceSequence, refDups))
    add(new CreateDict(referenceSequence))
    add(new CreateFAI(referenceSequence))
    if(pathToBWA!=null) add(new BWAIndex(referenceSequence))
    addDups(refDups)
  }
  
  class InputResourceVisitorImpl() extends InputResourceVisitor{
    
    //vcf
    def visit(resource: VcfInputResource) : Unit = {
      val sample = resource.sampleName
      val vcf = resource.resource
      
      val sampleDir = new File(samplesoutputDir, sample)
      if(!sampleDir.exists) sampleDir.mkdir
      
      //copy vcf to out directory
      val vcfCopy = new File(sampleDir, sample + ".vcf")
      FileUtils.copyFile(vcf, vcfCopy)
      addVcf(vcfCopy)
    }
    
    //bam
    def visit(resource: BamInputResource) : Unit = {
      val sample = resource.sampleName
      val bam = resource.resource
      callSnpsAndLoci(sample, bam)
    }
    
    //fasta
    def visit(resource: FastaInputResource) : Unit = {
      val sample = resource.sampleName
      val fasta = resource.resource
      mummer(sample, fasta)
    }
    
    //single-end fastq
    def visit(resource: FastqInputResource) : Unit = {
      val sample = resource.sampleName
      val reads = resource.resource
      callSnpsAndLoci(sample, bwa(sample, reads, null))
    }
    
    //paired-end fastq
    def visit(resource: FastqPairInputResource) : Unit = {
      val sample = resource.sampleName
      val pair = resource.resource
      val reads = pair.first
      val mates = pair.second
      callSnpsAndLoci(sample, bwa(sample, reads, mates))
    }
    
    //add mummer functions
    def mummer(sampleName: String, fasta: File) {
      if(pathToMummer==null) return

      val sampleDir = new File(samplesoutputDir, sampleName)
      if(!sampleDir.exists) sampleDir.mkdir
      
      val ref = stripExtension(referenceSequence)
      val prefix = sampleDir.getPath + "/" + sampleName + "_" + sampleName
      val selfCoords = new File(sampleDir, sampleName + "_" + sampleName + ".coords")
      val refCoords = new File(sampleDir, ref + "_" + sampleName + ".coords")
      val dups = new File(sampleDir, sampleName + ".dups")
      val cov = new File(sampleDir, sampleName + ".interval_list")
      val vcf = new File(sampleDir, sampleName + ".vcf")

      //find snps in both directions
      val refSnps = mummer(sampleDir, referenceSequence, fasta, false)
      val qrySnps = mummer(sampleDir, fasta, referenceSequence, true)
      add(new ToVcf(refSnps, qrySnps, vcf, sampleName, referenceSequence))

      //find dups
      add(new CoordsCov(refCoords, referenceSequence, cov))
      add(new Nucmer(fasta, fasta, prefix, true, true))
      add(new FindParalogs(selfCoords, refCoords, referenceSequence, dups))
      addVcf(vcf)
      addCov(cov)
      addDups(dups)
    }

    //add mummer alignment functions
    def mummer(sampleDir: File, ref: File, qry: File, sortByQry: Boolean): File = {
      val prefix = sampleDir.getPath + "/" + stripExtension(ref) + "_" + stripExtension(qry)
      val delta = new File(prefix + ".delta")
      val filtered = new File(prefix + ".filtered.delta")
      val snps = new File(prefix + ".snps")

      add(new Nucmer(ref, qry, prefix))
      add(new DeltaFilter(delta, filtered, true, true))
      add(new ShowSnps(filtered, snps, sortByQry))
      return snps
    }
    
    //add bwa functions
    def bwa(sample: String, reads: File, mates: File) : File = {
      val sampleDir = new File(samplesoutputDir, sample)
      if(!sampleDir.exists) sampleDir.mkdir
      
      val sampleTmpDir = new File(sampleDir, "tmp")
      if(!sampleTmpDir.exists) sampleTmpDir.mkdir
      
      val prefix = referenceSequence.getPath
      val sai1 = new File(sampleTmpDir, sample + "_1.sai")
      val sai2 = new File(sampleTmpDir, sample + "_2.sai")
      val sam = new File(sampleTmpDir, sample + ".sam")
      val rgBam = new File(sampleTmpDir, sample + ".rg.bam")
      val targetIntervals = new File(sampleTmpDir, sample + ".intervals")
      val realignedBam = new File(sampleTmpDir, sample + ".realigned.bam")
      val uniqueBam = new File(sampleDir, sample + ".bam")
      
      //bwa alignment of fastqs
      if(useBWAMem){
        add(new BWAMem(prefix, reads, mates, sam))
      }else{
        add(new BWAAln(prefix, reads, sai1))
        if(mates!=null){ //paired-end
          add(new BWAAln(prefix, mates, sai2))
          add(new BWASampe(prefix, sai1, sai2, reads, mates, sam))
        }else{ //single-end
          add(new BWASamse(prefix, sai1, reads, sam))
        }
      }
      
      //post-processing of bam
      add(new AddRG(sam, rgBam, sample))
      add(new RealignerTargetCreator(rgBam, referenceSequence, targetIntervals))
      add(new IndelRealigner(rgBam, targetIntervals, referenceSequence, realignedBam))
      add(new RMDups(realignedBam, uniqueBam))
      
      return uniqueBam
    }
    
    //add snp and loci calling functions
    def callSnpsAndLoci(sample: String, bam: File) {
      val sampleDir = new File(samplesoutputDir, sample)
      if(!sampleDir.exists) sampleDir.mkdir
      
      val vcf = new File(sampleDir, sample + ".vcf")
      val bed = new File(sampleDir, sample + ".bed")

      add(new UG(bam, referenceSequence, vcf))
      add(new CallableLoci(bam, referenceSequence, bed))
      addVcf(vcf)
      addCov(bed)
    }
    
  }
  
  def stripExtension(f: File): String = {
    val i = f.getName.lastIndexOf('.');
    if (i > 0 && i < f.getName.length() - 1)
      return f.getName.substring(0, i);
    else
      return f.getName;
  }
  
  def listFiles(dir: File, exts: Array[String]): Seq[File] = {
    val files = asScalaIterator[File](FileUtils.listFiles(dir, exts, false).iterator)
    var ret = List[File]()
    for(file <- files){
      ret +:= file
    }
    return ret
  }
  
  class BWAIndex(@Input fasta: File) extends BWAIndexCommandLineFunction {
    this.bwa = pathToBWA
    this.fastaFile = fasta
  }
  
  class BWAAln(prfx: String, fastq: File, sai: File, intermediate: Boolean = true) extends BWAAlnCommandLineFunction {
    this.bwa = pathToBWA
    this.prefix = prfx
    this.saiFile = sai
    this.fastqFile = fastq
    this.isIntermediate = intermediate
  }
  
  class BWAMem(prfx: String, reads: File, mates: File, sam: File) extends BWAMemCommandLineFunction {
    this.bwa = pathToBWA
    this.prefix = prfx
    this.readsFile = reads
    this.matesFile = mates
    this.samFile = sam
    this.M = true
    this.isIntermediate = true
  }
  
  class BWASampe(prfx: String, @Input saiFile1: File, @Input saiFile2: File, @Input fqFile1: File, @Input fqFile2: File, @Output samFile: File, intermediate: Boolean = true) extends BWASampeCommandLineFunction {
    this.bwa = pathToBWA
    this.prefix = prfx
    this.sai1 = saiFile1
    this.sai2 = saiFile2
    this.fq1 = fqFile1
    this.fq2 = fqFile2
    this.sam = samFile
    this.isIntermediate = intermediate
  }
  
  class BWASamse(prfx: String, @Input saiFile: File, @Input fqFile: File, @Output samFile: File, intermediate: Boolean = true) extends BWASamseCommandLineFunction {
    this.bwa = pathToBWA
    this.prefix = prfx
    this.sai = saiFile
    this.fq = fqFile
    this.sam = samFile
    this.isIntermediate = intermediate
  }
  
  class RMDups(@Input in: File, @Output out: File, RM_DUPS: Boolean = true) extends MarkDuplicates {
    this.input = List(in)
    this.output = out
    this.REMOVE_DUPLICATES = RM_DUPS
    this.outputIndex = swapExt(out.getParent, out, ".bam", ".bai")
  }
  
  class AddRG(@Input in: File, @Output out: File, SM: String, ID: String = "1", LB: String = ".", PL: String = ".", PU: String = ".", intermediate: Boolean = true) extends AddOrReplaceReadGroups {
    this.input = List(in)
    this.output = out
    this.RGSM = SM
    this.RGID = ID
    this.RGLB = LB
    this.RGPU = PU
    this.RGPL = PL
    this.createIndex = true
    this.isIntermediate = intermediate
    this.outputIndex = swapExt(out.getParent, out, ".bam", ".bai")
  }
  
  class Nucmer(inRef: File, inQry: File, prfx: String, mm: Boolean = false, ns: Boolean = false) extends NucmerCommandLineFunction {
    this.mummerDir = new File(pathToMummer)
    this.refFasta = inRef
    this.qryFasta = inQry
    this.prefix = prfx
    this.coords = true
    this.maxmatch = mm
    this.nosimplify = ns
  }
  
  class DeltaFilter(inDeltaFile: File, outDeltaFile: File, ref: Boolean, qry: Boolean) extends DeltaFilterCommandLineFunction {
    this.mummerDir = new File(pathToMummer)
    this.inDelta = inDeltaFile
    this.outDelta = outDeltaFile
    this.r = ref
    this.q = qry
  }
  
  class ShowSnps(delta: File, snps: File, sortByQry: Boolean) extends ShowSnpsCommandLineFunction {
    this.mummerDir = new File(pathToMummer)
    this.deltaFile = delta
    this.snpsFile = snps
    this.sortByQuery = sortByQry
    this.showAmbiguous = true
    this.showIndels = true
  }
  
  class ToVcf(refSnps: File, qrySnps: File, out: File, sample: String, ref: File) extends MumSnpToVcf {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    this.refSnpsFile = refSnps
    this.querySnpsFile = qrySnps
    this.output = out
    this.sampleName = sample
    this.referenceSequence = ref
  }
  
  class CoordsCov(coords: File, ref: File, out: File) extends CoordsCoverageInProcessFunction {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    this.coordsFile = coords
    this.referenceSequence = ref
    this.outFile = out
  }
  
  class CoordsDup(coords: File, ref: File, out: File) extends CoordsDupsInProcessFunction {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    this.coordsFile = coords
    this.referenceSequence = ref
    this.outFile = out
  }
  
  class RealignerTargetCreator(inBam: File, ref: File, outTargetIntervals: File) extends RealignerTargetCreatorCommandLineFunction with UNIVERSAL_GATK_ARGS {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    @Input val fai: File = swapExt(ref.getParent, ref, ".fasta", ".fasta.fai")
    this.inputFile = inBam 
    this.referenceFile = ref
    this.out = outTargetIntervals
    this.isIntermediate = true;
  }
  
  class IndelRealigner(inBam: File, inTargetIntervals: File, ref: File, outBam: File) extends IndelRealignerCommandLineFunction with UNIVERSAL_GATK_ARGS {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    @Input val fai: File = swapExt(ref.getParent, ref, ".fasta", ".fasta.fai")
    this.inputFile = inBam 
    this.referenceFile = ref
    this.out = outBam
    this.targetIntervals = inTargetIntervals
    this.isIntermediate = true
  }
  
  class UG(bam: File, ref: File, outVCF: File) extends UnifiedGenotyperCommandLineFunction with UNIVERSAL_GATK_ARGS {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    this.inputFile = bam
    this.referenceFile = ref
    this.out = outVCF
    this.standard_min_confidence_threshold_for_calling = 0
    this.standard_min_confidence_threshold_for_emitting = 0
    this.genotype_likelihoods_model = GenotypeLikelihoodsModel.BOTH
  }
  
  class CallableLoci(bam: File, ref: File, outBED: File) extends CallableLociCommandLineFunction with UNIVERSAL_GATK_ARGS {
    @Input val dict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    this.inputFile = bam
    this.referenceFile = ref
    this.out = outBED
    this.summary = swapExt(outBED.getParent, outBED, ".bed", ".summary")
  }
  
  class CreateDict (@Input ref: File) extends JavaCommandLineFunction {
    analysisName = "createSequenceDictionary"
    javaMainClass = "net.sf.picard.sam.CreateSequenceDictionary"
    @Output val outDict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    override def commandLine = super.commandLine + required("REFERENCE=" + ref) + required("OUTPUT=" + outDict)
  }
  
  class CreateFAI (@Input ref: File) extends JavaCommandLineFunction {
    analysisName = "createFastaIndex"
    javaMainClass = "isg.util.CreateFastaIndex"
    @Output val outFai: File = swapExt(ref.getParent, ref, ".fasta", ".fasta.fai")
    override def commandLine = super.commandLine + required("R=" + ref)
  }
  
  class ISG(@Input ref: File, sampleDirs: List[String]) extends JavaCommandLineFunction {
    analysisName = "isg"
    javaMainClass = "isg.ISG2"
    @Input var vcfFiles: Seq[File] = VCF_FILES.toSeq
    @Input(required = false) var covFiles: Seq[File] = COV_FILES.toSeq
    @Output val allOut: File = new File(outputDir, "all.variants.txt")
    
    override def commandLine = super.commandLine + 
      repeat("SAMPLE_DIR=", sampleDirs, spaceSeparated=false) +
      required("OUT_DIR="+outputDir) +
      required("REF="+ref) +
      optional("PLOIDY="+ploidy) +
      optional("MIN_AF="+minAF) +
      optional("MIN_QUAL="+minQual) +
      optional("MIN_GQ="+minGQ) +
      optional("MIN_DP="+minDP) +
      optional("INDEL="+includeIndels)
  }
  
  class FilterDups(@Input inMatrix: File, @Input inFilter: Seq[File], @Output unique: File, @Output dups: File) extends JavaCommandLineFunction {
    analysisName = "filterMatrix"
    javaMainClass = "isg.tools.FilterMatrix"
    
    override def commandLine = super.commandLine + 
      required("INPUT="+inMatrix) + 
      repeat("FILTER=", inFilter, spaceSeparated=false) +
      required("INCLUSIVE_OUT="+dups) + 
      required("EXCLUSIVE_OUT="+unique) + 
      required("REFERENCE_SEQUENCE="+referenceSequence)
  }
  
  class BatchRunner(@Input in: File, @Output out: File, programs: Seq[String]) extends JavaCommandLineFunction {
    analysisName = "isgToolsBatchRunner"
    javaMainClass = "isg.tools.ISGToolsBatchRunner"
    
    override def commandLine = super.commandLine + 
      required("INPUT="+in) + 
      required("OUTPUT="+out) +
      required("REFERENCE_SEQUENCE="+referenceSequence) + 
      required("GBK_DIR="+gbkDir) +
      repeat("PROGRAM=", programs, spaceSeparated=false)
  }
  
  class FindParalogs(@Input selfCoords: File, @Input refCoords: File, @Input ref: File, @Output out: File) extends JavaCommandLineFunction {
    @Input val inDict: File = swapExt(ref.getParent, ref, ".fasta", ".dict")
    @Input val inFai: File = swapExt(ref.getParent, ref, ".fasta", ".fasta.fai")
    analysisName = "findParalogs"
    javaMainClass = "isg.tools.FindParalogs"
    
    override def commandLine = super.commandLine + 
      required("SELF_COORDS="+selfCoords) + 
      required("REF_COORDS="+refCoords) +
      required("REFERENCE_SEQUENCE="+ref) + 
      required("OUTPUT="+out)
  }
  
  class FormatForTree(@Input in: File, @Output out: File) extends JavaCommandLineFunction {
    analysisName = "FormatForTree"
    javaMainClass = "isg.tools.FormatForTree"
    
    override def commandLine = super.commandLine + 
      required("INPUT="+in) +  
      required("OUTPUT="+out)
  }
  
}
