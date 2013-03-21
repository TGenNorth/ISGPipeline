package org.tgen.commons.feature;

import org.broad.tribble.Feature;
import org.broad.tribble.readers.LineReader;
import org.broadinstitute.sting.gatk.refdata.ReferenceDependentFeatureCodec;
import org.broadinstitute.sting.utils.GenomeLocParser;
import org.broadinstitute.sting.utils.exceptions.UserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.broad.tribble.AsciiFeatureCodec;

/**
 * Reads tab deliminated tabular text files
 *
 * <p>
 *     <ul>
 *     <li>Header: must begin with line HEADER or track (for IGV), followed by any number of column names,
 *     separated by whitespace.</li>
 *     <li>Comment lines starting with # are ignored</li>
 *     <li>Each non-header and non-comment line is split into parts by whitespace,
 *     and these parts are assigned as a map to their corresponding column name in the header.
 *     Note that the first element (corresponding to the HEADER column) must be a valid genome loc
 *     such as 1, 1:1 or 1:1-10, which is the position of the Table element on the genome.  TableCodec
 *     requires that there be one value for each column in the header, and no more, on all lines.</li>
 *     </ul>
 * </p>
 *
 * </p>
 *
 * <h2>File format example</h2>
 * <pre>
 *     HEADER a b c
 *     1:1  1   2   3
 *     1:2  4   5   6
 *     1:3  7   8   9
 * </pre>
 *
 * @author Mark DePristo
 * @since 2009
 */
public class TableCodec extends AsciiFeatureCodec<TableFeature> {

    final static protected String delimiterRegex = "\\t";
    final static protected String headerDelimiter = "HEADER";
    final static protected String igvHeaderDelimiter = "track";
    final static protected String defaultCommentDelimiter = "#";
    private final String commentDelimiter;
    private boolean firstLine = true;
    private final int chrCol, startCol, endCol;
    protected ArrayList<String> header = new ArrayList<String>();

    public TableCodec(int chrCol, int startCol, int endCol){
        this(chrCol, startCol, endCol, defaultCommentDelimiter);
    }
    
    public TableCodec(int chrCol, int startCol, int endCol, String commentDelimiter){
        super(TableFeature.class);
        this.chrCol = chrCol;
        this.startCol = startCol;
        this.endCol = endCol;
        this.commentDelimiter = commentDelimiter;
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
        return new TableFeature(split[chrCol], Integer.parseInt(split[startCol]), Integer.parseInt(split[endCol]), Arrays.asList(split), header);
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
