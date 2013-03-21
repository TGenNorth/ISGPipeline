package org.tgen.commons.feature;


import org.broad.tribble.Feature;
import org.broadinstitute.sting.utils.GenomeLoc;
import org.broadinstitute.sting.utils.Utils;

import java.util.List;

/**
 * A feature representing a single row out of a text table
 */
public class TableFeature implements Feature {
    // stores the values for the columns seperated out
    private final List<String> values;

    // if we have column names, we store them here
    private final List<String> keys;

    // our location
    private final String chr;
    private final int start, end;

    public TableFeature(String chr, int start, int end, List<String> values, List<String> keys) {
        this.values = values;
        this.keys = keys;
        this.chr = chr;
        this.start = start;
        this.end = end;
    }

    @Override
    public String getChr() {
        return chr;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return end;
    }

    public String getValue(int columnPosition) {
        if (columnPosition >= values.size()) throw new IllegalArgumentException("We only have " + values.size() + "columns, the requested column = " + columnPosition);
        return values.get(columnPosition);
    }

    public String toString() {
        return String.format("%s", Utils.join("\t",values));
    }

    public String get(String columnName) {
        int position = keys.indexOf(columnName);
        if (position < 0) throw new IllegalArgumentException("We don't have a column named " + columnName);
        return values.get(position);
    }

    public List<String> getAllValues() {
        return getValuesTo(values.size());
    }

    public List<String> getValuesTo(int columnPosition) {
        return values.subList(0,columnPosition);
    }

    public List<String> getHeader() {
        return keys;
    }
}
