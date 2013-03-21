package org.tgen.commons.gff;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author jpearson
 * @version $Id: GffRecord.java,v 1.1 2010/09/09 22:42:34 jbeckstr Exp $
 *
 * Data container class for records from SOLiD GFF format sequence 
 * alignment files.  GFF is a tab-separated text file with unix-style
 * line endings and the following fields of which the last two are 
 * optional:
 * 
 *      Fieldname      Example value
 *  1.  seqname        1231_644_1328_F3
 *  2.  source         solid
 *  3.  feature        read
 *  4.  start          97
 *  5.  end            121
 *  6.  score          13.5
 *  7.  strand         -
 *  8.  frame          .
 *  9.  [attributes]   b=TAGGGTTAGGGTTGGGTTAGGGTTA;
 *                     c=AAA;
 *                     g=T320010320010100103000103;
 *                     i=1;
 *                     p=1.000;
 *                     q=23,28,27,20,17,12,24,16,20,8,13,26,28,2
 *                       4,13,13,27,14,19,4,23,16,19,9,14;
 *                     r=20_2;
 *                     s=a20;
 *                     u=0,1
 *  10. [comments]
 */

public class GffRecord {
	private String   originalLine; // original line
    private String   seqname;      // read ID
    private String   source;       // should always be "solid"
    private String   feature;      // should always be "read"
    private int      start;        // start position of mapping to reference
    private int      end;          // end position of mapping to reference
    private double   score;        // quality of mapping
    private String   strand;       // - or +
    private String   frame;        // 1,2,3,.
    private String   attribStr;    // this is the gold!
    private String   comments;     // comments (seldom present)
    private HashMap<String, String>  attributes;  // deconstruct attribStr
    

    /**
     * Constructor 0
     */
    public GffRecord() {
    	originalLine = "";
        seqname     = "";
        source      = "";
        feature     = "";
        start       = 0;
        end         = 0;
        score       = 0.0;
        strand      = "";
        frame       = "";
        attribStr   = "";
        comments    = "";
        attributes  = new HashMap<String, String>();
    }

    /**
     * Constructor 1
     * @param textRecord text GFF Record typically read from GFF file
     */
    public GffRecord( String textRecord ) {
    	this(); // call constructor 0
    	originalLine = textRecord;
    	String[] fields = textRecord.split( "\t" );
        // To-Do: should throw an error if less than 8 fields
        seqname     = fields[0];
        source      = fields[1];
        feature     = fields[2];
        start       = Integer.parseInt( fields[3] );
        end         = Integer.parseInt( fields[4] );
        score       = Double.parseDouble( fields[5] );
        strand      = fields[6];
        frame       = fields[7];

         // Cope with the optional attribute field
		if (fields.length > 8) {
			attribStr = fields[8];
			String[] tmpattribs = attribStr.split( ";" );
            for ( int i=0; i < tmpattribs.length; i++ ) {
            	String[] attrFields = tmpattribs[i].split("=");
                attributes.put( attrFields[0], attrFields[1] );
            }
		}
		
		// And comments is also optional
		if (fields.length > 9) {
			comments = fields[9];
		}
    }

    /**
     * Return a StringBuffer containing a multi-line string representation
     * of the record, one record attribute per line.
     * @return StringBuffer containing record as String
     */
    public StringBuffer asString() {
        StringBuffer buffer = new StringBuffer("");
        buffer.append( "seqname      " + seqname + "\n" );
        buffer.append( "source       " + source + "\n" );
        buffer.append( "feature      " + feature + "\n" );
        buffer.append( "start        " + start + "\n" );
        buffer.append( "end          " + end + "\n" );
        buffer.append( "score        " + score + "\n" );
        buffer.append( "strand       " + strand + "\n" );
        buffer.append( "frame        " + frame + "\n" );
        buffer.append( "attributes   " + attribStr + "\n" );
        buffer.append( "comments     " + comments + "\n" );
        buffer.append( "attributes broken out:\n" );

        // Use entrySet view (p681 Core Java vol 1)
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
        	String key = entry.getKey();
        	String val = entry.getValue();
            buffer.append( "             " + key + "=" + val + "\n" );
        }

        return( buffer );
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer("");
        buffer.append( seqname + "\t" );
        buffer.append( source + "\t" );
        buffer.append( feature + "\t" );
        buffer.append( start + "\t" );
        buffer.append( end + "\t" );
        buffer.append( score + "\t" );
        buffer.append( strand + "\t" );
        buffer.append( frame + "\t" );

        // Use entrySet view (p681 Core Java vol 1)
        for (Map.Entry<String, String> entry : attributes.entrySet()) {
        	String key = entry.getKey();
        	String val = entry.getValue();
            buffer.append( key + "=" + val + ";" );
        }

        return( buffer.toString() );
    }

	public String getOriginalLine() {
		return originalLine;
	}
	public String getSeqname() {
		return seqname;
	}
	public void setSeqname(String seqname) {
		this.seqname = seqname;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getFeature() {
		return feature;
	}
	public void setFeature(String feature) {
		this.feature = feature;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getStrand() {
		return strand;
	}
	public void setStrand(String strand) {
		this.strand = strand;
	}
	public String getFrame() {
		return frame;
	}
	public void setFrame(String frame) {
		this.frame = frame;
	}
	public String getAttribStr() {
		return attribStr;
	}
	public void setAttribstr(String attribStr) {
		this.attribStr = attribStr;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	public void setAttribute(String key, String value) {
		this.attributes.put( key, value );
	}
	public String getAttribute(String key) {
		return this.attributes.get( key );
	}
	
	public Set<String> getAttributeKeys(){
		return this.attributes.keySet();
	}
	
	/**
	 * Returns whether or not the attribute exists
	 * 
	 * @param key
	 *            Name of attribute to be checked
	 *            
	 * @return boolean
	 */
	public boolean containsAttribute(String key){
		return this.attributes.containsKey(key);
	}

}
