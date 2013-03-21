/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tgen.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import net.sf.samtools.SAMSequenceDictionary;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.variantcontext.Allele;
import org.broadinstitute.sting.utils.variantcontext.Genotype;
import org.broadinstitute.sting.utils.variantcontext.GenotypeBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContext;
import org.broadinstitute.sting.utils.variantcontext.VariantContextBuilder;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils.FilteredRecordMergeType;
import org.broadinstitute.sting.utils.variantcontext.VariantContextUtils.GenotypeMergeType;
import org.tgen.commons.vcf.VCFReader;
import org.tgen.commons.vcf.VariantContextIterator;

/**
 *
 * @author jbeckstrom
 */
public class VariantContextMergingIterator implements Iterator<VariantContext> {

    private final PriorityQueue<ComparableVariantContextIterator> pq;
    private final Collection<VCFReader> readers;
    private final VariantContextComparator comparator;
    private final GenomeLocParser genomeLocParser;
    private final Set<String> genotypeSamples = new HashSet<String>();
    private boolean initialized = false;

    public VariantContextMergingIterator(Collection<VCFReader> readers, SAMSequenceDictionary seqDict) {
        this.comparator = new VariantContextComparator();
        this.readers = readers;
        this.genomeLocParser = new GenomeLocParser(seqDict);

        this.pq = new PriorityQueue<ComparableVariantContextIterator>(readers.size());
    }

    private void startIterationIfRequired() {
        if (initialized) {
            return;
        }
        for (final VCFReader reader : readers) {
            addIfNotEmpty(new ComparableVariantContextIterator(new VariantContextIterator(reader), comparator));
            genotypeSamples.addAll(reader.getHeader().getGenotypeSamples());
        }
        initialized = true;
    }

    /**
     * Adds iterator to priority queue. If the iterator has more records it is added
     * otherwise it is closed and not added.
     */
    private void addIfNotEmpty(final ComparableVariantContextIterator iterator) {
        if (iterator.hasNext()) {
            pq.offer(iterator);
        } else {
            iterator.close();
        }
    }
    
    private void addIfNotEmpty(final Collection<ComparableVariantContextIterator> iterators) {
        for(ComparableVariantContextIterator iterator: iterators){
            addIfNotEmpty(iterator);
        }
    }
    
    /**
     * Adds no call records to list. This is a workaround for the diploid assumption
     * made by the merging routine.
     */
    private void addNoCalls(final Collection<VariantContext> records) {
        final VariantContext vc = records.iterator().next(); //grab the first vc to use as a reference
        final Set<String> uniqueSampleNames = getUniqueSampleNames(records);
        for(String sampleName: genotypeSamples){
            if(!uniqueSampleNames.contains(sampleName)){
                VariantContextBuilder vcb = new VariantContextBuilder(vc);
                vcb = vcb.genotypes(createNoCallGenotypes(sampleName, vc.getGenotypes().size()));
                records.add(vcb.make());
            }
        }
    }
    
    private List<Genotype> createNoCallGenotypes(final String sampleName, final int num){
        final List<Genotype> ret = new ArrayList<Genotype>();
        for(int i=0; i<num; i++){
            ret.add(new GenotypeBuilder(sampleName, Arrays.asList(Allele.NO_CALL)).make());
        }
        return ret;
    }
    
    private Set<String> getUniqueSampleNames(final Collection<VariantContext> records){
        final Set<String> ret = new HashSet<String>();
        for(VariantContext vc: records){
            ret.addAll(vc.getGenotypes().getSampleNames());
        }
        return ret;
    }

    public boolean hasNext() {
        startIterationIfRequired();
        return !this.pq.isEmpty();
    }

    public VariantContext next() {
        startIterationIfRequired();

        final Collection<VariantContext> recordsToMerge = new ArrayList<VariantContext>();
        final Collection<ComparableVariantContextIterator> itersToAdd = new ArrayList<ComparableVariantContextIterator>();

        final ComparableVariantContextIterator iterator = this.pq.poll();
        final VariantContext record = iterator.next();

        recordsToMerge.add(record);
        itersToAdd.add(iterator);

        //find records that itersect first record polled to merge later
        ComparableVariantContextIterator iterator2 = null;
        while ((iterator2 = pq.peek()) != null) {
            if (comparator.compare(record, iterator2.peek()) == 0) {
                pq.poll();
                recordsToMerge.add(iterator2.next());
                itersToAdd.add(iterator2);
            }else{
                break;
            }
        }

        addNoCalls(recordsToMerge);
        addIfNotEmpty(itersToAdd);
        
        return VariantContextUtils.simpleMerge(
                genomeLocParser, 
                recordsToMerge, 
                null,  //priorityListOfVCs
                FilteredRecordMergeType.KEEP_IF_ANY_UNFILTERED, 
                GenotypeMergeType.REQUIRE_UNIQUE, 
                false,  //annotate origin 
                false,  //print messages
                null,   //setKey 
                false,  //filteredAreUncalled 
                false); //mergeInfoWithMaxAC
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class VariantContextComparator implements Comparator<VariantContext> {

        public int compare(VariantContext t, VariantContext t1) {
            int i = genomeLocParser.getContigIndex(t.getChr());
            int i1 = genomeLocParser.getContigIndex(t1.getChr());
            if(i==i1){
                return t.getStart() - t1.getStart();
            }else if(i<i1){
                return -1;
            }else{
                return 1;
            }
        }
    }
    
}
