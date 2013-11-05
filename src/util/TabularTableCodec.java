package util;

import org.broad.tribble.Feature;
import org.broad.tribble.readers.LineReader;
import org.broadinstitute.sting.gatk.refdata.ReferenceDependentFeatureCodec;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.exceptions.UserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.broad.tribble.AsciiFeatureCodec;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.utils.codecs.table.TableFeature;

/**
 * Parsing logic for tabular text files
 */
public class TabularTableCodec extends AsciiFeatureCodec<TableFeature> {

    final static protected String delimiterRegex = "\\t";
    final static protected String headerDelimiter = "HEADER";
    final static protected String igvHeaderDelimiter = "track";
    final static protected String defaultCommentDelimiter = "#";
    private final String commentDelimiter;
    private boolean firstLine = true;
    private final int chrCol, startCol, endCol;
    protected ArrayList<String> header = new ArrayList<String>();
    private GenomeLocParser genomeLocParser;

    public TabularTableCodec(int chrCol, int startCol, int endCol){
        this(chrCol, startCol, endCol, defaultCommentDelimiter);
    }
    
    public TabularTableCodec(int chrCol, int startCol, int endCol, String commentDelimiter){
        super(TableFeature.class);
        this.chrCol = chrCol;
        this.startCol = startCol;
        this.endCol = endCol;
        this.commentDelimiter = commentDelimiter;
    }
    
    /**
     * Set the parser to use when resolving genetic data.
     * @param genomeLocParser The supplied parser.
     */
    public void setGenomeLocParser(GenomeLocParser genomeLocParser) {
        this.genomeLocParser =  genomeLocParser;
    }

    @Override
    public Feature decodeLoc(String line) {
        return decode(line);
    }

    public TableFeature decode(String line) {
        if (firstLine || line.startsWith(headerDelimiter) || line.startsWith(commentDelimiter) || line.startsWith(igvHeaderDelimiter)) {
            firstLine = false;
            return null;
        }
        String[] split = line.split(delimiterRegex);
        if (split.length < 1) {
            throw new IllegalArgumentException("TableCodec line = " + line + " doesn't appear to be a valid table format");
        }
        GenomeLoc genomeLoc = genomeLocParser.createGenomeLoc(split[chrCol], Integer.parseInt(split[startCol]), Integer.parseInt(split[endCol]));
        return new TableFeature(genomeLoc, Arrays.asList(split), header);
    }

    public Class<TableFeature> getFeatureType() {
        return TableFeature.class;
    }

    @Override
    public Object readHeader(LineReader reader) {
        try {
            String line = reader.readLine();
            if (header.size() > 0) {
                throw new IllegalStateException("Input table file seems to have two header lines.  The second is = " + line);
            }
            String spl[] = line.split(delimiterRegex);
            System.out.println(spl.length);
            header.addAll(Arrays.asList(spl));
        } catch (IOException e) {
            throw new UserException.MalformedFile("unable to parse header from TableCodec file", e);
        }
        return header;
    }

    @Override
    public boolean canDecode(final String potentialInput) {
        return false;
    }
}
