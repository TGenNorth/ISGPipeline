package org.tgen.commons.gff;

import java.io.*;
import java.util.*;
import java.util.regex.*;


/**
 * Class that reads plain-text SOLiD GFF sequence alignment files record by
 * record.
 * 
 * @author jpearson
 * @version $Id: GffReader.java,v 1.1 2010/09/09 22:42:34 jbeckstr Exp $
 * 
 */

public class GffReader{

	private int numRowsRead = 0;
	private int numFileCols = 0;
	private String fieldDelimiter = "";
	private File gffFile;
	private BufferedReader infile = null;
	private List<String> headers = new ArrayList<String>();

	/**
	 * @param file				the file to be loaded
	 */
	public GffReader(String file) throws Exception {
		init(new File(file), "\t");
	}

	/**
	 * @param file				the file to be loaded
	 */
	public GffReader(File file) throws Exception {
		init(file, "\t");
	}

	/**
	 * @param file 				the file to be loaded
	 * @param fieldDelimiter	field delimiter
	 */
	public GffReader(File file, String fieldDelimiter) throws Exception {
		init(file, fieldDelimiter);
	}

	/**
	 * @return field delimiter
	 */
	public String getFieldDelimiter() {
		return fieldDelimiter;
	}

	/**
	 * @return the number of rows read from the file
	 */
	public int getNumRowsRead() {
		return numRowsRead;
	}

	/**
	 * Returns the largest number of columns observed in any row read from the
	 * file so far.
	 * 
	 * @return number of columns in the file
	 */
	public int getNumFileCols() {
		return numFileCols;
	}

	/**
	 * Returns the canonical path of the file being read
	 * 
	 * @return the full path of the file to be read from
	 */
	public String getFilePath() throws Exception {
		return gffFile.getCanonicalPath();
	}

	/**
	 * Reads a row from the text file and returns it as a string
	 * 
	 * @return next row in file
	 */
	public GffRecord nextRecord() throws IOException {
		String record = null;
		if (this.infile.ready()) {
			record = infile.readLine();
			this.numRowsRead++;
			GffRecord gffRecord = new GffRecord(record);
			return gffRecord;
		} else {
			return null;
		}
	}
	

	/**
	 * Returns ready status for BufferedReader
	 * 
	 * @return boolean ready status
	 */
	public boolean ready() throws IOException {
		return this.infile.ready();
	}

	/**
	 * Open file and read off headers
	 * 
	 * @param filePath
	 *            Full path of the file to be loaded
	 * @param delimiter
	 *            File delimiter
	 */
	public void init( File myFile, String fieldDelimiter ) throws Exception {
		this.gffFile = myFile;
		this.fieldDelimiter = fieldDelimiter;
		infile = new BufferedReader(new FileReader( myFile ));
		// Read off the headers. Note the use of mark/reset so when we find
		// ourselves reading the first read record, we can back up an
		// effectively "unread" that record.
		while (infile.ready()) {
			infile.mark(2047); // mark before reading the line
			String line = infile.readLine();
			// If line does not start with "##" we have finished headers
			if (Pattern.matches("^##.*", line)) {
				headers.add(line);
			} else {
				infile.reset(); // rewind to before this record
				break;
			}

		}
	}
	
	public Iterator<GffRecord> iterator(){
		return new GffIterator();
	}
	
	class GffIterator implements Iterator<GffRecord>{
		
		GffRecord nextRecord = null;
		GffRecord currentRecord = null;
		
		public GffIterator(){
			advanceRecord();
		}
		
		private void advanceRecord(){
			try {
				currentRecord = GffReader.this.nextRecord();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				currentRecord = null;
				e.printStackTrace();
			}
		}
		
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return (currentRecord != null);
		}

		public GffRecord next() {
			// TODO Auto-generated method stub
			GffRecord ret = currentRecord;
			advanceRecord();
			if(currentRecord==null){
				nextRecord = null;
			}
			return ret;
		}

		public void remove() {
			// TODO Auto-generated method stub
			
		}
		
	}

	

}
