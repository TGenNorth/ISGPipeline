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
import isg.util.SequenceFilePair
import isg.util.SequenceFilePairMatcher
import isg.util.SequenceFilePairPattern
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

class ISGPipelineQScript extends QScript {

  @Input(doc="The analysis directory.", shortName="isg")
  var isgRoot: File = null
  
  @Argument(doc="Path to GATK jar file", shortName="gatk", required=false)
  var gatkJarFile: File = null
  
  @Argument(doc="Path to bwa executable.", shortName="bwa", required=false)
  var pathToBWA: String = null
  
  @Argument(doc="Path to mummer directory.", shortName="mummer", required=false)
  var pathToMummer: String = null
  
  @Argument(doc="Path to options file.", required=false)
  var optionsFile: File = null
  
  @Argument(doc="Ploidy", required=false)
  var ploidy: java.lang.Integer = 1
  
  @Argument(doc="The minimum allele frequency of an alternative base needed to call a variant.", required=false)
  var minAF: Float = 1.0F
  
  @Argument(doc="The minimum Phred scaled probability needed to call a variant.", required=false)
  var minQual: java.lang.Integer = 30
  
  @Argument(doc="The minimum genotype quality needed to call a variant.", required=false)
  var minGQ: java.lang.Integer = 4
  
  @Argument(doc="The minimum depth of reads needed to call a variant.", required=false) 
  var minDP: java.lang.Integer = 3
  
  @Argument(doc="Do not fail when encountering base qualities that are too high "+
            "and that seemingly indicate a problem with the base quality encoding"+ 
            "of the BAM file", required=false)
  var allow_potentially_misencoded_quality_scores: Boolean = false
  
  @Argument(doc="Include indels in final output.", required=false)
  var includeIndels: Boolean = false
  
  val PATTERNS: java.util.List[SequenceFilePairPattern] = 
    Arrays.asList(new SequenceFilePairPattern("(.*)_[0-9]+_([0-9])_sequence\\..*"),
                new SequenceFilePairPattern("(.*)_[ATCG]+_L[0-9]+_R([0-9])_[0-9]+\\..*"),
                new SequenceFilePairPattern("(.*)_S[0-9]+_L[0-9]+_R([0-9])_[0-9]+\\..*"),
                new SequenceFilePairPattern("(.*)_R([0-9])_[0-9]+\\..*"),
                new SequenceFilePairPattern("(.*)_R([0-9])\\..*"),
                new SequenceFilePairPattern("(.*)_([12])\\..*"));
  var VCF_FILES: Set[File] = Set()
  var COV_FILES: Set[File] = Set()
  var BAM_FILES: Set[File] = Set()
  var FASTA_FILES: Set[File] = Set()
  var DUPS_FILES: Set[File] = Set()
  
  def addVcf(vcf: File) { VCF_FILES += vcf }
  def addCov(cov: File) { COV_FILES += cov }
  def addBam(bam: File) { BAM_FILES += bam }
  def addFasta(fasta: File) { FASTA_FILES += fasta }
  def addDups(dups: File) { DUPS_FILES += dups }
  
  trait UNIVERSAL_GATK_ARGS extends GATKCommandLineFunction {
    this.jarFile = gatkJarFile;
    this.allowPotentiallyMisencodedQuals = allow_potentially_misencoded_quality_scores;
  }
  
  var referenceFile: File = _
  var fastaDir: File = _
  var mummerDir: File = _
  var readsDir: File = _
  var bamsDir: File = _
  var vcfDir: File = _
  var covDir: File = _
  var outDir: File = _
  var gbkDir: File = _
  var dupsDir: File = _
  var typedProperties: TypedProperties = new TypedProperties()
  
  def init() {
    if(!isgRoot.exists){
      isgRoot.mkdirs
    }
    referenceFile = new File(isgRoot, "ref.fasta")
    fastaDir = mkdir(new File(isgRoot, "fastas"))
    mummerDir = mkdir(new File(isgRoot, "mummer"))
    readsDir = mkdir(new File(isgRoot, "reads"))
    bamsDir = mkdir(new File(isgRoot, "bams"))
    vcfDir = mkdir(new File(isgRoot, "vcf"))
    covDir = mkdir(new File(isgRoot, "coverage"))
    outDir = mkdir(new File(isgRoot, "out"))
    gbkDir = mkdir(new File(isgRoot, "genBank"))
    dupsDir = mkdir(new File(isgRoot, "dups"))
    
    //add bams
    for(bam : File <- bamsDir.listFiles){
      if(bam.getName.endsWith(".bam")) addBam(bam)
    }
    
    //add vcfs
    for(vcf : File <- vcfDir.listFiles){
      if(vcf.getName.endsWith(".vcf")) addVcf(vcf)
    }
    
    //add fastas
    for(fasta : File <- fastaDir.listFiles){
      if(fasta.getName.endsWith(".fasta")) addFasta(fasta)
    }
    
    //remove *.done files from outDir
    for(outFile : File <- outDir.listFiles){
      if(outFile.getName.endsWith(".done")) outFile.delete
    }
    
    //load options file if specified
    if(optionsFile!=null){
      typedProperties.loadFromFile(optionsFile)
    }
    
    //create index and dictionary for reference file if exists
    if(referenceFile.exists){
      add(new CreateDict(referenceFile))
      add(new CreateFAI(referenceFile))
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

    init
    bwa
    mummer
    dups
    callSnpsAndCalculateCoverageOnBams
    callSnpsAndCalculateCoverageOnFastas
    
    if(!VCF_FILES.isEmpty){
      val all = new File(outDir, "all.variants.txt");
      val allFinal = new File(outDir, "all.variants.final.txt");
      
      add(new ISG(VCF_FILES.toSeq, COV_FILES.toSeq, referenceFile))
      add(new BatchRunner(all, allFinal))
      if(!DUPS_FILES.isEmpty) add(new FilterDups(allFinal, DUPS_FILES.toSeq))
    }
    
  }
  
  def dups() {
    if(pathToMummer==null) return
    
    val ref = stripExtension(referenceFile)
    val prefix = mummerDir.getPath + "/" + ref + "_" + ref
    val coords = new File(mummerDir, ref + "_" + ref + ".coords")
    val refDups = new File(dupsDir, ref + ".interval_list")
    
    add(new Nucmer(referenceFile, referenceFile, prefix, true, true))
    add(new CoordsDup(coords, referenceFile, refDups))
    addDups(refDups)
    
    val fastas = FileUtils.listFiles(fastaDir, Array("fasta"), false)
    for (fasta <- fastas){
      val sampleName = stripExtension(fasta)
      val prefix = mummerDir.getPath + "/" + sampleName + "_" + sampleName
      val selfCoords = new File(mummerDir, sampleName + "_" + sampleName + ".coords")
      val refCoords = new File(mummerDir, ref + "_" + sampleName + ".coords")
      val dups = new File(dupsDir, sampleName + ".interval_list")
      
      add(new Nucmer(fasta, fasta, prefix, true, true))
      add(new FindParalogs(selfCoords, refCoords, referenceFile, dups))
      addDups(dups)
    }
  }
  
  def mummer() {
    if(pathToMummer==null) return
    
    val fastas = FileUtils.listFiles(fastaDir, Array("fasta"), false)
    for (fasta <- fastas){
      
      val sampleName = stripExtension(fasta)
      val vcf = new File(vcfDir, sampleName + ".vcf")
      
      //find snps in both directions
      val refSnps = mummer(referenceFile, fasta, false)
      val qrySnps = mummer(fasta, referenceFile, true)
      add(new ToVcf(refSnps, qrySnps, vcf, sampleName))
      addVcf(vcf)
    }
  }
  
  /**
   * Add commands for mummer
   */
  def mummer(ref: File, qry: File, sortByQry: Boolean): File = {
    val prefix = mummerDir.getPath + "/" + stripExtension(ref) + "_" + stripExtension(qry)
    val delta = new File(prefix + ".delta")
    val filtered = new File(prefix + ".filtered.delta")
    val snps = new File(prefix + ".snps")
    
    add(new Nucmer(ref, qry, prefix))
    add(new DeltaFilter(delta, filtered, true, true))
    add(new ShowSnps(filtered, snps, sortByQry))
    return snps
  }
  
  /**
   * Add commands for a bwa run
   */
  def bwa() {
    if(pathToBWA==null) return;
    
    val prefix = referenceFile.getPath
    add(new BWAIndex(referenceFile))
    
    val pairMatcher = new SequenceFilePairMatcher(PATTERNS)
    val pairs = asScalaIterator[SequenceFilePair](pairMatcher.process(readsDir).iterator) 
    for (pair <- pairs){
      
      val sample = pair.getName
      val fastq1 = pair.getSeq1
      val fastq2 = pair.getSeq2
      val bamsTmpDir = new File(bamsDir, "tmp")
      bamsTmpDir.mkdir
      val sai1 = new File(bamsTmpDir, sample + "_1.sai")
      val sai2 = new File(bamsTmpDir, sample + "_2.sai")
      val sam = new File(bamsTmpDir, sample + ".sam")
      val rgBam = new File(bamsTmpDir, sample + ".rg.bam")
      val targetIntervals = new File(bamsTmpDir, sample + ".intervals")
      val realignedBam = new File(bamsTmpDir, sample + ".realigned.bam")
      val uniqueBam = new File(bamsDir, sample + ".bam")
      
      
      add(new BWAAln(prefix, fastq1, sai1))
      if(fastq2!=null){ //paired-end
        add(new BWAAln(prefix, fastq2, sai2))
        add(new BWASampe(prefix, sai1, sai2, fastq1, fastq2, sam))
      }else{ //single-end
        add(new BWASamse(prefix, sai1, fastq1, sam))
      }
      
      add(new AddRG(sam, rgBam, sample))
      add(new RealignerTargetCreator(rgBam, referenceFile, targetIntervals))
      add(new IndelRealigner(rgBam, targetIntervals, referenceFile, realignedBam))
      add(new RMDups(realignedBam, uniqueBam))
      addBam(uniqueBam)
    
    }
  }
  
  def callSnpsAndCalculateCoverageOnBams() {
    if(gatkJarFile==null) return
    for(bam : File <- BAM_FILES){
      val sample = stripExtension(bam)
      val vcf = new File(vcfDir, sample + ".vcf")
      val bed = new File(covDir, sample + ".bed")
      add(new UG(bam, referenceFile, vcf))
      add(new CallableLoci(bam, referenceFile, bed))
      addCov(bed)
      addVcf(vcf)
    }
  }
  
  def callSnpsAndCalculateCoverageOnFastas() {
    if(pathToMummer==null) return
    for(fasta : File <- FASTA_FILES){
      val sample = stripExtension(fasta)
      val coords = new File(mummerDir, stripExtension(referenceFile) + "_" + stripExtension(fasta) + ".coords")
      val cov = new File(covDir, sample + ".interval_list")
      add(new CoordsCov(coords, referenceFile, cov))
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
  
  class ToVcf(refSnps: File, qrySnps: File, out: File, sample: String) extends MumSnpToVcf {
    @Input val dict: File = swapExt(referenceFile.getParent, referenceFile, ".fasta", ".dict")
    this.refSnpsFile = refSnps
    this.querySnpsFile = qrySnps
    this.output = out
    this.sampleName = sample
    this.referenceSequence = referenceFile
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
  
  class ISG(@Input input: Seq[File], @Input covFiles: Seq[File], @Input ref: File) extends JavaCommandLineFunction {
    analysisName = "isg"
    javaMainClass = "isg.ISG2"
    @Output val allOut: File = new File(outDir, "all.variants.txt")
    var samples: List[String] = Nil

    override def freezeFieldValues() {
      super.freezeFieldValues()
      for(in : File <- input){
        samples = in.getName.stripSuffix(".vcf") :: samples
      }
    }
    
    override def commandLine = super.commandLine + 
      repeat("SAMPLE=", samples, spaceSeparated=false) +
      required("OUT_DIR="+outDir) +
      required("REF="+ref) +
      required("VCF_DIR="+vcfDir) +
      required("COV_DIR="+covDir) +
      optional("PLOIDY="+ploidy) +
      optional("MIN_AF="+minAF) +
      optional("MIN_QUAL="+minQual) +
      optional("MIN_GQ="+minGQ) +
      optional("MIN_DP="+minDP) +
      optional("INDEL="+includeIndels)
  }
  
  class FilterDups(@Input inMatrix: File, @Input inFilter: Seq[File]) extends JavaCommandLineFunction {
    analysisName = "filterMatrix"
    javaMainClass = "isg.tools.FilterMatrix"
    @Output val uniqueOut: File = new File(outDir, "unique.variants.txt")
    @Output val dupsOut: File = new File(outDir, "dups.variants.txt")
    
    override def commandLine = super.commandLine + 
      required("INPUT="+inMatrix) + 
      repeat("FILTER=", inFilter, spaceSeparated=false) +
      required("INCLUSIVE_OUT="+dupsOut) + 
      required("EXCLUSIVE_OUT="+uniqueOut) + 
      required("REFERENCE_SEQUENCE="+referenceFile)
  }
  
  class BatchRunner(@Input in: File, @Output out: File) extends JavaCommandLineFunction {
    analysisName = "isgToolsBatchRunner"
    javaMainClass = "isg.tools.ISGToolsBatchRunner"
    
    override def commandLine = super.commandLine + required("INPUT="+in) + required("OUTPUT="+out) +
      required("REFERENCE_SEQUENCE="+referenceFile) + required("GBK_DIR="+gbkDir)
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
  
}
